package com.example.moviebookingapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponseDTO {

    private Long id;
    private String userEmail;
    private Long showtimeId;
    private String movieTitle;
    private String theaterName;
    private Integer screenNumber;
    private LocalDateTime showtimeStart;
    private List<SeatResponseDTO> seats;
    private String status;
    private Double totalPrice;
    private LocalDateTime bookingTime;
    private LocalDateTime expiresAt;
}