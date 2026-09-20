package com.example.moviebookingapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScreenResponseDTO {

    private Long id;
    private Integer screenNumber;
    private Integer totalSeats;
    private Long theatreId;
    private String theatreName;
    private Integer seatCount;
}
