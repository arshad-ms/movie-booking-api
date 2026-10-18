package com.example.moviebookingapi.repository;

import com.example.moviebookingapi.model.Booking;
import com.example.moviebookingapi.model.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByStatusAndExpiresAtBefore(BookingStatus status, LocalDateTime time);

    Page<Booking> findByUserId(Long userId, Pageable pageable);

    @Query("""
        SELECT b FROM Booking b
        WHERE (CAST(:userId AS Long) IS NULL OR b.user.id = :userId)
          AND b.status IN :statuses
          AND (CAST(:fromTime AS LocalDateTime) IS NULL OR b.bookingTime >= :fromTime)
          AND (CAST(:toTime AS LocalDateTime) IS NULL OR b.bookingTime <= :toTime)
          AND (CAST(:movieId AS Long) IS NULL OR b.showtime.movie.id = :movieId)
    """)
    Page<Booking> findWithFilters(
            @Param("userId") Long userId,
            @Param("statuses") List<BookingStatus> statuses,
            @Param("fromTime") LocalDateTime fromTime,
            @Param("toTime") LocalDateTime toTime,
            @Param("movieId") Long movieId,
            Pageable pageable
    );


}
