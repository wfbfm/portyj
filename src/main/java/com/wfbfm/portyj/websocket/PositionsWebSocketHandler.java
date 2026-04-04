package com.wfbfm.portyj.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wfbfm.portyj.ui.PositionDto;
import com.wfbfm.portyj.ui.PositionSummaryView;
import com.wfbfm.portyj.ui.PositionViewRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
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

        sendPositions(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status)
    {
        sessions.remove(session);
    }

    public void broadcastPositions()
    {
        sessions.forEach(session ->
        {
            try
            {
                sendPositions(session);
            } catch (Exception ignored)
            {
            }
        });
    }

    private void sendPositions(WebSocketSession session) throws Exception
    {
        final var positions = repository.getPositions();
        final var summary = new PositionSummaryView(positions);
        final var dto = new PositionDto(positions, summary);

        String json = objectMapper.writeValueAsString(dto);
        logger.info("Sending json {}", json);
        session.sendMessage(new TextMessage(json));
    }
}
