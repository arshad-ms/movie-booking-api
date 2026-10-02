package com.example.moviebookingapi.mapper;

import com.example.moviebookingapi.dto.SeatResponseDTO;
import com.example.moviebookingapi.model.Seat;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SeatMapper {
    public SeatResponseDTO toResponseDTO(Seat seat) {
        return new SeatResponseDTO(
                seat.getId(),
                seat.getRowLetter(),
                seat.getSeatNumber(),
                seat.getStatus().name(),
                null
        );
    }

    public List<SeatResponseDTO> toResponseDTOList(List<Seat> seats) {
        return seats.stream()
                .map(this::toResponseDTO)
                .toList();
    }

}
