package com.example.moviebookingapi.specification;

import com.example.moviebookingapi.model.Booking;
import com.example.moviebookingapi.model.BookingStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;

public class BookingSpecification {

    private BookingSpecification() {
        // Utility class
    }

    public static Specification<Booking> belongsToUser(Long userId) {
        return (root, query, cb) -> {
            if (userId == null) {
                return null;
            }

            return cb.equal(
                    root.get("user").get("id"),
                    userId
            );
        };
    }

    public static Specification<Booking> hasStatusIn(
            List<BookingStatus> statuses
    ) {
        return (root, query, cb) -> {
            if (statuses == null || statuses.isEmpty()) {
                return null;
            }

            return root.get("status").in(statuses);
        };
    }

    public static Specification<Booking> bookingTimeBetween(
            LocalDateTime from,
            LocalDateTime to
    ) {
        return (root, query, cb) -> {
            if (from == null && to == null) {
                return null;
            }

            if (from != null && to != null) {
                return cb.between(
                        root.get("bookingTime"),
                        from,
                        to
                );
            }

            if (from != null) {
                return cb.greaterThanOrEqualTo(
                        root.get("bookingTime"),
                        from
                );
            }

            return cb.lessThanOrEqualTo(
                    root.get("bookingTime"),
                    to
            );
        };
    }

    public static Specification<Booking> hasMovieId(Long movieId) {
        return (root, query, cb) -> {
            if (movieId == null) {
                return null;
            }

            return cb.equal(
                    root.get("showtime").get("movie").get("id"),
                    movieId
            );
        };
    }
}
