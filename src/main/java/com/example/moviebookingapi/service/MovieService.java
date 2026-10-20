package com.example.moviebookingapi.service;

import com.example.moviebookingapi.dto.MovieRequestDTO;
import com.example.moviebookingapi.dto.MovieResponseDTO;
import com.example.moviebookingapi.exception.MovieInUseException;
import com.example.moviebookingapi.exception.ResourceNotFoundException;
import com.example.moviebookingapi.mapper.MovieMapper;
import com.example.moviebookingapi.model.Movie;
import com.example.moviebookingapi.repository.MovieRepository;
import com.example.moviebookingapi.repository.ShowtimeRepository;
import com.example.moviebookingapi.specification.MovieSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;
    private final ShowtimeRepository showtimeRepository;
    private final MovieMapper movieMapper;

    @Transactional
    public MovieResponseDTO createMovie(MovieRequestDTO movieRequestDTO) {
        Movie movie = movieMapper.toEntity(movieRequestDTO);
        Movie savedMovie = movieRepository.save(movie);

        return movieMapper.toResponseDTO(savedMovie);
    }

    @Transactional(readOnly = true)
    public List<MovieResponseDTO> getMovies(String search, String genre) {

        search = normalizeFilter(search);
        genre = normalizeFilter(genre);

        Specification<Movie> spec = Specification.allOf(
                MovieSpecification.titleContains(search),
                MovieSpecification.hasGenre(genre)
        );

        return movieRepository.findAll(spec)
                .stream()
                .map(movieMapper::toResponseDTO)
                .toList();
    }

    @Transactional
    public MovieResponseDTO updateMovie(Long id, MovieRequestDTO movieRequestDTO) {
        Movie movie = movieRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Movie not found with id " + id)
        );

        movie.setTitle(movieRequestDTO.getTitle());
        movie.setGenre(movieRequestDTO.getGenre());
        movie.setDuration(movieRequestDTO.getDuration());

        Movie updatedMovie = movieRepository.save(movie);

        return movieMapper.toResponseDTO(updatedMovie);
    }

    @Transactional
    public void deleteMovie(Long id) {
        if (!movieRepository.existsById(id)) {
            throw new ResourceNotFoundException("Movie not found with id " + id);
        }

        boolean movieHasShowtimes = showtimeRepository.existsByMovieId(id);
        if (movieHasShowtimes) {
            throw new MovieInUseException("Cannot delete movie with id " + id + " because it has associated showtimes");
        }

        movieRepository.deleteById(id);
    }

    private String normalizeFilter(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

}