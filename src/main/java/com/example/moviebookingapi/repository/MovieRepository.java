package com.example.moviebookingapi.repository;

import com.example.moviebookingapi.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    @Query("""
        SELECT m FROM Movie m
        WHERE (
            CAST(:search AS String) IS NULL
            OR LOWER(m.title) LIKE LOWER(
                CONCAT('%', CAST(:search AS String), '%')
            )
        )
        AND (
            CAST(:genre AS String) IS NULL
            OR LOWER(m.genre) = LOWER(CAST(:genre AS String))
        )
    """)
    List<Movie> searchMovies(
            @Param("search") String search,
            @Param("genre") String genre
    );
}
