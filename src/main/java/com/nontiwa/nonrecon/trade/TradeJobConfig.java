package com.nontiwa.nonrecon.trade;

import com.nontiwa.nonrecon.trade.model.TradeLeg;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class TradeJobConfig {

    @Bean
    public FlatFileItemReader<TradeLeg> tradeLegReader(TradeLegFieldSetMapper fieldSetMapper) {
        return new FlatFileItemReaderBuilder<TradeLeg>()
                .name("trade-leg-reader")
                .resource(new FileSystemResource("D:/data_backyard/trade/inbound/trade_leg.csv"))
                .delimited()
                .delimiter(",")
                .names("leg_id","trade_id","instrument","price","leg_quantity")
                .fieldSetMapper(fieldSetMapper)
                .linesToSkip(1)  // skip CSV header
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<TradeLeg> tradeLegWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<TradeLeg>()
                .dataSource(dataSource)                   // ✅ mandatory
                .sql("""
                INSERT INTO recon_dev.trade_leg
                ("leg_id","trade_id","instrument","price","leg_quantity")
                VALUES (:legId, :tradeId, :instrument, :price, :legQuantity)
                ON CONFLICT ON CONSTRAINT trade_leg_pkey DO NOTHING
                """)                                       // ✅ mandatory
                .beanMapped()                              // ✅ mandatory
                .assertUpdates(false)   // 🔑 IMPORTANT
                .build();
    }

    @Bean
    public Step importTradeLegStep(JobRepository repo,
                                   FlatFileItemReader<TradeLeg> reader,
                                   JdbcBatchItemWriter<TradeLeg> writer,
                                   PlatformTransactionManager transactionManager,
                                   TradeLegProcessor processor) {

        return new StepBuilder("import-trade-leg-step", repo)
                .<TradeLeg, TradeLeg>chunk(50, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
//                .faultTolerant()
//                .skip(DuplicateKeyException.class)
//                .skipLimit(100)
                .build();
    }

    @Bean
    public Job importTradeJob(JobRepository repo, Step importTradeLegStep) {
        return new JobBuilder("import-trade-job", repo)
                .start(importTradeLegStep)
                .build();
    }

}
