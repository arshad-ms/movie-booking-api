package com.example.moviebookingapi.controller;

import com.example.moviebookingapi.dto.TheaterRequestDTO;
import com.example.moviebookingapi.dto.TheaterResponseDTO;
import com.example.moviebookingapi.service.TheaterService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasRole('ADMIN')")
    public TheaterResponseDTO createTheatre(@Valid @RequestBody TheaterRequestDTO theatreRequestDTO) {
        return theatreService.createTheater(theatreRequestDTO);
    }
}