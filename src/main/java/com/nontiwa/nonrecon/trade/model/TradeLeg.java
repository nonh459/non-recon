package com.nontiwa.nonrecon.trade.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class TradeLeg {

    String legId;
    String tradeId;
    String instrument;
    BigDecimal price;
    Integer legQuantity;
}
