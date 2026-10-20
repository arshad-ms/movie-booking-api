package com.example.moviebookingapi.controller;

import com.example.moviebookingapi.dto.TheaterRequestDTO;
import com.example.moviebookingapi.dto.TheaterResponseDTO;
import com.example.moviebookingapi.service.TheaterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/theaters")
@RequiredArgsConstructor
public class TheaterController {

    private final TheaterService theaterService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public TheaterResponseDTO createTheater(@Valid @RequestBody TheaterRequestDTO theaterRequestDTO) {
        return theaterService.createTheater(theaterRequestDTO);
    }
}