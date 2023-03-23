package com.example.springbatchmultireaderdemo.reader;

import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;


@Component("myMultiItemRestartReader")
public class MyMultiItemRestartReader {
    private final MultiResourceItemReader multiResourceItemReader = new MultiResourceItemReader();


    public MyMultiItemRestartReader() {

        multiResourceItemReader.setDelegate(new FlatFileItemReader());
        multiResourceItemReader.setResources(new Resource[] { new FileSystemResource("classpath:/file*.txt") });
    }



}
