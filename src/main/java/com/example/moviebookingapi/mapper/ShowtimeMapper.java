package com.example.moviebookingapi.mapper;

import com.example.moviebookingapi.dto.ShowtimeRequestDTO;
import com.example.moviebookingapi.dto.ShowtimeResponseDTO;
import com.example.moviebookingapi.model.Movie;
import com.example.moviebookingapi.model.Screen;
import com.example.moviebookingapi.model.Showtime;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ShowtimeMapper {

    public Showtime toEntity(ShowtimeRequestDTO showtimeRequestDTO, Movie movie, Screen screen) {
        return new Showtime(
                null,
                showtimeRequestDTO.getStartTime(),
                showtimeRequestDTO.getStartTime().plusMinutes(showtimeRequestDTO.getDurationMinutes()),
                movie,
                screen
        );
    }

    public ShowtimeResponseDTO toResponseDTO(Showtime showtime, long availableSeatCount){
        return new ShowtimeResponseDTO(
                showtime.getId(),
                showtime.getMovie().getId(),
                showtime.getMovie().getTitle(),
                showtime.getScreen().getId(),
                showtime.getScreen().getScreenNumber(),
                showtime.getScreen().getTheater().getId(),
                showtime.getScreen().getTheater().getName(),
                showtime.getStartTime(),
                showtime.getEndTime(),
                availableSeatCount
        );
    }
}
