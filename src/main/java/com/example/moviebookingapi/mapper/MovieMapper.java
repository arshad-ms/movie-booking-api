package com.example.moviebookingapi.mapper;

import com.example.moviebookingapi.dto.MovieDTO;
import com.example.moviebookingapi.model.Movie;

public class MovieMapper {

    public static MovieDTO toResponse(Movie movie) {
        return new MovieDTO(
                movie.getId(),
                movie.getTitle(),
                movie.getGenre(),
                movie.getDuration()
        );
    }

    public static Movie toEntity(MovieDTO movieDTO) {
        return new Movie(
                movieDTO.getId(),
                movieDTO.getTitle(),
                movieDTO.getGenre(),
                movieDTO.getDuration()
        );
    }

}
