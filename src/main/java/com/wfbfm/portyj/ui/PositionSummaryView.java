package com.wfbfm.portyj.ui;

import com.wfbfm.portyj.model.AccountType;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class PositionSummaryView
{
    private static final BigDecimal ZERO = BigDecimal.ZERO;

    private final BigDecimal totalNotional;
    private final BigDecimal totalNonIsaNotional;
    private final BigDecimal totalIsaNotional;

    private final BigDecimal totalPnl;
    private final BigDecimal totalNonIsaPnl;
    private final BigDecimal totalIsaPnl;

    private final BigDecimal totalPcChange;
    private final BigDecimal totalNonIsaPcChange;
    private final BigDecimal totalIsaPcChange;

    private final BigDecimal dailyPnl;
    private final BigDecimal dailyNonIsaPnl;
    private final BigDecimal dailyIsaPnl;

    private final BigDecimal dailyPcChange;
    private final BigDecimal dailyNonIsaPcChange;
    private final BigDecimal dailyIsaPcChange;

    public PositionSummaryView(final List<PositionView> positions)
    {
        BigDecimal totalNotional = ZERO;
        BigDecimal totalIsaNotional = ZERO;
        BigDecimal totalNonIsaNotional = ZERO;

        BigDecimal totalPnl = ZERO;
        BigDecimal totalIsaPnl = ZERO;
        BigDecimal totalNonIsaPnl = ZERO;

        BigDecimal dailyPnl = ZERO;
        BigDecimal dailyIsaPnl = ZERO;
        BigDecimal dailyNonIsaPnl = ZERO;

        BigDecimal totalPurchaseNotional = ZERO;
        BigDecimal totalIsaPurchaseNotional = ZERO;
        BigDecimal totalNonIsaPurchaseNotional = ZERO;

        BigDecimal totalLastCloseNotional = ZERO;
        BigDecimal totalIsaLastCloseNotional = ZERO;
        BigDecimal totalNonIsaLastCloseNotional = ZERO;

        for (PositionView p : positions)
        {

            BigDecimal currentNotional = p.getQuantity().multiply(p.getCurrentPriceGbp());
            BigDecimal purchaseNotional = p.getQuantity().multiply(p.getPurchasePriceGbp());
            BigDecimal lastCloseNotional = p.getQuantity().multiply(p.getLastCloseGbp());

            totalNotional = totalNotional.add(currentNotional);
            totalPurchaseNotional = totalPurchaseNotional.add(purchaseNotional);
            totalLastCloseNotional = totalLastCloseNotional.add(lastCloseNotional);
            totalPnl = totalPnl.add(p.getTotalPnlGbp());
            dailyPnl = dailyPnl.add(purchaseNotional.subtract(lastCloseNotional));

            if (AccountType.ISA.name().equals(p.getAccountType()))
            {
                totalIsaNotional = totalIsaNotional.add(currentNotional);
                totalIsaPurchaseNotional = totalIsaPurchaseNotional.add(purchaseNotional);
                totalIsaLastCloseNotional = totalIsaLastCloseNotional.add(lastCloseNotional);
                totalIsaPnl = totalIsaPnl.add(p.getTotalPnlGbp());
                dailyIsaPnl = dailyIsaPnl.add(purchaseNotional.subtract(lastCloseNotional));
            }
            else
            {
                totalNonIsaNotional = totalNonIsaNotional.add(currentNotional);
                totalNonIsaPurchaseNotional = totalNonIsaPurchaseNotional.add(purchaseNotional);
                totalNonIsaLastCloseNotional = totalNonIsaLastCloseNotional.add(lastCloseNotional);
                totalNonIsaPnl = totalNonIsaPnl.add(p.getTotalPnlGbp());
                dailyNonIsaPnl = dailyNonIsaPnl.add(purchaseNotional.subtract(lastCloseNotional));
            }
        }

        this.totalPcChange = percentChange(totalPnl, totalPurchaseNotional);
        this.totalIsaPcChange = percentChange(totalIsaPnl, totalIsaPurchaseNotional);
        this.totalNonIsaPcChange = percentChange(totalNonIsaPnl, totalNonIsaPurchaseNotional);

        this.dailyPcChange = percentChange(dailyPnl, totalLastCloseNotional);
        this.dailyIsaPcChange = percentChange(dailyIsaPnl, totalIsaLastCloseNotional);
        this.dailyNonIsaPcChange = percentChange(dailyNonIsaPnl, totalNonIsaLastCloseNotional);

        // ----- Totals -----

        this.totalNotional = totalNotional;
        this.totalIsaNotional = totalIsaNotional;
        this.totalNonIsaNotional = totalNonIsaNotional;

        this.totalPnl = totalPnl;
        this.totalIsaPnl = totalIsaPnl;
        this.totalNonIsaPnl = totalNonIsaPnl;

        // ----- Daily (same for now) -----

        this.dailyPnl = dailyPnl;
        this.dailyIsaPnl = dailyIsaPnl;
        this.dailyNonIsaPnl = dailyNonIsaPnl;
    }


    private static BigDecimal percentChange(BigDecimal pnl, BigDecimal notional) {
        if (notional.signum() == 0) {
            return BigDecimal.ZERO;
        }
        return pnl
                .divide(notional, 6, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    public BigDecimal getTotalNotional()
    {
        return totalNotional;
    }

    public BigDecimal getTotalNonIsaNotional()
    {
        return totalNonIsaNotional;
    }

    public BigDecimal getTotalIsaNotional()
    {
        return totalIsaNotional;
    }

    public BigDecimal getTotalPnl()
    {
        return totalPnl;
    }

    public BigDecimal getTotalNonIsaPnl()
    {
        return totalNonIsaPnl;
    }

    public BigDecimal getTotalIsaPnl()
    {
        return totalIsaPnl;
    }

    public BigDecimal getTotalPcChange()
    {
        return totalPcChange;
    }

    public BigDecimal getTotalNonIsaPcChange()
    {
        return totalNonIsaPcChange;
    }

    public BigDecimal getTotalIsaPcChange()
    {
        return totalIsaPcChange;
    }

    public BigDecimal getDailyPnl()
    {
        return dailyPnl;
    }

    public BigDecimal getDailyNonIsaPnl()
    {
        return dailyNonIsaPnl;
    }

    public BigDecimal getDailyIsaPnl()
    {
        return dailyIsaPnl;
    }

    public BigDecimal getDailyPcChange()
    {
        return dailyPcChange;
    }

    public BigDecimal getDailyNonIsaPcChange()
    {
        return dailyNonIsaPcChange;
    }

    public BigDecimal getDailyIsaPcChange()
    {
        return dailyIsaPcChange;
    }
}
