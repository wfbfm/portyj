package com.wfbfm.portyj;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class LseScraper
{

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public LseScraper(WebClient webClient, ObjectMapper objectMapper)
    {
        this.webClient = webClient;
        this.objectMapper = objectMapper;
    }

    // ---------------- Symbol lookup ----------------
    // ISIN -> TIDM

    public Map<String, String> getSymbols(final Set<String> isins)
    {
        Map<String, String> result = new HashMap<>();

        for (final String isin : isins)
        {
            try
            {
                String json = webClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .scheme("https")
                                .host("api.londonstockexchange.com")
                                .path("/api/gw/lse/search/autocomplete")
                                .queryParam("q", isin)
                                .queryParam("size", 3)
                                .build())
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();

                JsonNode root = objectMapper.readTree(json);

                final JsonNode instruments = root.path("instruments");
                if (instruments.isArray() && !instruments.isEmpty())
                {
                    String url = instruments.get(0).path("url").asText();
                    String[] split = url.split("/");
                    String tidm = split[split.length - 2];
                    result.put(isin, tidm);
                }
            }
            catch (Exception ignored)
            {
            }
        }

        return result;
    }

    // ---------------- Prices ----------------

    public Map<String, MonetaryAmount> getPrices(Set<String> tidms)
    {
        Map<String, MonetaryAmount> result = new HashMap<>();

        for (String tidm : tidms)
        {
            try
            {
                Map<String, Object> payload = Map.of(
                        "path", "issuer-profile",
                        "parameters", "tidm%3D" + tidm +
                                "%26tab%3Dcompany-page" +
                                "%26issuername%3Dunited-kingdom" +
                                "%26tabId%3D771b9c49-382e-4e74-bd94-e96af5c94285",
                        "components", List.of(
                                Map.of(
                                        "componentId", "block_content%3Aeb11eb09-4797-469c-a6ca-a258d2a53d60",
                                        "parameters", ""
                                )
                        )
                );

                String json = webClient.post()
                        .uri("https://api.londonstockexchange.com/api/v1/components/refresh")
                        .header("User-Agent", "Mozilla/5.0")
                        .header("Accept", "application/json")
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(payload)
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();

                final JsonNode root = objectMapper.readTree(json);

                for (JsonNode block : root)
                {
                    if (!"ticker".equals(block.path("type").asText())) continue;

                    for (JsonNode item : block.path("content"))
                    {
                        if ("pricedata".equals(item.path("name").asText()))
                        {
                            JsonNode v = item.path("value");
                            result.put(
                                    tidm,
                                    new MonetaryAmount(
                                            new BigDecimal(v.path("lastprice").asText()),
                                            v.path("currency").asText()
                                    )
                            );
                        }
                    }
                }
            }
            catch (Exception ignored)
            {
            }
        }

        return result;
    }
}
