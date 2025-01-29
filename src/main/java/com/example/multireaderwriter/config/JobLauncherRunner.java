package com.example.multireaderwriter.config;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JobLauncherRunner implements CommandLineRunner {

    private final JobLauncher jobLauncher;
    private final Job myJob;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Starting job execution...");
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("timestamp", System.currentTimeMillis()) // Ensure uniqueness
                .toJobParameters();
        jobLauncher.run(myJob, jobParameters);
        System.out.println("Job execution finished.");
    }
}

