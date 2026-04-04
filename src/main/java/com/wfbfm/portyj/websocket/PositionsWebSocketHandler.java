package com.wfbfm.portyj.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wfbfm.portyj.ui.PositionView;
import com.wfbfm.portyj.ui.PositionViewRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class PositionsWebSocketHandler extends TextWebSocketHandler
{
    private final Logger logger = LoggerFactory.getLogger(PositionsWebSocketHandler.class);
    private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();
    private final ObjectMapper objectMapper;
    private final PositionViewRepository repository;

    public PositionsWebSocketHandler(ObjectMapper objectMapper,
                                     PositionViewRepository repository)
    {
        this.objectMapper = objectMapper;
        this.repository = repository;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception
    {
        logger.info("Adding session {}", session);
        sessions.add(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status)
    {
        sessions.remove(session);
    }

    public void broadcastPosition(final PositionView view)
    {
        sessions.forEach(session ->
        {
            try
            {
                sendPosition(session, view);
            } catch (Exception ignored)
            {
            }
        });
    }

    private void sendPosition(final WebSocketSession session, final PositionView view) throws Exception
    {
        String json = objectMapper.writeValueAsString(view);
        logger.info("Sending json {}", json);
        session.sendMessage(new TextMessage(json));
    }
}
