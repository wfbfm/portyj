package com.wfbfm.portyj.websocket;

import com.wfbfm.portyj.positions.Position;
import com.wfbfm.portyj.positions.WsMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.WebSocketSession;

@Component
public class PositionSender
{
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public void sendPosition(final WebSocketSession session, final Position position) throws Exception
    {
        final WsMsg msg = WsMsg.newBuilder()
                .setPosition(position)
                .build();
        logger.info("Sending position {} to session {}", position.getId(), session);
        session.sendMessage(new BinaryMessage(msg.toByteArray()));
    }
}
