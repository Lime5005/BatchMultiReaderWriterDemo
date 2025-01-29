package com.example.multireaderwriter.reader;

import com.example.multireaderwriter.pojo.Customer;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component("customerMultiResourceReader")
public class CustomerMultiResourceReader {

    @Autowired
    private FlatFileItemReader<Customer> customerReader;

    @Value("classpath:/file*.txt")
    private Resource[] fileResources;

    @Bean
    @StepScope
    public MultiResourceItemReader<Customer> myMultiResourceItemReader() {
        MultiResourceItemReader<Customer> multiResourceItemReader = new MultiResourceItemReader<>();
        multiResourceItemReader.setDelegate(customerReader);
        multiResourceItemReader.setResources(fileResources);
        return multiResourceItemReader;
    }

}
