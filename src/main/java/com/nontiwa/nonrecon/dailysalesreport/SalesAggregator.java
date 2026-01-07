package com.nontiwa.nonrecon.dailysalesreport;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class SalesAggregator implements ItemProcessor<SalesTransaction, SalesSummary> {

    private final Map<String, SalesSummary> summaryMap = new HashMap<>();

    @Override
    public SalesSummary process(SalesTransaction tx) {

        System.out.println("SalesAggregator.process() called");
        return new SalesSummary(
                tx.getProductId(),
                tx.getQuantity(),
                tx.getAmount(),
                1
        );
    }

    public Collection<SalesSummary> getSummaries() {
        return summaryMap.values();
    }
}
