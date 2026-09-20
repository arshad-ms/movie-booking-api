package com.example.moviebookingapi.controller;

import com.example.moviebookingapi.dto.ScreenRequestDTO;
import com.example.moviebookingapi.dto.ScreenResponseDTO;
import com.example.moviebookingapi.model.Screen;
import com.example.moviebookingapi.service.ScreenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/screens")
public class ScreenController {

    @Autowired
    private ScreenService screenService;

    @PostMapping
    public ScreenResponseDTO createScreen(@Valid @RequestBody ScreenRequestDTO screenRequestDTO) {
        return screenService.createScreen(screenRequestDTO);
    }

}