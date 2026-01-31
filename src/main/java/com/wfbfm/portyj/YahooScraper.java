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


    public Map<String, MonetaryAmount> getPrices(final Set<String> symbols)
    {
        final Map<String, MonetaryAmount> result = new HashMap<>();


        for (String symbol : symbols)
        {
            String json = webClient.get()
                    .uri("https://query1.finance.yahoo.com/v8/finance/chart/{s}", symbol)
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
            result.put(symbol, new MonetaryAmount(
                    new BigDecimal(meta.path("regularMarketPrice").asText()),
                    meta.path("currency").asText()
            ));
        }
        return result;
    }
}