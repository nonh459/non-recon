package com.nontiwa.nonrecon.config;

import com.nontiwa.nonrecon.simpleetl.CustomerFieldSetMapper;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommonConfig {

    @Autowired
    ApplicationContext context;

    private static final Logger log = LoggerFactory.getLogger(CommonConfig.class);

    @PostConstruct
    public void logJobs() {
        context.getBeansOfType(Job.class)
                .forEach((name, job) -> System.out.println("Job: " + name));
    }

}
