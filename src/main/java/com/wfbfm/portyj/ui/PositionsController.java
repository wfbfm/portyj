package com.wfbfm.portyj.ui;

import com.wfbfm.portyj.model.CanaccordPosition;
import com.wfbfm.portyj.model.Position;
import com.wfbfm.portyj.position.CanaccordPositionLoader;
import com.wfbfm.portyj.position.PositionNormaliser;
import org.apache.el.parser.ELParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Controller
public class PositionsController
{

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final PositionViewRepository repository;
    private final PositionNormaliser normaliser;


    public PositionsController(final PositionViewRepository repository,
                               final PositionNormaliser normaliser)
    {
        this.repository = repository;
        this.normaliser = normaliser;
    }


    @GetMapping("/")
    public String home(Model model)
    {
        logger.info("Received homepage request");
        return "index";
    }


    @GetMapping("/positions")
    public String positions(Model model)
    {
        final List<PositionView> positions = repository.getPositions();
        model.addAttribute("positions", positions);
        logger.info("Rendered {} positions", positions.size());
        final PositionSummaryView summaryView = new PositionSummaryView(positions);
        logger.info("Summary - overall PnL: {} ; daily PnL: {} ; daily change: {}%",
                summaryView.getTotalPnl(), summaryView.getDailyPnl(), summaryView.getDailyPcChange());
        model.addAttribute("summary", summaryView);
        return "fragments/positions-table :: positions-content";
    }

    @PostMapping(
            value = "/uploadPositions",
            consumes = MediaType.TEXT_PLAIN_VALUE
    )
    public ResponseEntity<Void> uploadPositions(@RequestBody String csvFile) throws IOException
    {
        // Convert String -> InputStream
        try (InputStream is = new ByteArrayInputStream(csvFile.getBytes(StandardCharsets.UTF_8)))
        {
            normaliser.initialisePositions(is);
        }

        return ResponseEntity.ok().build();
    }
}
