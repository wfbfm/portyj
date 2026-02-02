package com.wfbfm.portyj.ui;

import java.math.BigDecimal;

public class PositionView
{
    private final String isin;
    private final String assetName;
    private final String accountType;
    private final BigDecimal quantity;
    private final BigDecimal purchasePriceGbp;
    private final BigDecimal currentPriceGbp;
    private final BigDecimal lastCloseGbp;
    private final BigDecimal totalPnlGbp;
    private final BigDecimal dailyPnlGbp;
    private final BigDecimal percentChange;

    public PositionView(final String isin,
                        final String assetName,
                        final String accountType,
                        final BigDecimal quantity,
                        final BigDecimal purchasePriceGbp,
                        final BigDecimal currentPriceGbp,
                        final BigDecimal lastCloseGbp,
                        final BigDecimal totalPnlGbp,
                        final BigDecimal dailyPnlGbp,
                        final BigDecimal percentChange)
    {
        this.isin = isin;
        this.assetName = assetName;
        this.accountType = accountType;
        this.quantity = quantity;
        this.purchasePriceGbp = purchasePriceGbp;
        this.currentPriceGbp = currentPriceGbp;
        this.lastCloseGbp = lastCloseGbp;
        this.totalPnlGbp = totalPnlGbp;
        this.dailyPnlGbp = dailyPnlGbp;
        this.percentChange = percentChange;
    }

    public String getIsin()
    {
        return isin;
    }

    public String getAssetName()
    {
        return assetName;
    }

    public String getAccountType()
    {
        return accountType;
    }

    public BigDecimal getQuantity()
    {
        return quantity;
    }

    public BigDecimal getPurchasePriceGbp()
    {
        return purchasePriceGbp;
    }

    public BigDecimal getCurrentPriceGbp()
    {
        return currentPriceGbp;
    }

    public BigDecimal getTotalPnlGbp()
    {
        return totalPnlGbp;
    }

    public BigDecimal getPercentChange()
    {
        return percentChange;
    }

    public BigDecimal getLastCloseGbp()
    {
        return lastCloseGbp;
    }

    public BigDecimal getDailyPnlGbp()
    {
        return dailyPnlGbp;
    }
}


