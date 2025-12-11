package com.nontiwa.nonrecon.dailysalesreport;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class SalesSummary {

    private String productId;
    private int totalQty;
    private BigDecimal totalAmount;
    private int salesCount;

}
