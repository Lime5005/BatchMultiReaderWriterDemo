package com.example.springbatchmultireaderdemo.writer;


import com.example.springbatchmultireaderdemo.pojo.Customer;
import org.apache.commons.io.FilenameUtils;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamWriter;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("myMultiFileWriter")
public class MyMultiFileWriter implements ItemStreamWriter<Customer> {

    private final Map<String, FlatFileItemWriter<Customer>> delegates = new HashMap<>();

    private ExecutionContext executionContext;
    private boolean ignoreItemStream = false;

    public void setIgnoreItemStream(boolean ignoreItemStream) {
        this.ignoreItemStream = ignoreItemStream;
    }


    @Override
    public void write(List<? extends Customer> items) throws Exception {
        for (Customer customer : items) {
            if (customer == null) {
                continue;
            }
            getItemWriterForItem(customer).write(List.of(customer));
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
        return writer;
    }

    private FlatFileItemWriter<Customer> createMyItemWriter(String fileName) throws Exception {
        FlatFileItemWriter<Customer> writer = new FlatFileItemWriter<>();

        writer.setHeaderCallback(writer1 -> writer1.write("id,firstName,lastName,birthday"));

        String file = FilenameUtils.removeExtension(fileName);
        writer.setName(file + "Writer");
        FileSystemResource resource = new FileSystemResource("output/" + file + ".csv");
        resource.getFile().createNewFile();
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
