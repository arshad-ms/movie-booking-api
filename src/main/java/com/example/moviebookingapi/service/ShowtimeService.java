package com.example.moviebookingapi.service;

import com.example.moviebookingapi.dto.SeatResponseDTO;
import com.example.moviebookingapi.dto.ShowtimeRequestDTO;
import com.example.moviebookingapi.dto.ShowtimeResponseDTO;
import com.example.moviebookingapi.exception.ResourceNotFoundException;
import com.example.moviebookingapi.mapper.SeatMapper;
import com.example.moviebookingapi.mapper.ShowtimeMapper;
import com.example.moviebookingapi.model.*;
import com.example.moviebookingapi.repository.MovieRepository;
import com.example.moviebookingapi.repository.ScreenRepository;
import com.example.moviebookingapi.repository.SeatRepository;
import com.example.moviebookingapi.repository.ShowtimeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShowtimeService {

    private final ShowtimeRepository showtimeRepository;
    private final MovieRepository movieRepository;
    private final ScreenRepository screenRepository;
    private final SeatRepository seatRepository;
    private final ShowtimeMapper showtimeMapper;
    private final SeatMapper seatMapper;

    @Transactional
    public ShowtimeResponseDTO createShowtime(ShowtimeRequestDTO showtimeRequestDTO) {
        Movie movie = movieRepository.findById(showtimeRequestDTO.getMovieId())
                .orElseThrow(() -> new ResourceNotFoundException("Movie " + showtimeRequestDTO.getMovieId() + " not found"));

        Screen screen = screenRepository.findById(showtimeRequestDTO.getScreenId())
                .orElseThrow(() -> new ResourceNotFoundException("Screen " + showtimeRequestDTO.getScreenId() + " not found"));

        Showtime showtime = showtimeMapper.toEntity(showtimeRequestDTO, movie, screen);

        Showtime savedShowtime = showtimeRepository.save(showtime);

        long availableSeatCount = seatRepository.countByScreenIdAndStatus(screen.getId(), SeatStatus.AVAILABLE);

        return showtimeMapper.toResponseDTO(savedShowtime, availableSeatCount);
    }

    @Transactional(readOnly = true)
    public List<SeatResponseDTO> getSeatsByShowtime(Long id) {
        Showtime showtime = showtimeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Showtime " + id + " doesn't exist"));

        Long screenId = showtime.getScreen().getId();
        return seatMapper.toResponseDTOList(seatRepository.findByScreenId(screenId));
    }
}