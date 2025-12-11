package com.nontiwa.nonrecon.dailysalesreport;

import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
public class SalesTransaction {

    private Long id;
    private String productId;
    private String storeId;
    private String paymentMethod;
    private int quantity;
    private BigDecimal amount;
    private OffsetDateTime timestamp;

}
