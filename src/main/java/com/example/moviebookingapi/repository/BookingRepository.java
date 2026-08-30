package com.example.moviebookingapi.repository;

import com.example.moviebookingapi.model.Booking;
import com.example.moviebookingapi.model.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByStatusAndExpiresAtBefore(BookingStatus status, LocalDateTime time);
}
