package com.wfbfm.portyj.ui;

import com.wfbfm.portyj.model.Position;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class PositionsController
{

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final PositionViewRepository repository;


    public PositionsController(PositionViewRepository repository) {
        this.repository = repository;
    }


    @GetMapping("/")
    public String home(Model model) {
        logger.info("Received homepage request");
        return "index";
    }


    @GetMapping("/positions")
    public String positions(Model model) {
        final List<PositionView> positions = repository.getPositions();
        model.addAttribute("positions", positions);
        logger.info("Rendered {} positions", positions.size());
        final PositionSummaryView summaryView = new PositionSummaryView(positions);
        logger.info("Summary - overall PnL: {} ; daily PnL: {} ; daily change: {}%",
                summaryView.getTotalPnl(), summaryView.getDailyPnl(), summaryView.getDailyPcChange());
        model.addAttribute("summary", summaryView);
        return "fragments/positions-table :: positions-content";
    }
}
