package com.example.moviebookingapi.service;

import com.example.moviebookingapi.dto.MovieRequestDTO;
import com.example.moviebookingapi.dto.MovieResponseDTO;
import com.example.moviebookingapi.mapper.MovieMapper;
import com.example.moviebookingapi.model.Movie;
import com.example.moviebookingapi.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieService {

    @Autowired
    private MovieRepository movieRepository;

    public MovieResponseDTO createMovie(MovieRequestDTO movieRequestDTO) {

        Movie movie = MovieMapper.toEntity(movieRequestDTO);
        Movie savedMovie = movieRepository.save(movie);

        return MovieMapper.toResponseDTO(savedMovie);
    }

    public List<MovieResponseDTO> getMovies() {
        return movieRepository.findAll()
                .stream()
                .map(MovieMapper::toResponseDTO)
                .toList();
    }
}
