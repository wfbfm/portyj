package com.wfbfm.portyj;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    WebClient webClient() {
        return WebClient.builder()
                .defaultHeader("User-Agent", "Mozilla/5.0")
                .build();
    }

    @Bean
    ObjectMapper objectMapper()
    {
        return new ObjectMapper();
    }
}
