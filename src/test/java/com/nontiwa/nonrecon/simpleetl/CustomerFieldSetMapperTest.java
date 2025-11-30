package com.nontiwa.nonrecon.simpleetl;

import org.junit.jupiter.api.Test;
import org.springframework.batch.item.file.transform.DefaultFieldSet;
import org.springframework.batch.item.file.transform.FieldSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CustomerFieldSetMapperTest {

    private final CustomerFieldSetMapper mapper = new CustomerFieldSetMapper();

    @Test
    void testMapFieldSet() {
        // prepare input (simulated CSV row)
        String[] values = {"John", "Doe", "john@example.com", "25"};
        String[] names = {"first_name", "last_name", "email", "age"};

        FieldSet fieldSet = new DefaultFieldSet(values, names);

        // map
        Customer customer = mapper.mapFieldSet(fieldSet);

        // assertions
        assertNotNull(customer);
        assertEquals("John", customer.getFirstName());
        assertEquals("Doe", customer.getLastName());
        assertEquals("john@example.com", customer.getEmail());
        assertEquals(25, customer.getAge());
    }

}
