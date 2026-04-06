package com.wfbfm.portyj.ui;

import java.math.BigDecimal;

public record PositionView(String isin, String assetName, String accountType, BigDecimal quantity, BigDecimal purchasePriceGbp,
                           BigDecimal currentPriceGbp, BigDecimal lastCloseGbp, BigDecimal totalPnlGbp, BigDecimal dailyPnlGbp,
                           BigDecimal percentChange, long id)
{
}


