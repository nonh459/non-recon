package com.nontiwa.nonrecon.dailysalesreport;


import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.sql.Timestamp;

@Configuration
@RequiredArgsConstructor
public class DailySalesReportJobConfig {


    @Bean
    @StepScope
    public JdbcCursorItemReader<SalesTransaction> salesReader(
            DataSource dataSource,
            @Value("#{jobParameters['reportDate']}") String date) {

        JdbcCursorItemReader<SalesTransaction> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(dataSource);
        reader.setSql("""
        SELECT id, product_id, store_id, payment_method, quantity, amount, timestamp 
        FROM recon_dev.sales_transaction 
        WHERE DATE(timestamp) = ?
        """);

        reader.setPreparedStatementSetter(ps ->
                ps.setTimestamp(1, Timestamp.valueOf(date+" 00:00:00"))
        );
        reader.setRowMapper(new SalesTransactionRowMapper());
        return reader;
    }


//    @Bean
//    @StepScope
//    public ItemWriter<SalesSummary> summaryWriter(SalesAggregator aggregator,
//                                                  JdbcBatchItemWriter<SalesSummary> dbWriter) {
//
//        return items -> {
//            // ignore incoming items (processor returned null)
//        };
//    }


    @Bean
    public FlatFileItemWriter<SalesSummary> csvWriter() {
        System.out.println("FlatFileItemWriter() called");
        return new FlatFileItemWriterBuilder<SalesSummary>()
                .name("daily-sales-writer")
                .resource(new FileSystemResource("D:/data_backyard/sales/reports/daily_sales.csv"))
                .delimited()
                .names("productId", "totalQty", "totalAmount", "salesCount")
                .headerCallback(writer ->
                        writer.write("product_id,quantity,amount,sales_count"))
                .build();
    }

    @Bean
    public Job dailySalesReportJob(JobRepository repo,
                                   Step salesStep
                                   /*SalesReportListener listener*/) {
        return new JobBuilder("dailySalesReportJob", repo)
                .start(salesStep)
//                .listener(listener)
                .build();
    }

    @Bean
    public Step salesStep(JobRepository repo,
                          PlatformTransactionManager txManager,
                          ItemReader<SalesTransaction> reader,
                          SalesAggregator processor,
                          ItemWriter<SalesSummary> writer) {
        return new StepBuilder("salesStep", repo)
                .<SalesTransaction, SalesSummary>chunk(500, txManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

}
