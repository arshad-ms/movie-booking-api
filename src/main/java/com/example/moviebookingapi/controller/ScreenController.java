package com.example.moviebookingapi.controller;

import com.example.moviebookingapi.dto.ScreenRequestDTO;
import com.example.moviebookingapi.dto.ScreenResponseDTO;
import com.example.moviebookingapi.service.ScreenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/screens")
@RequiredArgsConstructor
public class ScreenController {

    private final ScreenService screenService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ScreenResponseDTO createScreen(@Valid @RequestBody ScreenRequestDTO screenRequestDTO) {
        return screenService.createScreen(screenRequestDTO);
    }

}