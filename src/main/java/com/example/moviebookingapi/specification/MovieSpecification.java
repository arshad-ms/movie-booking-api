package com.example.moviebookingapi.specification;

import com.example.moviebookingapi.model.Movie;
import org.springframework.data.jpa.domain.Specification;

import java.util.Locale;

public class MovieSpecification {

    private MovieSpecification() {
        // Utility class
    }

    public static Specification<Movie> titleContains(String search) {
        return (root, query, cb) -> {
            if (search == null || search.isEmpty()) {
                return null;
            }

            String normalizedSearch = search.trim().toLowerCase(Locale.ROOT);

            return cb.like(
                    cb.lower(root.get("title")),
                    "%" + normalizedSearch + "%"
            );
        };
    }

    public static Specification<Movie> hasGenre(String genre) {
        return (root, query, cb) ->
        {
            if (genre == null || genre.isEmpty()) {
                return null;
            }

            String normalizedGenre = genre.trim().toLowerCase(Locale.ROOT);

            return cb.equal(
                    cb.lower(root.get("genre")),
                    normalizedGenre
            );
        };

    }
}
