package com.nontiwa.nonrecon.dailysalesreport;

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
public class DailySalesReportController {

    private final JobLauncher jobLauncher;
    private final Job dailySalesReportJob;

    @PostMapping("/daily-sales-report")
    public String runDailySaleReportJob() throws Exception {
        jobLauncher.run(
                dailySalesReportJob,
                new JobParametersBuilder()
                        .addLong("run.id", System.currentTimeMillis())
                        .addString("reportDate", "2025-12-11")
                        .toJobParameters()
        );
        return "Daily Sales report job started!";
    }
}
