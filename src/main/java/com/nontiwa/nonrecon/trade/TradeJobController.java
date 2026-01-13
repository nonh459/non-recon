package com.nontiwa.nonrecon.trade;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/jobs")
@RequiredArgsConstructor
public class TradeJobController {

    private final JobLauncher jobLauncher;
    private final Job importTradeJob;

    @PostMapping("/import-trades")
    public String runTradeJob() throws Exception {
        jobLauncher.run(
                importTradeJob,
                new JobParametersBuilder()
                        .addLong("run.id", System.currentTimeMillis())
                        .toJobParameters()
        );
        return "trade job import job started!";
    }
}
