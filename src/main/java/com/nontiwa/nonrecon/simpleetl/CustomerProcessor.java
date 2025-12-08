package com.nontiwa.nonrecon.simpleetl;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class CustomerProcessor implements ItemProcessor<Customer, Customer> {

    @Override
    public Customer process(Customer c) {

        // Example transformation: uppercase names
        c.setFirstName(c.getFirstName().toUpperCase());
        c.setLastName(c.getLastName().toUpperCase());
        c.setSourceFile(c.getResource().getFilename());

        // Example validation
        if (!c.getEmail().contains("@")) return null; // skip invalid

        return c;
    }
}
