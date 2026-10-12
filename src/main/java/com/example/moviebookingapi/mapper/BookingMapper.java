package com.example.moviebookingapi.mapper;

import com.example.moviebookingapi.dto.BookingResponseDTO;
import com.example.moviebookingapi.dto.SeatResponseDTO;
import com.example.moviebookingapi.model.Booking;
import com.example.moviebookingapi.model.BookingSeat;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BookingMapper {

    public BookingResponseDTO toResponseDTO(Booking booking, List<BookingSeat> bookingSeats) {

        List<SeatResponseDTO> seatResponseDTOs = bookingSeats.stream()
                .map(bookingSeat -> new SeatResponseDTO(
                        bookingSeat.getSeat().getId(),
                        bookingSeat.getSeat().getRowLetter(),
                        bookingSeat.getSeat().getSeatNumber(),
                        bookingSeat.getStatus().name(),
                        bookingSeat.getPriceAtBooking()
                ))
                .toList();

        return new BookingResponseDTO(
                booking.getId(),
                booking.getUser().getEmail(),
                booking.getShowtime().getId(),
                booking.getShowtime().getMovie().getTitle(),
                booking.getShowtime().getScreen().getTheater().getName(),
                booking.getShowtime().getScreen().getScreenNumber(),
                booking.getShowtime().getStartTime(),
                seatResponseDTOs,
                booking.getStatus().name(),
                booking.getTotalPrice(),
                booking.getBookingTime(),
                booking.getExpiresAt()
        );
    }
}
