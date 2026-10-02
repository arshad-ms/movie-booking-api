package com.example.moviebookingapi.mapper;

import com.example.moviebookingapi.dto.ScreenRequestDTO;
import com.example.moviebookingapi.dto.ScreenResponseDTO;
import com.example.moviebookingapi.model.Screen;
import com.example.moviebookingapi.model.Theater;
import org.springframework.stereotype.Component;

@Component
public class ScreenMapper {

    public Screen toEntity(ScreenRequestDTO screenDTO, Theater theater) {
        return new Screen(
                null,
                screenDTO.getScreenNumber(),
                screenDTO.getTotalSeats(),
                theater,
                null
        );
    }

    public ScreenResponseDTO toResponseDTO(Screen screen) {
        return new ScreenResponseDTO(
                screen.getId(),
                screen.getScreenNumber(),
                screen.getTotalSeats(),
                screen.getTheater().getId(),
                screen.getTheater().getName(),
                screen.getSeats() != null ? screen.getSeats().size() : 0
        );
    }

}
