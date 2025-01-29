package com.example.multireaderwriter.job;

import com.example.multireaderwriter.listener.CustomerJobListener;
import com.example.multireaderwriter.pojo.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class MultiFileReaderWriterJob {

    private static final String JOB_NAME = "multiFileItemReaderWriterJob";
    private static final String STEP_NAME = "multiFileItemReaderWriterJob";
    private static final int CHUNK_SIZE = 20;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    private final MultiResourceItemReader<Customer> customerMultiResourceReader;

    private final ItemProcessor<Customer, Customer> customerItemProcessor;

    private final ItemWriter<? super Customer> CustomerMultiFileWriter;


    @Bean
    public Job multiFileItemReaderWriterJob() {
        return  new JobBuilder(JOB_NAME, jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(multiFileItemReaderWriterStep())
                .listener(new CustomerJobListener())
                .build();
    }

    @Bean
    public Step multiFileItemReaderWriterStep() {
        return new StepBuilder(STEP_NAME, jobRepository)
                .<Customer,Customer>chunk(CHUNK_SIZE, transactionManager)
                .reader(customerMultiResourceReader)
                .processor(customerItemProcessor)
                .writer(CustomerMultiFileWriter)
                .build();
    }

}
