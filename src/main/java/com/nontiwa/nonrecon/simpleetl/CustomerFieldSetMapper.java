package com.nontiwa.nonrecon.simpleetl;

import lombok.NonNull;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.lang.NonNullApi;
import org.springframework.stereotype.Component;

@Component
public class CustomerFieldSetMapper implements FieldSetMapper<Customer> {

    @Override
    @NonNull
    public Customer mapFieldSet(FieldSet fieldSet) {
        return Customer.builder()
//                .id(fieldSet.readLong("id"))
                .firstName(fieldSet.readString("first_name"))
                .lastName(fieldSet.readString("last_name"))
                .email(fieldSet.readString("email"))
                .age(fieldSet.readInt("age"))
                .build();
    }
}
