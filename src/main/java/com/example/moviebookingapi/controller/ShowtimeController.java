package com.example.moviebookingapi.controller;

import com.example.moviebookingapi.dto.SeatResponseDTO;
import com.example.moviebookingapi.dto.ShowtimeRequestDTO;
import com.example.moviebookingapi.dto.ShowtimeResponseDTO;
import com.example.moviebookingapi.service.ShowtimeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/showtimes")
@RequiredArgsConstructor
public class ShowtimeController {

    private final ShowtimeService showtimeService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ShowtimeResponseDTO createShowtime(@Valid @RequestBody ShowtimeRequestDTO showtimeRequestDTO) {
        return showtimeService.createShowtime(showtimeRequestDTO);
    }

    @GetMapping("/{id}/seats")
    public List<SeatResponseDTO> getSeatsByShowtime(@PathVariable Long id) {
        return showtimeService.getSeatsByShowtime(id);
    }
}