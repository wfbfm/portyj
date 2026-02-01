package com.wfbfm.portyj.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CanaccordPosition
{

    private String assetName;
    private String account;
    private BigDecimal percentOfTotal;
    private String currency;
    private BigDecimal bookCost;
    private BigDecimal value;
    private BigDecimal unrealisedGain;
    private BigDecimal price;
    private String priceCurrency;
    private String isin;
    private BigDecimal quantity;
    private LocalDate asOf;
    private BigDecimal fxRate;

    public String getAssetName()
    {
        return assetName;
    }

    public String getAccount()
    {
        return account;
    }

    public BigDecimal getPercentOfTotal()
    {
        return percentOfTotal;
    }

    public String getCurrency()
    {
        return currency;
    }

    public BigDecimal getBookCost()
    {
        return bookCost;
    }

    public BigDecimal getValue()
    {
        return value;
    }

    public BigDecimal getUnrealisedGain()
    {
        return unrealisedGain;
    }

    public BigDecimal getPrice()
    {
        return price;
    }

    public String getPriceCurrency()
    {
        return priceCurrency;
    }

    public String getIsin()
    {
        return isin;
    }

    public BigDecimal getQuantity()
    {
        return quantity;
    }

    public LocalDate getAsOf()
    {
        return asOf;
    }

    public void setAssetName(final String assetName)
    {
        this.assetName = assetName;
    }

    public void setAccount(final String account)
    {
        this.account = account;
    }

    public void setPercentOfTotal(final BigDecimal percentOfTotal)
    {
        this.percentOfTotal = percentOfTotal;
    }

    public void setCurrency(final String currency)
    {
        this.currency = currency;
    }

    public void setBookCost(final BigDecimal bookCost)
    {
        this.bookCost = bookCost;
    }

    public void setValue(final BigDecimal value)
    {
        this.value = value;
    }

    public void setUnrealisedGain(final BigDecimal unrealisedGain)
    {
        this.unrealisedGain = unrealisedGain;
    }

    public void setPrice(final BigDecimal price)
    {
        this.price = price;
    }

    public void setPriceCurrency(final String priceCurrency)
    {
        this.priceCurrency = priceCurrency;
    }

    public void setIsin(final String isin)
    {
        this.isin = isin;
    }

    public void setQuantity(final BigDecimal quantity)
    {
        this.quantity = quantity;
    }

    public void setAsOf(final LocalDate asOf)
    {
        this.asOf = asOf;
    }

    public BigDecimal getFxRate()
    {
        return fxRate;
    }

    public void setFxRate(final BigDecimal fxRate)
    {
        this.fxRate = fxRate;
    }
}
