package com.example.moviebookingapi.mapper;

import com.example.moviebookingapi.dto.BookingResponseDTO;
import com.example.moviebookingapi.model.Booking;
import com.example.moviebookingapi.model.Seat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BookingMapper {

    @Autowired
    private SeatMapper seatMapper;

    public BookingResponseDTO toResponseDTO(Booking booking, List<Seat> seats) {
        return new BookingResponseDTO(
                booking.getId(),
                booking.getUser().getEmail(),
                booking.getShowtime().getId(),
                booking.getShowtime().getMovie().getTitle(),
                booking.getShowtime().getScreen().getTheater().getName(),
                booking.getShowtime().getScreen().getScreenNumber(),
                booking.getShowtime().getStartTime(),
                seatMapper.toResponseDTOList(seats),
                booking.getStatus().name(),
                booking.getTotalPrice(),
                booking.getBookingTime(),
                booking.getExpiresAt()
        );
    }
}
