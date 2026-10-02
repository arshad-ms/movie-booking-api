package com.example.moviebookingapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeatResponseDTO {

    private Long id;
    private String rowLetter;
    private Integer seatNumber;
    private String status;

    // TODO: Populate when pricing is implemented.
    private Double price;
}
