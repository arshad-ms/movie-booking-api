package com.example.moviebookingapi.mapper;

import com.example.moviebookingapi.dto.MovieRequestDTO;
import com.example.moviebookingapi.dto.MovieResponseDTO;
import com.example.moviebookingapi.model.Movie;

public class MovieMapper {

    public static Movie toEntity(MovieRequestDTO movieDTO) {
        return new Movie(
                null,
                movieDTO.getTitle(),
                movieDTO.getGenre(),
                movieDTO.getDuration()
        );
    }

    public static MovieResponseDTO toResponseDTO(Movie movie) {
        return new MovieResponseDTO(
                movie.getId(),
                movie.getTitle(),
                movie.getGenre(),
                movie.getDuration()
        );
    }

}
