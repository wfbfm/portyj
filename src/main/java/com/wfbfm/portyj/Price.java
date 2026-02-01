package com.wfbfm.portyj;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Price
{

    private static final int CALC_SCALE = 10;
    private static final int DISPLAY_SCALE = 4;

    private final String currency;
    private final BigDecimal price;
    private final BigDecimal lastClose;
    private final BigDecimal fxRate;
    private final BigDecimal priceGbp;
    private final BigDecimal lastCloseGbp;
    private final BigDecimal percentChangeSinceClose;

    public Price(final String currency, final BigDecimal price, final BigDecimal lastClose, final BigDecimal fxRate)
    {
        this.currency = currency;
        this.price = price;
        this.lastClose = lastClose;
        this.fxRate = fxRate;
        this.priceGbp = price.multiply(fxRate).setScale(DISPLAY_SCALE, RoundingMode.HALF_UP);
        this.lastCloseGbp = lastClose.multiply(fxRate).setScale(DISPLAY_SCALE, RoundingMode.HALF_UP);
        this.percentChangeSinceClose = (price.subtract(lastClose))
                .divide(lastClose, CALC_SCALE, RoundingMode.HALF_UP)
                .multiply(new BigDecimal(100))
                .setScale(DISPLAY_SCALE, RoundingMode.HALF_UP);
    }

    public String getCurrency()
    {
        return currency;
    }

    public BigDecimal getPrice()
    {
        return price;
    }

    public BigDecimal getLastClose()
    {
        return lastClose;
    }

    public BigDecimal getFxRate()
    {
        return fxRate;
    }

    public BigDecimal getPriceGbp()
    {
        return priceGbp;
    }

    public BigDecimal getLastCloseGbp()
    {
        return lastCloseGbp;
    }

    public BigDecimal getPercentChangeSinceClose()
    {
        return percentChangeSinceClose;
    }
}
