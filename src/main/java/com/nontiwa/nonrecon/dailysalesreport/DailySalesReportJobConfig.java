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

@Configuration
@RequiredArgsConstructor
public class DailySalesReportJobConfig {

    private final PlatformTransactionManager transactionManager;

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
        WHERE DATE(timestamp) = :reportDate
    """);
        reader.setRowMapper(new SalesTransactionRowMapper());
        return reader;
    }

    @Bean
    @StepScope
    public ItemWriter<SalesSummary> summaryWriter(SalesAggregator aggregator,
                                                  JdbcBatchItemWriter<SalesSummary> dbWriter) {

        return items -> {
            // ignore incoming items (processor returned null)
        };
    }

    @Bean
    public FlatFileItemWriter<SalesSummary> csvWriter() {
        return new FlatFileItemWriterBuilder<SalesSummary>()
                .name("daily-sales-writer")
                .resource(new FileSystemResource("reports/daily_sales.csv"))
                .delimited()
                .names("productId", "totalQty", "totalAmount", "salesCount")
                .build();
    }

    @Bean
    public Job dailySalesReportJob(JobRepository repo,
                                   Step salesStep,
                                   SalesReportListener listener) {
        return new JobBuilder("dailySalesReportJob", repo)
                .start(salesStep)
                .listener(listener)
                .build();
    }

    @Bean
    public Step salesStep(JobRepository repo,
                          PlatformTransactionManager txManager,
                          ItemReader<SalesTransaction> reader,
                          SalesAggregator processor,
                          ItemWriter<SalesSummary> writer) {
        return new StepBuilder("salesStep", repo)
                .<SalesTransaction, SalesSummary>chunk(500, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }




}
