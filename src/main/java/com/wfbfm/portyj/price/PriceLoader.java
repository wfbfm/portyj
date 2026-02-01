package com.wfbfm.portyj.price;

import com.wfbfm.portyj.db.DbPersistor;
import com.wfbfm.portyj.model.*;
import com.wfbfm.portyj.scrape.LseScraper;
import com.wfbfm.portyj.scrape.YahooScraper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
public class PriceLoader
{

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
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

    @Scheduled(cron = "0 15 8-22 * * MON-FRI")
    public void fetchPrices()
    {
        logger.info("Initiating price refresh");
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
        logger.info("Fetched {} fxRates", fxRates.size());
        final Map<Product, Price> lsePrices = lseScraper.getPrices(lseProducts, fxRates);
        logger.info("Fetched {} lsePrices", lsePrices.size());
        final Map<Product, Price> yahooPrices = yahooScraper.getPrices(yahooProducts, fxRates);
        logger.info("Fetched {} yahooPrices", yahooPrices.size());
        dbPersistor.insertPrices(lsePrices);
        dbPersistor.insertPrices(yahooPrices);
    }
}
