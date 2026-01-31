package com.wfbfm.portyj;

import java.math.BigDecimal;

public class MonetaryAmount
{
    private final BigDecimal amount;
    private final String currency;

    public MonetaryAmount(final BigDecimal amount, final String currency)
    {
        this.amount = amount;
        this.currency = currency;
    }

    public BigDecimal getAmount()
    {
        return amount;
    }

    public String getCurrency()
    {
        return currency;
    }
}
