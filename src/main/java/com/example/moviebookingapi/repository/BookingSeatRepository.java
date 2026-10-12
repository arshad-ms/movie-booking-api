package com.example.moviebookingapi.repository;

import com.example.moviebookingapi.model.Booking;
import com.example.moviebookingapi.model.BookingSeat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingSeatRepository extends JpaRepository<BookingSeat, Long> {

    List<BookingSeat> findByBooking(Booking booking);

    List<BookingSeat> findByBookingId(Long bookingId);
}
