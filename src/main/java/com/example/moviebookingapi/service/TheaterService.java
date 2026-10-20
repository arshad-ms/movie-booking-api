package com.example.moviebookingapi.service;

import com.example.moviebookingapi.dto.TheaterRequestDTO;
import com.example.moviebookingapi.dto.TheaterResponseDTO;
import com.example.moviebookingapi.mapper.TheaterMapper;
import com.example.moviebookingapi.model.Theater;
import com.example.moviebookingapi.repository.TheaterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TheaterService {

    private final TheaterRepository theaterRepository;
    private final TheaterMapper theaterMapper;

    @Transactional
    public TheaterResponseDTO createTheater(TheaterRequestDTO theaterRequestDTO) {
        Theater theater = theaterMapper.toEntity(theaterRequestDTO);
        Theater savedTheater = theaterRepository.save(theater);

        return theaterMapper.toResponseDTO(savedTheater);
    }
}