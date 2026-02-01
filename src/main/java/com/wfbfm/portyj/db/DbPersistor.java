package com.wfbfm.portyj.db;

import com.wfbfm.portyj.model.Position;
import com.wfbfm.portyj.model.Price;
import com.wfbfm.portyj.model.Product;
import com.wfbfm.portyj.model.SymbolSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.util.*;

@Service
public class DbPersistor
{

    private final JdbcTemplate jdbcTemplate;

    public DbPersistor(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final String INSERT_POSITION_SQL = """
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

    private static final String INSERT_PRICE_SQL = """
            INSERT INTO prices (
                isin,
                source,
                symbol,
                current_price,
                last_close,
                currency,
                current_price_gbp,
                last_close_gbp,
                fx_rate,
                percent_change
            ) VALUES (?, ?::price_source, ?, ?, ?, ?, ?, ?, ?, ?)
            """;


    public int[][] insertPositions(final List<Position> positions) {
        return jdbcTemplate.batchUpdate(INSERT_POSITION_SQL, positions, positions.size(),
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

    public int[][] insertPrices(final Map<Product, Price> prices) {

        List<Map.Entry<Product, Price>> entries = new ArrayList<>(prices.entrySet());

        return jdbcTemplate.batchUpdate(
                INSERT_PRICE_SQL,
                entries,
                entries.size(),
                (PreparedStatement ps, Map.Entry<Product, Price> entry) -> {

                    Product product = entry.getKey();
                    Price price = entry.getValue();

                    ps.setString(1, product.getIsin());
                    ps.setString(2, product.getSource().name());
                    ps.setString(3, product.getSymbol());
                    ps.setBigDecimal(4, price.getPrice());
                    ps.setBigDecimal(5, price.getLastClose());
                    ps.setString(6, price.getCurrency());
                    ps.setBigDecimal(7, price.getPriceGbp());
                    ps.setBigDecimal(8, price.getLastCloseGbp());
                    ps.setBigDecimal(9, price.getFxRate());
                    ps.setBigDecimal(10, price.getPercentChangeSinceClose());
                }
        );
    }

    public Set<Product> getUniqueProducts()
    {
        final String sql = """
                SELECT DISTINCT isin, source, symbol, currency from positions
                """;
        return new HashSet<>(
                jdbcTemplate.query(sql, (rs, rowNum) ->
                        new Product(
                                rs.getString("isin"),
                                SymbolSource.valueOf(rs.getString("source")),
                                rs.getString("symbol"),
                                rs.getString("currency")
                        )
                )
        );
    }
}
