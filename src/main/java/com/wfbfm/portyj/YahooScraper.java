package com.wfbfm.portyj;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class YahooScraper
{
    private final WebClient webClient;
    private final ObjectMapper objectMapper;


    public YahooScraper(WebClient webClient, ObjectMapper objectMapper)
    {
        this.webClient = webClient;
        this.objectMapper = objectMapper;
    }


    public Map<String, String> getSymbols(final Set<String> isins)
    {
        final Map<String, String> result = new HashMap<>();


        for (final String isin : isins)
        {
            String json = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("https")
                            .host("query2.finance.yahoo.com")
                            .path("/v1/finance/search")
                            .queryParam("q", isin)
                            .queryParam("quotesCount", 1)
                            .build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode root = null;
            try
            {
                root = objectMapper.readTree(json);
            }
            catch (JsonProcessingException e)
            {
                throw new RuntimeException(e);
            }


            if (root == null) continue;


            final JsonNode quotes = root.path("quotes");
            if (quotes.isArray() && !quotes.isEmpty())
            {
                result.put(isin, quotes.get(0).path("symbol").asText());
            }
        }
        return result;
    }


    public Map<Product, Price> getPrices(final Set<Product> products, final Map<String, BigDecimal> fxRates)
    {
        final Map<Product, Price> result = new HashMap<>();


        for (Product product : products)
        {
            String json = webClient.get()
                    .uri("https://query1.finance.yahoo.com/v8/finance/chart/{s}", product.getSymbol())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            final JsonNode root;
            try
            {
                root = objectMapper.readTree(json);
            }
            catch (JsonProcessingException e)
            {
                throw new RuntimeException(e);
            }
            if (root == null) continue;


            JsonNode meta = root.path("chart").path("result").get(0).path("meta");
            final String currency = meta.path("currency").asText();
            final Price price = new Price(currency, new BigDecimal(meta.path("regularMarketPrice").asText()),
                    new BigDecimal(meta.path("previousClose").asText()), fxRates.get(currency));
            result.put(product, price);
        }
        return result;
    }



    public Map<String, BigDecimal> getFxRates(final Set<String> currencies)
    {
        final Map<String, BigDecimal> result = new HashMap<>();


        for (String currency : currencies)
        {
            if (currency.equals("GBP"))
            {
                result.put("GBP", BigDecimal.ONE);
                result.put("GBp", new BigDecimal("0.01"));
                continue;
            }
            String json = webClient.get()
                    .uri("https://query1.finance.yahoo.com/v8/finance/chart/{s}", currency + "GBP=X")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            final JsonNode root;
            try
            {
                root = objectMapper.readTree(json);
            }
            catch (JsonProcessingException e)
            {
                throw new RuntimeException(e);
            }
            if (root == null) continue;


            JsonNode meta = root.path("chart").path("result").get(0).path("meta");
            result.put(currency, new BigDecimal(meta.path("regularMarketPrice").asText()));
        }
        return result;
    }
}