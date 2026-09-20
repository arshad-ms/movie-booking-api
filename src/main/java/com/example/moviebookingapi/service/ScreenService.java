package com.example.moviebookingapi.service;

import com.example.moviebookingapi.dto.ScreenRequestDTO;
import com.example.moviebookingapi.dto.ScreenResponseDTO;
import com.example.moviebookingapi.exception.ResourceNotFoundException;
import com.example.moviebookingapi.mapper.ScreenMapper;
import com.example.moviebookingapi.model.Screen;
import com.example.moviebookingapi.model.Seat;
import com.example.moviebookingapi.model.SeatStatus;
import com.example.moviebookingapi.model.Theater;
import com.example.moviebookingapi.repository.ScreenRepository;
import com.example.moviebookingapi.repository.TheaterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ScreenService {

    @Autowired
    private ScreenRepository screenRepository;

    @Autowired
    private TheaterRepository theaterRepository;

    public ScreenResponseDTO createScreen(ScreenRequestDTO screenRequestDTO) {
        Theater theatre = theaterRepository.findById(screenRequestDTO.getTheatreId())
                .orElseThrow(() -> new ResourceNotFoundException("Theatre " + screenRequestDTO.getTheatreId() + " not found"));

        Screen screen = ScreenMapper.toEntity(screenRequestDTO, theatre);

        // Auto-generate seats (e.g., A1, A2... B1, B2...)
        List<Seat> seats = new ArrayList<>();

        int totalSeats = screenRequestDTO.getTotalSeats();

        // 10 seats per row
        int rows = (int) Math.ceil(totalSeats / 10.0);

        for (int row = 0; row < rows; row++) {

            char rowLetter = (char) ('A' + row);
            int seatsInThisRow = Math.min(10, totalSeats - (row * 10));

            for (int seatNum = 1; seatNum <= seatsInThisRow; seatNum++) {
                Seat seat = new Seat();
                seat.setRowLetter(String.valueOf(rowLetter));
                seat.setSeatNumber(seatNum);
                seat.setStatus(SeatStatus.AVAILABLE);
                seat.setScreen(screen);
                seats.add(seat);
            }
        }
        screen.setSeats(seats);

        Screen savedScreen = screenRepository.save(screen);

        return ScreenMapper.toResponseDTO(savedScreen);
    }

}
