package com.example.moviebookingapi.controller;

import com.example.moviebookingapi.model.Theater;
import com.example.moviebookingapi.service.TheaterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/theaters")
public class TheaterController {

    @Autowired
    private TheaterService theatreService;

    @PostMapping
    public Theater createTheatre(@RequestBody Theater theatre) {
        return theatreService.createTheater(theatre);
    }
}