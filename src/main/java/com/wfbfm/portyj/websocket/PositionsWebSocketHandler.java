package com.wfbfm.portyj.websocket;

import com.wfbfm.portyj.positions.Position;
import com.wfbfm.portyj.positions.WsMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class PositionsWebSocketHandler extends BinaryWebSocketHandler
{
    private final Logger logger = LoggerFactory.getLogger(PositionsWebSocketHandler.class);
    private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();

    @Override
    public void afterConnectionEstablished(final WebSocketSession session)
    {
        logger.info("Adding session {}", session);
        sessions.add(session);
    }

    @Override
    public void afterConnectionClosed(final WebSocketSession session, final CloseStatus status)
    {
        sessions.remove(session);
    }

    public void broadcastPosition(final Position position)
    {
        sessions.forEach(session ->
        {
            try
            {
                sendPosition(session, position);
            } catch (Exception ignored)
            {
            }
        });
    }

    private void sendPosition(final WebSocketSession session, final Position position) throws Exception
    {
        final WsMsg msg = WsMsg.newBuilder()
                .setPosition(position)
                .build();
        logger.info("Sending position {} to session {}", position.getId(), session);
        session.sendMessage(new BinaryMessage(msg.toByteArray()));
    }
}
