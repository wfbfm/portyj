package com.wfbfm.portyj;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.thymeleaf.expression.Sets;

import java.util.*;

@SpringBootApplication
public class PortyjApplication implements CommandLineRunner
{
    private final LseScraper lseScraper;
    private final YahooScraper yahooScraper;
    private final CanaccordPositionLoader loader;
    private final DbPersistor dbPersistor;

    public PortyjApplication(final LseScraper lseScraper, final YahooScraper yahooScraper, final CanaccordPositionLoader loader,
                             final DbPersistor dbPersistor)
    {
        this.lseScraper = lseScraper;
        this.yahooScraper = yahooScraper;
        this.loader = loader;
        this.dbPersistor = dbPersistor;
    }

    public static void main(String[] args)
    {
        SpringApplication.run(PortyjApplication.class, args);
    }

    @Override
    public void run(final String... args) throws Exception
    {
//        final Set<String> lseIsins = Set.of("GB00BPSNB460", "GB00BMBL1G81", "GB00BJQWYH73");
//        final Set<String> yahooIsins = Set.of("US70450Y1038", "DK0062498333", "CH0038863350");
//
//        final Map<String, String> lseSymbols = lseScraper.getSymbols(lseIsins);
//        final Set<String> lseTidms = new HashSet<>();
//        lseSymbols.forEach((isin, symbol) -> {
//            System.out.println("Found symbol: " + symbol + " for isin: " + isin);
//            lseTidms.add(symbol);
//        });
//
//        final Map<String, String> yahooSymbols = yahooScraper.getSymbols(yahooIsins);
//        final Set<String> yahooIds = new HashSet<>();
//
//        yahooSymbols.forEach((isin, symbol) -> {
//            System.out.println("Found symbol: " + symbol + " for isin: " + isin);
//            yahooIds.add(symbol);
//        });
//
//        final Map<String, MonetaryAmount> lsePrices = lseScraper.getPrices(lseTidms);
//        lsePrices.forEach((symbol, price) -> {
//            System.out.println("Found price: " + price.getAmount().toPlainString() + " " + price.getCurrency() + " for symbol: " + symbol);
//        });
//
//        final Map<String, MonetaryAmount> yahooPrices = yahooScraper.getPrices(yahooIds);
//        yahooPrices.forEach((symbol, price) -> {
//            System.out.println("Found price: " + price.getAmount().toPlainString() + " " + price.getCurrency() + " for symbol: " + symbol);
//        });

        final List<CanaccordPosition> positions = loader.parse("positions.csv");
        System.out.println(Arrays.deepToString(dbPersistor.insertPositions(positions)));
    }
}
