package com.example.springbatchmultireaderdemo.job;

import com.example.springbatchmultireaderdemo.listener.MyJobListener;
import com.example.springbatchmultireaderdemo.pojo.Customer;
import com.example.springbatchmultireaderdemo.reader.CustomerFlatFileItemReader;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

@Configuration
@EnableBatchProcessing
public class MultiFileReaderJob {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private CustomerFlatFileItemReader customerFlatFileItemReader;

    @Autowired
    @Qualifier("myMultiFileWriter")
    private ItemWriter<? super Customer> multiFileWriter;

    @Value("classpath:/file*.txt")
    private Resource[] fileResources;

    @Bean
    public Job multiFileItemReaderDemoJob() {
        return jobBuilderFactory.get("multiFileItemReaderDemoJob")
                .start(multiFileItemReaderDemoStep())
                .listener(new MyJobListener())
                .build();
    }

    @Bean
    public Step multiFileItemReaderDemoStep() {

        return stepBuilderFactory.get("multiFileItemReaderDemoStep")
                .<Customer,Customer>chunk(3)
                .reader(multiResourceItemReader())
                .writer(multiFileWriter)
                .build();
    }

    @Bean
    @StepScope
    public MultiResourceItemReader multiResourceItemReader() {
        MultiResourceItemReader multiResourceItemReader = new MultiResourceItemReader();
        multiResourceItemReader.setDelegate(myFlatFileItemReader());
        multiResourceItemReader.setResources(fileResources);
        return multiResourceItemReader;
    }


    @Bean
    public FlatFileItemReader<Customer> myFlatFileItemReader() {
        FlatFileItemReader<Customer> reader = new FlatFileItemReader<Customer>();
        reader.setResource(new ClassPathResource("customer.txt"));
        //reader.setLinesToSkip(1);
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setNames(new String[]{"id","fistName","lastName","birthday"});
        DefaultLineMapper<Customer> mapper = new DefaultLineMapper<>();
        mapper.setLineTokenizer(tokenizer);
        mapper.setFieldSetMapper(fieldSet -> {
            Customer customer = new Customer();
            customer.setId(fieldSet.readLong("id"));
            customer.setFirstName(fieldSet.readString("fistName"));
            customer.setLastName(fieldSet.readString("lastName"));
            customer.setBirthday(fieldSet.readString("birthday"));
            return customer;
        });
        mapper.afterPropertiesSet();
        reader.setLineMapper(mapper);
        return reader;
    }

}
