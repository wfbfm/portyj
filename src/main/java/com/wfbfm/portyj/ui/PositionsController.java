package com.wfbfm.portyj.ui;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PositionsController
{

    private final PositionViewRepository repository;


    public PositionsController(PositionViewRepository repository) {
        this.repository = repository;
    }


    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("positions", repository.getPositions());
        return "positions";
    }


    @GetMapping("/positions/table")
    public String table(Model model) {
        model.addAttribute("positions", repository.getPositions());
        return "fragments/positions-table :: table";
    }
}
