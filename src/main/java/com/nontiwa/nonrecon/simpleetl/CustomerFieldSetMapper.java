package com.nontiwa.nonrecon.simpleetl;

import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.lang.NonNullApi;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class CustomerFieldSetMapper implements FieldSetMapper<Customer> {

    private static final Logger log = LoggerFactory.getLogger(CustomerFieldSetMapper.class);

    @Override
    @NonNull
    public Customer mapFieldSet(FieldSet fieldSet) {
        try {
            return Customer.builder()
    //                .id(fieldSet.readLong("id"))
                    .firstName(fieldSet.readString("first_name"))
                    .lastName(fieldSet.readString("last_name"))
                    .email(fieldSet.readString("email"))
                    .age(fieldSet.readInt("age"))
                    .build();
        } catch (Exception ex) {
            log.error("❌ Malformed record: {}", Arrays.toString(fieldSet.getValues()));
            log.error("Error: {}", ex.getMessage());
            throw ex; // Pass error to skip policy (if defined)
        }
    }
}
