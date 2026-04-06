package com.wfbfm.portyj.websocket;

import com.wfbfm.portyj.positions.Position;
import org.springframework.web.socket.WebSocketSession;

sealed interface ViewServerEvent
{
}

record SubscribeEvent(
        WebSocketSession session,
        PositionFilter filter
) implements ViewServerEvent
{
}

record PositionEvent(Position position) implements ViewServerEvent
{
}

record UnsubscribeEvent(String sessionId) implements ViewServerEvent
{
}