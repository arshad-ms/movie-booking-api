package com.example.moviebookingapi.service;

import com.example.moviebookingapi.dto.SeatResponseDTO;
import com.example.moviebookingapi.dto.ShowtimeRequestDTO;
import com.example.moviebookingapi.dto.ShowtimeResponseDTO;
import com.example.moviebookingapi.exception.ResourceNotFoundException;
import com.example.moviebookingapi.exception.TheaterScreenMismatchException;
import com.example.moviebookingapi.mapper.SeatMapper;
import com.example.moviebookingapi.mapper.ShowtimeMapper;
import com.example.moviebookingapi.model.*;
import com.example.moviebookingapi.repository.MovieRepository;
import com.example.moviebookingapi.repository.ScreenRepository;
import com.example.moviebookingapi.repository.SeatRepository;
import com.example.moviebookingapi.repository.ShowtimeRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShowtimeService {

    @Autowired
    private ShowtimeRepository showtimeRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private ScreenRepository screenRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private ShowtimeMapper showtimeMapper;

    @Autowired
    private SeatMapper seatMapper;

    @Transactional
    public ShowtimeResponseDTO createShowtime(ShowtimeRequestDTO showtimeRequestDTO) {
        Movie movie = movieRepository.findById(showtimeRequestDTO.getMovieId())
                .orElseThrow(() -> new ResourceNotFoundException("Movie " + showtimeRequestDTO.getMovieId() + " not found"));

        Screen screen = screenRepository.findById(showtimeRequestDTO.getScreenId())
                .orElseThrow(() -> new ResourceNotFoundException("Screen " + showtimeRequestDTO.getScreenId() + " not found"));

        // NEW VALIDATION: If the frontend sent a theatreId, cross-check it!
//        if (theaterId != null && !screen.getTheater().getId().equals(theaterId)) {
//            throw new TheaterScreenMismatchException("Theatre ID mismatch: The screen does not belong to this theatre!");
//        }

        Showtime showtime = showtimeMapper.toEntity(showtimeRequestDTO,  movie, screen);

        Showtime savedShowtime = showtimeRepository.save(showtime);

        long availableSeatCount = seatRepository.countByScreenIdAndStatus(screen.getId(), SeatStatus.AVAILABLE);

        return showtimeMapper.toResponseDTO(savedShowtime, availableSeatCount);
    }

    @Transactional
    public List<SeatResponseDTO> getSeatsByShowtime(Long id){
        Showtime showtime = showtimeRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Showtime " + id + " doesn't exist"));

        Long screenId = showtime.getScreen().getId();
        return seatMapper.toResponseDTOList( seatRepository.findByScreenId(screenId) );
    }
}
