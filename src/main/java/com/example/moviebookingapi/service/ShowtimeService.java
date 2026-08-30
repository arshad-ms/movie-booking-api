package com.example.moviebookingapi.service;

import com.example.moviebookingapi.model.Movie;
import com.example.moviebookingapi.model.Screen;
import com.example.moviebookingapi.model.Seat;
import com.example.moviebookingapi.model.Showtime;
import com.example.moviebookingapi.repository.MovieRepository;
import com.example.moviebookingapi.repository.ScreenRepository;
import com.example.moviebookingapi.repository.SeatRepository;
import com.example.moviebookingapi.repository.ShowtimeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShowtimeService {

    @Autowired
    private ShowtimeRepository showtimeRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private ScreenRepository screenRepository;

    @Autowired
    private SeatRepository seatRepository;

    public Showtime createShowtime(Long movieId,
                                   Long screenId,
                                   Long theaterId,
                                   LocalDateTime startTime,
                                   Integer durationMinutes) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new RuntimeException("Movie not found"));

        Screen screen = screenRepository.findById(screenId)
                .orElseThrow(() -> new RuntimeException("Screen not found"));

        // NEW VALIDATION: If the frontend sent a theatreId, cross-check it!
        if (theaterId != null && !screen.getTheater().getId().equals(theaterId)) {
            throw new RuntimeException("Theatre ID mismatch: The screen does not belong to this theatre!");
        }

        Showtime showtime = new Showtime();
        showtime.setMovie(movie);
        showtime.setScreen(screen);
        showtime.setStartTime(startTime);
        showtime.setEndTime(startTime.plusMinutes(durationMinutes));

        return showtimeRepository.save(showtime);
    }

    public List<Seat> getSeatsByShowtime(Long id){
        Showtime showtime = showtimeRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Showtime doesn't exist"));

        Long screenId = showtime.getScreen().getId();
        return seatRepository.findByScreenId(screenId);
    }
}
