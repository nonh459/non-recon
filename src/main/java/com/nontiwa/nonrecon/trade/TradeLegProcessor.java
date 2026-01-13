package com.nontiwa.nonrecon.trade;

import com.nontiwa.nonrecon.trade.model.TradeLeg;
import lombok.NonNull;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class TradeLegProcessor implements ItemProcessor<TradeLeg, TradeLeg> {

    @Override
    public TradeLeg process(@NonNull TradeLeg t) {
        return t;
    }
}
