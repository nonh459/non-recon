package com.nontiwa.nonrecon.trade;

import com.nontiwa.nonrecon.simpleetl.Customer;
import com.nontiwa.nonrecon.trade.model.TradeLeg;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class TradeLegFieldSetMapper implements FieldSetMapper<TradeLeg> {

    private static final Logger log = LoggerFactory.getLogger(TradeLegFieldSetMapper.class);

    @Override
    @NonNull
    public TradeLeg mapFieldSet(@NonNull FieldSet fieldSet) {
        try {
            return TradeLeg.builder()
    //                .id(fieldSet.readLong("id"))
                    .legId(fieldSet.readString("leg_id"))
                    .tradeId(fieldSet.readString("trade_id"))
                    .instrument(fieldSet.readString("instrument"))
                    .price(fieldSet.readBigDecimal("price"))
                    .legQuantity(fieldSet.readInt("leg_quantity"))
                    .build();
        } catch (Exception ex) {
            log.error("❌ Malformed record: {}", Arrays.toString(fieldSet.getValues()));
            log.error("Error: {}", ex.getMessage());
            throw ex; // Pass error to skip policy (if defined)
        }
    }
}
