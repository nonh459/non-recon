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
        String key = tx.getProductId();

        SalesSummary summary = summaryMap.computeIfAbsent(key, k ->
                new SalesSummary(k, 0, BigDecimal.ZERO, 0)
        );

        summary.setTotalQty(summary.getTotalQty() + tx.getQuantity());
        summary.setTotalAmount(summary.getTotalAmount().add(tx.getAmount()));
        summary.setSalesCount(summary.getSalesCount() + 1);

        return null; // Avoid writing here (we write at end)
    }

    public Collection<SalesSummary> getSummaries() {
        return summaryMap.values();
    }
}
