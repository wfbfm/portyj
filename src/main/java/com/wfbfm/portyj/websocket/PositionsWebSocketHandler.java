package com.wfbfm.portyj.websocket;

import com.wfbfm.portyj.positions.SubscriptionMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class PositionsWebSocketHandler extends BinaryWebSocketHandler
{
    private final Logger logger = LoggerFactory.getLogger(PositionsWebSocketHandler.class);
    private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();
    private final ViewServerEventLoop eventLoop;

    public PositionsWebSocketHandler(final ViewServerEventLoop eventLoop)
    {
        this.eventLoop = eventLoop;
    }

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
        eventLoop.submit(new UnsubscribeEvent(session.getId()));
        logger.info("Submitted unsubscribe event for session {}", session);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception
    {
        if (!(message instanceof BinaryMessage binaryMessage))
        {
            logger.warn("Received non-binary message from session {}", session.getId());
            return;
        }

        final byte[] payload = binaryMessage.getPayload().array();
        final SubscriptionMsg subscriptionMsg = SubscriptionMsg.parseFrom(payload);
        final PositionFilter filter = new PositionFilter(subscriptionMsg);

        eventLoop.submit(new SubscribeEvent(session, filter));
        logger.info("Submitted subscribe event for session {}", session);
    }
}
