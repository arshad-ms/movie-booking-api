package com.example.moviebookingapi.service;

import com.example.moviebookingapi.dto.MovieRequestDTO;
import com.example.moviebookingapi.dto.MovieResponseDTO;
import com.example.moviebookingapi.exception.MovieInUseException;
import com.example.moviebookingapi.exception.ResourceNotFoundException;
import com.example.moviebookingapi.mapper.MovieMapper;
import com.example.moviebookingapi.model.Movie;
import com.example.moviebookingapi.repository.MovieRepository;
import com.example.moviebookingapi.repository.ShowtimeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieService {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private ShowtimeRepository showtimeRepository;

    @Autowired
    private MovieMapper movieMapper;

    public MovieResponseDTO createMovie(MovieRequestDTO movieRequestDTO) {

        Movie movie = movieMapper.toEntity(movieRequestDTO);
        Movie savedMovie = movieRepository.save(movie);

        return movieMapper.toResponseDTO(savedMovie);
    }

    public List<MovieResponseDTO> getMovies(String search, String genre) {

        search = normalizeFilter(search);
        genre = normalizeFilter(genre);

        return movieRepository.searchMovies(search, genre)
                .stream()
                .map(movieMapper::toResponseDTO)
                .toList();
    }

    public MovieResponseDTO updateMovie(Long id, MovieRequestDTO movieRequestDTO) {
        Movie movie = movieRepository.findById(id).orElseThrow(
                ()-> new ResourceNotFoundException("Movie not found with id " + id)
        );

        movie.setTitle(movieRequestDTO.getTitle());
        movie.setGenre(movieRequestDTO.getGenre());
        movie.setDuration(movieRequestDTO.getDuration());

        Movie updatedMovie = movieRepository.save(movie);

        return movieMapper.toResponseDTO(updatedMovie);
    }

    public void deleteMovie(Long id) {
        Movie movie = movieRepository.findById(id).orElseThrow(
                ()-> new ResourceNotFoundException("Movie not found with id " + id)
        );

        boolean movieHasShowtimes = showtimeRepository.existsByMovieId(id);
        if (movieHasShowtimes){
            throw new MovieInUseException( "Cannot delete movie with id " + id + " because it has associated showtimes");
        }

        movieRepository.deleteById(id);
    }

    // helper method
    private String normalizeFilter(String value){
        if(value == null || value.isBlank()){
            return null;
        }
        return value.trim();
    }

}
