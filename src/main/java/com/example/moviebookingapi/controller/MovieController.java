package com.example.moviebookingapi.controller;

import com.example.moviebookingapi.dto.MovieRequestDTO;
import com.example.moviebookingapi.dto.MovieResponseDTO;
import com.example.moviebookingapi.service.MovieService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
public class MovieController {

    @Autowired
    private MovieService movieService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public MovieResponseDTO createMovie(@Valid @RequestBody MovieRequestDTO movieRequestDTO) {
        return movieService.createMovie(movieRequestDTO);
    }

    @GetMapping
    public List<MovieResponseDTO> getMovies(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String genre) {
        return movieService.getMovies(search, genre);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public MovieResponseDTO updateMovie(@PathVariable Long id, @Valid @RequestBody MovieRequestDTO movieRequestDTO) {
        return movieService.updateMovie(id, movieRequestDTO);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMovie(@PathVariable Long id) {
        movieService.deleteMovie(id);
    }


}
