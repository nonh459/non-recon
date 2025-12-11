package com.nontiwa.nonrecon.dailysalesreport;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;

public class SalesTransactionRowMapper implements RowMapper<SalesTransaction> {

    @Override
    public SalesTransaction mapRow(ResultSet rs, int rowNum) throws SQLException {

        SalesTransaction tx = new SalesTransaction();

        tx.setId(rs.getLong("id"));
        tx.setProductId(rs.getString("product_id"));
        tx.setStoreId(rs.getString("store_id"));
        tx.setPaymentMethod(rs.getString("payment_method"));
        tx.setQuantity(rs.getInt("quantity"));
        tx.setAmount(rs.getBigDecimal("amount"));

        // Convert SQL TIMESTAMPTZ → Java OffsetDateTime
        tx.setTimestamp(rs.getObject("timestamp", OffsetDateTime.class));

        return tx;
    }
}
