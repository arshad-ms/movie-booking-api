package com.example.moviebookingapi.controller;

import com.example.moviebookingapi.dto.BookingRequestDTO;
import com.example.moviebookingapi.dto.BookingResponseDTO;
import com.example.moviebookingapi.model.Booking;
import com.example.moviebookingapi.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @PostMapping
    public BookingResponseDTO createBooking(@Valid @RequestBody BookingRequestDTO bookingRequestDTO) {
        return bookingService.createBooking(bookingRequestDTO);
    }
}
