package com.wfbfm.portyj.websocket;

import com.wfbfm.portyj.positions.Position;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

@Component
public class ViewServerEventLoop
{
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final BlockingQueue<ViewServerEvent> queue = new LinkedBlockingQueue<>();
    private final PositionCache cache;
    private final Map<String, PositionSubscription> subscriptions = new ConcurrentHashMap<>();
    private final PositionSender sender;

    public ViewServerEventLoop(final PositionCache cache, final PositionSender sender)
    {
        this.cache = cache;
        this.sender = sender;
        ExecutorService eventLoopExecutor = Executors.newSingleThreadExecutor();
        eventLoopExecutor.submit(this::runLoop);
    }

    public void submit(ViewServerEvent event)
    {
        queue.offer(event);
    }

    private void runLoop()
    {
        while (true)
        {
            try
            {
                ViewServerEvent event = queue.take();
                handle(event);
            } catch (Exception e)
            {
                // log + continue
            }
        }
    }

    private void handle(final ViewServerEvent event)
    {
        if (event instanceof SubscribeEvent)
        {
            handleSubscribe((SubscribeEvent) event);
            return;
        }
        if (event instanceof UnsubscribeEvent)
        {
            handleUnsubscribe((UnsubscribeEvent) event);
            return;
        }
        if (event instanceof PositionEvent)
        {
            handlePositionEvent((PositionEvent) event);
            return;
        }
    }

    private void handleSubscribe(final SubscribeEvent event) {
        final String sessionId = event.session().getId();
        final PositionSubscription sub = new PositionSubscription(sessionId, event.filter());

        final Set<Long> matchingIds = cache.queryIds(sub.getFilter());
        matchingIds.forEach(id ->
        {
            final Position p = cache.getById(id);
            if (p != null)
            {
                sub.offer(p);
            }
        });
        subscriptions.put(sessionId, sub);
        logger.info("Subscribing session {}", sessionId);
        startDrainLoop(event.session());
    }

    private void handleUnsubscribe(final UnsubscribeEvent event) {
        logger.info("Unsubscribing session {}", event.sessionId());
        final PositionSubscription sub = subscriptions.remove(event.sessionId());
        if (sub != null)
        {
            sub.close();
        }
    }

    private void handlePositionEvent(final PositionEvent event)
    {
        final Position position = event.position();
        cache.put(position);
        for (final PositionSubscription sub : subscriptions.values())
        {
            if (sub.getFilter().matches(position))
            {
                boolean accepted = sub.offer(position);
                if (!accepted) handleSlowConsumer(sub);
            }
        }
    }

    private void startDrainLoop(final WebSocketSession session)
    {
        Thread.ofVirtual().name("drain-" + session.getId()).start(() ->
        {
            PositionSubscription sub = subscriptions.get(session.getId());
            if (sub == null) return;

            while (sub.isOpen())
            {
                try
                {
                    // Block until something arrives, with a timeout so we can
                    // check isClosed() and send heartbeats periodically
                    Position update = sub.getOutbox().poll(5, TimeUnit.SECONDS);

                    if (update == null)
                    {
                        // Timeout — send a heartbeat and loop
                        continue;
                    }

                    // Drain everything currently in the queue in one batch —
                    // avoids a syscall-per-message under high throughput
                    List<Position> batch = new ArrayList<>();
                    batch.add(update);
                    sub.getOutbox().drainTo(batch, 499); // cap at 500 per flush

                    // TODO - make this a batch
                    batch.forEach(position ->
                    {
                        try
                        {
                            sender.sendPosition(session, position);
                        } catch (Exception e)
                        {
                            throw new RuntimeException(e);
                        }
                    });

                } catch (InterruptedException e)
                {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            logger.info("Subscription closed - stopping drain thread for session {}", session.getId());
        });
    }

    private void handleSlowConsumer(PositionSubscription sub)
    {
        // TODO - drop the consumer and have it reconnect
    }
}