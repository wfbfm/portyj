package com.wfbfm.portyj;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class PositionNormaliser
{
    private final CanaccordPositionLoader loader;
    private final DbPersistor dbPersistor;
    private final LseScraper lseScraper;
    private final YahooScraper yahooScraper;

    public PositionNormaliser(final CanaccordPositionLoader loader, final DbPersistor dbPersistor, final LseScraper lseScraper,
                              final YahooScraper yahooScraper)
    {
        this.loader = loader;
        this.dbPersistor = dbPersistor;
        this.lseScraper = lseScraper;
        this.yahooScraper = yahooScraper;
    }

    public void initialisePositions(final String fileName) throws IOException
    {
        final List<CanaccordPosition> canaccordPositionList = loader.parse(fileName);
        final Set<String> lseIsins = new HashSet<>();
        final Set<String> yahooIsins = new HashSet<>();

        canaccordPositionList.forEach(pos ->
        {
            if (pos.getAssetName().toLowerCase().contains("uk treasury"))
            {
                lseIsins.add(pos.getIsin());
            }
            else
            {
                yahooIsins.add(pos.getIsin());
            }
        });

        final Map<String, String> lseSymbols = lseScraper.getSymbols(lseIsins);
        final Map<String, String> yahooSymbols = yahooScraper.getSymbols(yahooIsins);

        final List<Position> positions = canaccordPositionList.stream()
                .map(pos ->
                {
                    return normalise(pos, lseSymbols, yahooSymbols);
                })
                .toList();

        dbPersistor.insertPositions(positions);
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
        position.setCurrency(canaccordPosition.getPriceCurrency());
        position.setPurchasePrice(canaccordPosition.getPrice()); // fixme - this isn't quite accurate, it's a bit out of date
        position.setPurchasePriceGbp(canaccordPosition.getBookCost().divide(canaccordPosition.getQuantity(), RoundingMode.HALF_UP));
        position.setPurchaseFxRate(position.getPurchasePriceGbp().divide(position.getPurchasePrice(), RoundingMode.HALF_UP));
        position.setPurchaseNotional(position.getPurchasePrice().multiply(position.getQuantity()));
        position.setPurchaseNotionalGbp(position.getPurchasePriceGbp().multiply(position.getQuantity()));
        position.setTradeDate(canaccordPosition.getAsOf()); // todo - not really
        return position;
    }
}
