package com.nontiwa.nonrecon.dailysalesreport;

import lombok.NonNull;
import lombok.SneakyThrows;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

//@Component
//public class SalesReportListener implements StepExecutionListener {
//
//    @Autowired
//    private SalesAggregator aggregator;
//
//    @Autowired
//    private JdbcBatchItemWriter<SalesSummary> writer;
//
//    @SneakyThrows
//    @Override
//    public ExitStatus afterStep(@NonNull StepExecution stepExecution) {
////        writer.write(new ArrayList<>(aggregator.getSummaries()));
//        writer.write(new Chunk<SalesSummary>(new ArrayList<>(aggregator.getSummaries())));
//        return ExitStatus.COMPLETED;
//    }
//}
