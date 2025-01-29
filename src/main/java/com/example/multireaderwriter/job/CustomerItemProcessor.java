package com.example.multireaderwriter.job;

import com.example.multireaderwriter.pojo.Customer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Slf4j
@Component("customerItemProcessor")
public class CustomerItemProcessor implements ItemProcessor<Customer, Customer> {
    @Override
    public Customer process(Customer customer) throws Exception {
        char c = customer.getFirstName().charAt(0);
        if (Character.isLowerCase(c)) {
            log.info("customer firstname with lowercase {}", customer.getFirstName());
            customer.setFirstName(Character.toUpperCase(c) + customer.getFirstName().substring(1));
        }
        return customer;
    }
}
