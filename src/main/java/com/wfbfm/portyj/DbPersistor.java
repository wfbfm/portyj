package com.wfbfm.portyj;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.util.List;

@Service
public class DbPersistor
{

    private final JdbcTemplate jdbcTemplate;

    public DbPersistor(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final String INSERT_SQL = """
            INSERT INTO positions (
                isin,
                asset_name,
                source,
                symbol,
                quantity,
                purchase_price,
                currency,
                purchase_price_gbp,
                purchase_fx_rate,
                purchase_notional,
                purchase_notional_gbp,
                trade_date,
                account_type
            ) VALUES (?, ?, ?::price_source, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?::account_type)
            """;

    // Insert a single position
    public int insertPosition(final CanaccordPosition position) {
        return jdbcTemplate.update(INSERT_SQL,
                position.getIsin(),
                position.getAssetName(),
                "YAHOO", // hardcoded for now
                "SYMBOL", // placeholder
                position.getQuantity(),
                position.getPrice(),
                position.getPriceCurrency(),
                0,  // purchase_price_gbp
                0,  // purchase_fx_rate
                0,  // purchase_notional
                0,  // purchase_notional_gbp
                position.getAsOf()
        );
    }

    // Insert a list of positions efficiently
    public int[][] insertPositions(final List<Position> positions) {
        return jdbcTemplate.batchUpdate(INSERT_SQL, positions, positions.size(),
                (PreparedStatement ps, Position position) -> {
                    ps.setString(1, position.getIsin());
                    ps.setString(2, position.getAssetName());
                    ps.setString(3, position.getSource().name());
                    ps.setString(4, position.getSymbol());
                    ps.setBigDecimal(5, position.getQuantity());
                    ps.setBigDecimal(6, position.getPurchasePrice());
                    ps.setString(7, position.getCurrency());
                    ps.setBigDecimal(8, position.getPurchasePriceGbp());
                    ps.setBigDecimal(9, position.getPurchaseFxRate());
                    ps.setBigDecimal(10, position.getPurchaseNotional());
                    ps.setBigDecimal(11, position.getPurchaseNotionalGbp());
                    ps.setDate(12, java.sql.Date.valueOf(position.getTradeDate()));
                    ps.setString(13, position.getAccountType().name());
                });
    }
}
