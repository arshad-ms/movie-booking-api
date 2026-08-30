package com.example.moviebookingapi.controller;

import com.example.moviebookingapi.dto.BookingRequest;
import com.example.moviebookingapi.model.Booking;
import com.example.moviebookingapi.model.Seat;
import com.example.moviebookingapi.model.Showtime;
import com.example.moviebookingapi.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @PostMapping
    public Booking createBooking(@RequestBody BookingRequest bookingRequest) {
        return bookingService.createBooking(bookingRequest.getShowtimeId(), bookingRequest.getSeatIds());
    }
}
