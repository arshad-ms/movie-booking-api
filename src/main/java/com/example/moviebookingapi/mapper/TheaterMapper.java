package com.example.moviebookingapi.mapper;

import com.example.moviebookingapi.dto.TheaterRequestDTO;
import com.example.moviebookingapi.dto.TheaterResponseDTO;
import com.example.moviebookingapi.model.Theater;
import org.springframework.stereotype.Component;

@Component
public class TheaterMapper {
    public Theater toEntity(TheaterRequestDTO theaterRequestDTO) {
        return new Theater(
                null,
                theaterRequestDTO.getName(),
                theaterRequestDTO.getLocation(),
                null
        );
    }

    public TheaterResponseDTO toResponseDTO(Theater theater) {
        int screenCount = theater.getScreens() == null
                ? 0
                : theater.getScreens().size();

        return new TheaterResponseDTO(
                theater.getId(),
                theater.getName(),
                theater.getLocation(),
                screenCount
        );
    }

}