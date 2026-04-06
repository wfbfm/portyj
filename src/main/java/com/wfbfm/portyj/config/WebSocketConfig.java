package com.wfbfm.portyj.config;

import com.wfbfm.portyj.websocket.PositionsWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer
{
    private final PositionsWebSocketHandler handler;

    public WebSocketConfig(PositionsWebSocketHandler handler)
    {
        this.handler = handler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry)
    {
        registry.addHandler(handler, "/ws/positions")
                .setAllowedOrigins("http://localhost:5173");
    }
}

