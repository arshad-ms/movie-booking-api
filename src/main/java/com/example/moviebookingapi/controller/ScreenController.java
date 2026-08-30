package com.example.moviebookingapi.controller;

import com.example.moviebookingapi.model.Screen;
import com.example.moviebookingapi.service.ScreenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/screens")
public class ScreenController {

    @Autowired
    private ScreenService screenService;

    @PostMapping
    public Screen createScreen(@RequestParam Long theatreId,
                               @RequestParam Integer screenNumber,
                               @RequestParam Integer totalSeats) {
        return screenService.createScreen(theatreId, screenNumber, totalSeats);
    }

}