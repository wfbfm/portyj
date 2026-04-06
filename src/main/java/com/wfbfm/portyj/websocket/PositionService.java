package com.wfbfm.portyj.websocket;

import com.wfbfm.portyj.positions.Position;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Component
public class PositionService
{
    private final PositionCache cache;
    // private final ReadWriteLock snapshotLock = new ReentrantReadWriteLock();
    // All active subscriptions
    private final Map<String, PositionSubscription> subscriptions = new ConcurrentHashMap<>();
    private final PositionSender sender;

    public PositionService(final PositionCache cache, final PositionSender sender)
    {
        this.cache = cache;
        this.sender = sender;
    }

    public void handleSubscribeRequest(final WebSocketSession session, final PositionFilter filter)
    {
        final String sessionId = session.getId();
        final PositionSubscription sub = new PositionSubscription(sessionId, filter);
        subscriptions.put(sessionId, sub);
        final Set<Long> matchingIds = cache.queryIds(filter);
        matchingIds.forEach(id ->
        {
            final Position p = cache.getById(id);
            if (p != null)
            {
                sub.offer(p);
            }
        });
        startDrainLoop(session);
    }


    private void startDrainLoop(final WebSocketSession session) {
        Thread.ofVirtual().name("drain-" + session.getId()).start(() -> {
            PositionSubscription sub = subscriptions.get(session.getId());
            if (sub == null) return;

            while (sub.isOpen()) {
                try {
                    // Block until something arrives, with a timeout so we can
                    // check isClosed() and send heartbeats periodically
                    Position update = sub.getOutbox().poll(5, TimeUnit.SECONDS);

                    if (update == null) {
                        // Timeout — send a heartbeat and loop
                        // ws.sendHeartbeat(sessionId);
                        continue;
                    }

                    // Drain everything currently in the queue in one batch —
                    // avoids a syscall-per-message under high throughput
                    List<Position> batch = new ArrayList<>();
                    batch.add(update);
                    sub.getOutbox().drainTo(batch, 499); // cap at 500 per flush

                    // TODO - make this a batch
                    batch.forEach(position -> {
                        try
                        {
                            sender.sendPosition(session, position);
                        } catch (Exception e)
                        {
                            throw new RuntimeException(e);
                        }
                    });

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
    }

    public void onPositionUpdate(final Position updated)
    {
        // Write lock only needed if you want atomic snapshot+update
        // consistency. For most cases, just update cache + fan out:
        cache.put(updated);
        for (final PositionSubscription sub : subscriptions.values())
        {
            if (sub.getFilter().matches(updated))
            {
                boolean accepted = sub.offer(updated);
                if (!accepted) handleSlowConsumer(sub);
            }
        }
    }

    private void handleSlowConsumer(PositionSubscription sub) {
//        // Option A (recommended): Drop and flag; client must re-snapshot
//        sub.markStale();
//        ws.sendResyncRequired(sub.getSessionId());
//        subscriptions.remove(sub.getSessionId());
//
//        // Option B: Drop oldest messages (ring buffer semantics)
//        // Use a fixed-capacity circular structure instead of LinkedBlockingQueue
//
//        // Option C: Disconnect the session entirely
//        ws.close(sub.getSessionId());
    }
}