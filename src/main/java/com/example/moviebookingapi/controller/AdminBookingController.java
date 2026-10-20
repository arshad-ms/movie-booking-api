package com.example.moviebookingapi.controller;

import com.example.moviebookingapi.dto.BookingResponseDTO;
import com.example.moviebookingapi.dto.PagedResponseDTO;
import com.example.moviebookingapi.model.BookingStatus;
import com.example.moviebookingapi.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin/bookings")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminBookingController {

    private final BookingService bookingService;

    @GetMapping
    public PagedResponseDTO<BookingResponseDTO> getAllBookings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) List<BookingStatus> status,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fromDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate toDate,
            @RequestParam(required = false) Long movieId
    ) {
        return bookingService.getAllBookings(
                page,
                size,
                status,
                fromDate,
                toDate,
                movieId
        );
    }
}