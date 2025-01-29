package com.example.multireaderwriter.writer;


import com.example.multireaderwriter.pojo.Customer;
import lombok.Setter;
import org.apache.commons.io.FilenameUtils;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamWriter;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component("customerMultiFileWriter")
public class CustomerMultiFileWriter implements ItemStreamWriter<Customer> {

    private final Map<String, FlatFileItemWriter<Customer>> delegates = new HashMap<>();

    private ExecutionContext executionContext;

    @Setter
    private boolean ignoreItemStream = false; // An example for inserting a new logic

    @Override
    public void write(Chunk<? extends Customer> chunk) throws Exception {
        for (Customer customer : chunk.getItems()) {
            if (customer == null) {
                continue;
            }
            getItemWriterForItem(customer).write(Chunk.of(customer));
        }
    }

    @StepScope
    private FlatFileItemWriter<Customer> getItemWriterForItem(Customer customer) throws Exception {
        String fileName = customer.getInputSrcFileName();
        FlatFileItemWriter<Customer> writer = delegates.get(fileName);
        if (writer == null) {
            writer = createMyItemWriter(fileName);
            delegates.put(fileName, writer);
        }
        System.out.println("customer = " + customer);
        return writer;
    }

    private FlatFileItemWriter<Customer> createMyItemWriter(String fileName) throws Exception {
        FlatFileItemWriter<Customer> writer = new FlatFileItemWriter<>();

        writer.setHeaderCallback(writer1 -> writer1.write("id,firstName,lastName,birthday"));

        String file = FilenameUtils.removeExtension(fileName);
        writer.setName(file + "Writer");
        FileSystemResource resource = new FileSystemResource("output/" + file + ".csv");
        boolean fileCreated = resource.getFile().createNewFile();
        if (!fileCreated) {
            throw new IOException("Unable to create file at specified path. It already exists");
        }

        writer.setResource(resource);
        writer.open(executionContext);
        writer.setAppendAllowed(true);
        writer.setLineAggregator(new DelimitedLineAggregator<>() {
            {
                setDelimiter(",");
                setFieldExtractor(new BeanWrapperFieldExtractor<>() {
                    {
                        setNames(new String[]{"id", "firstName", "lastName", "birthday"});
                    }

                });
            }
        });
        writer.afterPropertiesSet();
        return writer;
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        this.executionContext = executionContext;
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        for (FlatFileItemWriter<Customer> writer : delegates.values()) {
            if (!ignoreItemStream && (writer != null)) {
                writer.update(executionContext);
            }
        }
    }

    @Override
    public void close() throws ItemStreamException {
        for (FlatFileItemWriter<Customer> writer : delegates.values()) {
            if (!ignoreItemStream && (writer != null)) {
                writer.close();
            }
        }
    }
}
