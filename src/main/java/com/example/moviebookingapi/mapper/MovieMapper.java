package com.example.moviebookingapi.mapper;

import com.example.moviebookingapi.dto.MovieRequestDTO;
import com.example.moviebookingapi.dto.MovieResponseDTO;
import com.example.moviebookingapi.model.Movie;
import org.springframework.stereotype.Component;

@Component
public class MovieMapper {

    public Movie toEntity(MovieRequestDTO movieDTO) {
        return new Movie(
                null,
                movieDTO.getTitle(),
                movieDTO.getGenre(),
                movieDTO.getDuration()
        );
    }

    public MovieResponseDTO toResponseDTO(Movie movie) {
        return new MovieResponseDTO(
                movie.getId(),
                movie.getTitle(),
                movie.getGenre(),
                movie.getDuration()
        );
    }

}
