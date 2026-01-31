package com.wfbfm.portyj;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Position
{
    private long id;
    private String isin;
    private AccountType accountType;
    private String assetName;
    private SymbolSource source;
    private String symbol;
    private BigDecimal quantity;
    private BigDecimal purchasePrice;
    private String currency;
    private BigDecimal purchasePriceGbp;
    private BigDecimal purchaseFxRate;
    private BigDecimal purchaseNotional;
    private BigDecimal purchaseNotionalGbp;
    private LocalDate tradeDate;
    private LocalDateTime capturedAt;

    public Position()
    {

    }

    public long getId()
    {
        return id;
    }

    public void setId(final long id)
    {
        this.id = id;
    }

    public String getIsin()
    {
        return isin;
    }

    public void setIsin(final String isin)
    {
        this.isin = isin;
    }

    public String getAssetName()
    {
        return assetName;
    }

    public void setAssetName(final String assetName)
    {
        this.assetName = assetName;
    }

    public SymbolSource getSource()
    {
        return source;
    }

    public void setSource(final SymbolSource source)
    {
        this.source = source;
    }

    public String getSymbol()
    {
        return symbol;
    }

    public void setSymbol(final String symbol)
    {
        this.symbol = symbol;
    }

    public BigDecimal getQuantity()
    {
        return quantity;
    }

    public void setQuantity(final BigDecimal quantity)
    {
        this.quantity = quantity;
    }

    public BigDecimal getPurchasePrice()
    {
        return purchasePrice;
    }

    public void setPurchasePrice(final BigDecimal purchasePrice)
    {
        this.purchasePrice = purchasePrice;
    }

    public String getCurrency()
    {
        return currency;
    }

    public void setCurrency(final String currency)
    {
        this.currency = currency;
    }

    public BigDecimal getPurchasePriceGbp()
    {
        return purchasePriceGbp;
    }

    public void setPurchasePriceGbp(final BigDecimal purchasePriceGbp)
    {
        this.purchasePriceGbp = purchasePriceGbp;
    }

    public BigDecimal getPurchaseFxRate()
    {
        return purchaseFxRate;
    }

    public void setPurchaseFxRate(final BigDecimal purchaseFxRate)
    {
        this.purchaseFxRate = purchaseFxRate;
    }

    public BigDecimal getPurchaseNotional()
    {
        return purchaseNotional;
    }

    public void setPurchaseNotional(final BigDecimal purchaseNotional)
    {
        this.purchaseNotional = purchaseNotional;
    }

    public BigDecimal getPurchaseNotionalGbp()
    {
        return purchaseNotionalGbp;
    }

    public void setPurchaseNotionalGbp(final BigDecimal purchaseNotionalGbp)
    {
        this.purchaseNotionalGbp = purchaseNotionalGbp;
    }

    public LocalDate getTradeDate()
    {
        return tradeDate;
    }

    public void setTradeDate(final LocalDate tradeDate)
    {
        this.tradeDate = tradeDate;
    }

    public LocalDateTime getCapturedAt()
    {
        return capturedAt;
    }

    public void setCapturedAt(final LocalDateTime capturedAt)
    {
        this.capturedAt = capturedAt;
    }

    public AccountType getAccountType()
    {
        return accountType;
    }

    public void setAccountType(final AccountType accountType)
    {
        this.accountType = accountType;
    }
}
