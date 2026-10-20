package com.example.moviebookingapi.controller;

import com.example.moviebookingapi.dto.BookingRequestDTO;
import com.example.moviebookingapi.dto.BookingResponseDTO;
import com.example.moviebookingapi.dto.PagedResponseDTO;
import com.example.moviebookingapi.model.BookingStatus;
import com.example.moviebookingapi.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingResponseDTO createBooking(@Valid @RequestBody BookingRequestDTO bookingRequestDTO) {
        return bookingService.createBooking(bookingRequestDTO);
    }

    @GetMapping("/me")
    public PagedResponseDTO<BookingResponseDTO> getMyBookings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) List<BookingStatus> status
    ) {
        return bookingService.getMyBookings(page, size, status);
    }

    @GetMapping("/{id}")
    public BookingResponseDTO getBookingById(@PathVariable Long id) {
        return bookingService.getBookingById(id);
    }

    @DeleteMapping("/{id}")
    public BookingResponseDTO cancelBooking(@PathVariable Long id) {
        return bookingService.cancelBooking(id);
    }
}