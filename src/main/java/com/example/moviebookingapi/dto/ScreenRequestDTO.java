package com.example.moviebookingapi.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScreenRequestDTO {

    @NotNull
    @Positive
    private Long theatreId;

    @NotNull
    @Positive
    private Integer screenNumber;

    @NotNull
    @Positive
    private Integer totalSeats;
}
