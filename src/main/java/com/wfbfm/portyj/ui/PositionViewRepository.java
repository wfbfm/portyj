package com.wfbfm.portyj.ui;

import com.wfbfm.portyj.model.Position;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PositionViewRepository
{
    private final JdbcTemplate jdbcTemplate;

    public PositionViewRepository(final JdbcTemplate jdbcTemplate)
    {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<PositionView> getPositions()
    {

        String sql = """
                SELECT
                p.isin,
                p.asset_name,
                p.account_type,
                p.quantity,
                p.purchase_price_gbp,
                pr.current_price_gbp,
                pr.last_close_gbp,
                pr.percent_change,
                (pr.current_price_gbp - p.purchase_price_gbp) * p.quantity AS pnl_gbp
                FROM positions p
                JOIN LATERAL (
                SELECT *
                FROM prices pr
                WHERE pr.isin = p.isin
                ORDER BY pr.captured_at DESC
                LIMIT 1
                ) pr ON true
                ORDER BY pnl_gbp DESC
                """;


        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new PositionView(
                        rs.getString("isin"),
                        rs.getString("asset_name"),
                        rs.getString("account_type"),
                        rs.getBigDecimal("quantity"),
                        rs.getBigDecimal("purchase_price_gbp"),
                        rs.getBigDecimal("current_price_gbp"),
                        rs.getBigDecimal("last_close_gbp"),
                        rs.getBigDecimal("pnl_gbp"),
                        rs.getBigDecimal("percent_change")
                )
        );
    }
}
