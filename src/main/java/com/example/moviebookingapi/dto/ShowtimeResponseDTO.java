package com.example.moviebookingapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShowtimeResponseDTO {

    private Long id;
    private Long movieId;
    private String movieTitle;
    private Long screenId;
    private Integer screenNumber;
    private Long theaterId;
    private String theaterName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private long availableSeatCount;
}
