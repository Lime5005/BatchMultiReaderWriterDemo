package com.example.springbatchmultireaderdemo.writer;


import com.example.springbatchmultireaderdemo.pojo.Customer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("myMultiFileWriter")
public class MyMultiFileWriter implements ItemWriter<Customer> {
    @Override
    public void write(List<? extends Customer> customers) throws Exception {
        for (Customer customer : customers) {
            System.out.println(customer);
        }
    }
}
