package com.example.springbatchmultireaderdemo.reader;

import com.example.springbatchmultireaderdemo.pojo.Customer;
import org.springframework.batch.item.*;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component("myRestartReader")
public class MyRestartReader implements ItemStreamReader<Customer> {

    private final FlatFileItemReader<Customer> customerFlatFileItemReader = new FlatFileItemReader<>();
    private Long currentLine = 0L;
    private boolean restart = false;
    private ExecutionContext executionContext;


    public MyRestartReader() {

        customerFlatFileItemReader.setResource(new ClassPathResource("file2.txt"));
        customerFlatFileItemReader.setLinesToSkip(1);

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
        customerFlatFileItemReader.setLineMapper(mapper);
    }


    @Override
    public Customer read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        Customer customer = null;
        this.currentLine ++;

        // Restart from line before where is error last time
        if (restart) {
            customerFlatFileItemReader.setLinesToSkip(this.currentLine.intValue() - 1);
            restart = false;
            System.out.println("Start reading from line: " + this.currentLine);
        }
        customerFlatFileItemReader.open(this.executionContext);
        customer = customerFlatFileItemReader.read();

        if (customer != null && customer.getFirstName().equals("WrongName")) {
            throw new RuntimeException("Something went wrong. Customer id: " + customer.getId() + " is wrong name");
        }

        return customer;
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        this.executionContext = executionContext;
        // Second time restart
        if (executionContext.containsKey("currentLine")) {
            this.currentLine = executionContext.getLong("currentLine");
            this.restart = true;
        } else {
            // First time read
            this.currentLine = 0L;
            executionContext.put("currentLine", this.currentLine);
            System.out.println("Start reading from line " + this.currentLine + 1);
        }
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        // Update after each chunk: 3 lines
        executionContext.put("currentLine", this.currentLine);
        System.out.println("Current Line is " + this.currentLine);
    }

    @Override
    public void close() throws ItemStreamException {

    }

}
