package com.wfbfm.portyj;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class PriceLoader
{

    private final DbPersistor dbPersistor;
    private final LseScraper lseScraper;
    private final YahooScraper yahooScraper;

    public PriceLoader(final DbPersistor dbPersistor, final LseScraper lseScraper,
                       final YahooScraper yahooScraper)
    {
        this.dbPersistor = dbPersistor;
        this.lseScraper = lseScraper;
        this.yahooScraper = yahooScraper;
    }

    public void fetchPrices()
    {
        final Set<Product> allProducts = dbPersistor.getUniqueProducts();
        final Set<String> currencies = new HashSet<>();
        final Set<Product> lseProducts = new HashSet<>();
        final Set<Product> yahooProducts = new HashSet<>();
        for (final Product product : allProducts)
        {
            switch (product.getSource()) {
                case LSE:
                    lseProducts.add(product);
                    break;
                case YAHOO:
                    yahooProducts.add(product);
                    break;
            }
            currencies.add(product.getCurrency());
        }

        final Map<String, BigDecimal> fxRates = yahooScraper.getFxRates(currencies);
        final Map<Product, Price> lsePrices = lseScraper.getPrices(lseProducts, fxRates);
        final Map<Product, Price> yahooPrices = yahooScraper.getPrices(yahooProducts, fxRates);
        System.out.println("Found " + lsePrices.size() + " prices for " + lseProducts.size() + " LSE symbols");
        System.out.println("Found " + yahooPrices.size() + " prices for " + yahooPrices.size() + " Yahoo symbols");
        dbPersistor.insertPrices(lsePrices);
        dbPersistor.insertPrices(yahooPrices);
    }

    private Position normalise(final CanaccordPosition canaccordPosition,
                               final Map<String, String> lseSymbols,
                               final Map<String, String> yahooSymbols)
    {
        final String isin = canaccordPosition.getIsin();
        final String symbol;
        final SymbolSource source;
        if (lseSymbols.containsKey(isin))
        {
            symbol = lseSymbols.get(isin);
            source = SymbolSource.LSE;
        }
        else
        {
            symbol = yahooSymbols.get(isin);
            source = SymbolSource.YAHOO;
        }
        final Position position = new Position();
        position.setIsin(isin);
        position.setAccountType(canaccordPosition.getAccount().contains("ISA") ? AccountType.ISA : AccountType.NON_ISA);
        position.setAssetName(canaccordPosition.getAssetName());
        position.setSymbol(symbol);
        position.setSource(source);
        position.setQuantity(canaccordPosition.getQuantity());
        position.setPurchaseFxRate(canaccordPosition.getFxRate());
        position.setCurrency(canaccordPosition.getCurrency());
        final BigDecimal purchaseNotionalGbp = canaccordPosition.getBookCost();
        final BigDecimal purchasePriceGbp = purchaseNotionalGbp.divide(canaccordPosition.getQuantity(), RoundingMode.HALF_UP);
        final BigDecimal purchaseNotional = purchaseNotionalGbp.divide(canaccordPosition.getFxRate(), RoundingMode.HALF_UP);
        final BigDecimal purchasePrice = purchasePriceGbp.divide(canaccordPosition.getFxRate(), RoundingMode.HALF_UP);
        position.setPurchasePrice(purchasePrice);
        position.setPurchasePriceGbp(purchasePriceGbp);
        position.setPurchaseNotional(purchaseNotional);
        position.setPurchaseNotionalGbp(purchaseNotionalGbp);
        position.setTradeDate(canaccordPosition.getAsOf()); // todo - not really
        return position;
    }
}
