package com.wfbfm.portyj.websocket;

import com.wfbfm.portyj.positions.Position;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class PositionSubscription
{
    private final String sessionId;
    private final PositionFilter filter;
    // Bounded queue: if a slow client falls behind, you drop or disconnect
    // rather than letting memory balloon
    private final BlockingQueue<Position> outbox = new LinkedBlockingQueue<>(10_000);
    private boolean isOpen = true;

    public PositionSubscription(final String sessionId, final PositionFilter filter)
    {
        this.sessionId = sessionId;
        this.filter = filter;
    }

    public boolean offer(Position position)
    {
        return outbox.offer(position); // non-blocking, returns false if full
    }
    // ... drainTo() called by your websocket write loop


    public String getSessionId()
    {
        return sessionId;
    }

    public PositionFilter getFilter()
    {
        return filter;
    }

    public boolean isOpen()
    {
        return isOpen;
    }

    public BlockingQueue<Position> getOutbox()
    {
        return outbox;
    }
}