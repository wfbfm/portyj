package com.wfbfm.portyj;

public class Product
{
    private final String isin;
    private final SymbolSource source;
    private final String symbol;
    private final String currency;

    public Product(final String isin, final SymbolSource source, final String symbol, final String currency)
    {
        this.isin = isin;
        this.source = source;
        this.symbol = symbol;
        this.currency = currency;
    }

    public String getIsin()
    {
        return isin;
    }

    public SymbolSource getSource()
    {
        return source;
    }

    public String getSymbol()
    {
        return symbol;
    }

    public String getCurrency()
    {
        return currency;
    }
}
