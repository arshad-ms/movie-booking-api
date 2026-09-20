package com.example.moviebookingapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieResponseDTO {

    private Long id;
    private String title;
    private String genre;
    private Integer duration;


}