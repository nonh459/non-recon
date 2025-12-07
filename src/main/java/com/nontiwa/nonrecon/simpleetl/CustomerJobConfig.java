package com.nontiwa.nonrecon.simpleetl;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class CustomerJobConfig {

    private final CustomerProcessor processor;
    private final CustomerFieldSetMapper fieldSetMapper;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public FlatFileItemReader<Customer> customerReader() {
        return new FlatFileItemReaderBuilder<Customer>()
                .name("customer-reader")
                .resource(new ClassPathResource("customers.csv"))
                .delimited()
                .delimiter(",")
                .names("first_name","last_name","email","age")
                .fieldSetMapper(fieldSetMapper)
                .linesToSkip(1)  // skip CSV header
                .build();
    }

    @Bean
    public JpaItemWriter<Customer> customerWriter(EntityManagerFactory emf) {
        JpaItemWriter<Customer> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(emf);
        return writer;
    }

    @Bean
    public Step importCustomerStep(JobRepository repo,
                                   FlatFileItemReader<Customer> reader,
                                   JpaItemWriter<Customer> writer) {

        return new StepBuilder("import-customer-step", repo)
                .<Customer, Customer>chunk(50, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    public Job importCustomerJob(JobRepository repo, Step importCustomerStep) {
        return new JobBuilder("import-customer-job", repo)
                .start(importCustomerStep)
                .build();
    }
}
