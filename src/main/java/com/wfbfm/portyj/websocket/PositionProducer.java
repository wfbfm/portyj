package com.wfbfm.portyj.websocket;

import com.wfbfm.portyj.ui.PositionView;
import com.wfbfm.portyj.ui.PositionViewRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.Queue;

@Component
public class PositionProducer
{

    private final PositionsWebSocketHandler positionsWebSocketHandler;
    private final Queue<PositionView> positions = new LinkedList<>();

    public PositionProducer(PositionViewRepository positionViewRepository, PositionsWebSocketHandler positionsWebSocketHandler)
    {
        positionViewRepository.getAllPositions().forEach(view -> {
            positions.offer(view);
        });
        this.positionsWebSocketHandler = positionsWebSocketHandler;
    }

    // with some imagination, this could be a Kafka topic
    @Scheduled(fixedRate = 500)
    public void produce()
    {
        final PositionView view = positions.poll();
        positionsWebSocketHandler.broadcastPosition(view);
    }
}
