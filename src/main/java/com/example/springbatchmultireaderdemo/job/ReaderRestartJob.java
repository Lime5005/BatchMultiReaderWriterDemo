package com.example.springbatchmultireaderdemo.job;

import com.example.springbatchmultireaderdemo.pojo.Customer;
import com.example.springbatchmultireaderdemo.reader.MyRestartReader;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBatchProcessing
public class ReaderRestartJob {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private MyRestartReader myRestartReader;

    @Autowired
    @Qualifier("myMultiFileWriter")
    private ItemWriter<? super Customer> multiFileWriter;


    @Bean
    public Job restartJob() {
        return jobBuilderFactory.get("restartJob")
                .start(restartStep())
                .build();
    }

    @Bean
    public Step restartStep() {
        return stepBuilderFactory.get("restartStep")
                .<Customer,Customer>chunk(3)
                .reader(myRestartReader)
                .writer(multiFileWriter)
                .build();
    }

}
