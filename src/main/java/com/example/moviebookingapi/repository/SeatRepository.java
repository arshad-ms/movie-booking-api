package com.example.moviebookingapi.repository;

import com.example.moviebookingapi.model.Booking;
import com.example.moviebookingapi.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByCurrentBooking(Booking booking);

    List<Seat> findByScreenId(Long screenId);
}
