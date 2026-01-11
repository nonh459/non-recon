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
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.PostgresPagingQueryProvider;
import org.springframework.batch.item.database.support.SqlServerPagingQueryProvider;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class DailySalesReportJobConfig {


    @Bean
    @StepScope
    public JdbcPagingItemReader<SalesTransaction> salesReader(
            DataSource dataSource,
            @Value("#{jobParameters['reportDate']}") String date) {

        JdbcPagingItemReader<SalesTransaction> reader = new JdbcPagingItemReader<>();
        reader.setDataSource(dataSource);
        reader.setPageSize(1000);     // tune based on DB & memory
        reader.setFetchSize(1000);
        reader.setRowMapper(new SalesTransactionRowMapper());

        // ---- WHERE parameters ----
        Map<String, Object> parameterValues = new HashMap<>();
        parameterValues.put("startDate", Timestamp.valueOf(date + " 00:00:00"));
        parameterValues.put("endDate", Timestamp.valueOf(date + " 23:59:59"));

        reader.setParameterValues(parameterValues);

        // ---- Paging query provider ----
        PostgresPagingQueryProvider queryProvider = getPostgresPagingQueryProvider();

        reader.setQueryProvider(queryProvider);

        return reader;
    }

    private static PostgresPagingQueryProvider getPostgresPagingQueryProvider() {
        PostgresPagingQueryProvider queryProvider = new PostgresPagingQueryProvider();

        queryProvider.setSelectClause("""
        SELECT id, product_id, store_id, payment_method,
               quantity, amount, timestamp
        """);

        queryProvider.setFromClause("""
        FROM recon_dev.sales_transaction
        """);

        queryProvider.setWhereClause("""
        WHERE timestamp >= :startDate
          AND timestamp < :endDate
        """);

        // REQUIRED for paging
        queryProvider.setSortKeys(Map.of("id", Order.ASCENDING));
        return queryProvider;
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
