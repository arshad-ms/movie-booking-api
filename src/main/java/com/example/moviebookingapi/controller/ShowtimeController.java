package com.example.moviebookingapi.controller;

import com.example.moviebookingapi.model.Seat;
import com.example.moviebookingapi.model.Showtime;
import com.example.moviebookingapi.service.ShowtimeService;
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
    public Showtime createShowtime(@RequestParam Long movieId,
                                   @RequestParam Long screenId,
                                   @RequestParam(required = false) Long theaterId,
                                   @RequestParam LocalDateTime startTime,
                                   @RequestParam Integer durationMinutes) {
        return showtimeService.createShowtime(movieId, screenId, theaterId, startTime, durationMinutes);
    }

    @GetMapping("/{id}/seats")
    public List<Seat> getSeatsByShowtime(@PathVariable Long id){
        return showtimeService.getSeatsByShowtime(id);
    }
}