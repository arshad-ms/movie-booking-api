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

    @Autowired
    private MovieMapper movieMapper;

    public MovieResponseDTO createMovie(MovieRequestDTO movieRequestDTO) {

        Movie movie = movieMapper.toEntity(movieRequestDTO);
        Movie savedMovie = movieRepository.save(movie);

        return movieMapper.toResponseDTO(savedMovie);
    }

    public List<MovieResponseDTO> getMovies() {
        return movieRepository.findAll()
                .stream()
                .map(movieMapper::toResponseDTO)
                .toList();
    }
}
