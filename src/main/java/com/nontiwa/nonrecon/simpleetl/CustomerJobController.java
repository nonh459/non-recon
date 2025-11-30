package com.nontiwa.nonrecon.simpleetl;

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
public class CustomerJobController {

    private final JobLauncher jobLauncher;
    private final Job importCustomerJob;

    @PostMapping("/import-customers")
    public String runCustomerJob() throws Exception {
        jobLauncher.run(
                importCustomerJob,
                new JobParametersBuilder()
                        .addLong("run.id", System.currentTimeMillis())
                        .toJobParameters()
        );
        return "Customer import job started!";
    }
}
