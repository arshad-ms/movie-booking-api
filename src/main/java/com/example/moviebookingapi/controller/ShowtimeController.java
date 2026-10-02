package com.example.moviebookingapi.controller;

import com.example.moviebookingapi.dto.SeatResponseDTO;
import com.example.moviebookingapi.dto.ShowtimeRequestDTO;
import com.example.moviebookingapi.dto.ShowtimeResponseDTO;
import com.example.moviebookingapi.mapper.SeatMapper;
import com.example.moviebookingapi.model.Seat;
import com.example.moviebookingapi.model.Showtime;
import com.example.moviebookingapi.service.ShowtimeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/showtimes")
public class ShowtimeController {

    @Autowired
    private ShowtimeService showtimeService;

    @PostMapping
    public ShowtimeResponseDTO createShowtime(@Valid @RequestBody ShowtimeRequestDTO showtimeRequestDTO) {
        return showtimeService.createShowtime(showtimeRequestDTO);
    }

    @GetMapping("/{id}/seats")
    public List<SeatResponseDTO> getSeatsByShowtime(@PathVariable Long id){
        return showtimeService.getSeatsByShowtime(id);
    }
}