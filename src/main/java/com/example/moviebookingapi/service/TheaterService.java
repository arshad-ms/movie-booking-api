package com.example.moviebookingapi.service;

import com.example.moviebookingapi.dto.TheaterRequestDTO;
import com.example.moviebookingapi.dto.TheaterResponseDTO;
import com.example.moviebookingapi.mapper.TheaterMapper;
import com.example.moviebookingapi.model.Theater;
import com.example.moviebookingapi.repository.TheaterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TheaterService {

    @Autowired
    private TheaterRepository theaterRepository;

    @Autowired
    private TheaterMapper theaterMapper;

    public TheaterResponseDTO createTheater(TheaterRequestDTO theaterRequestDTO) {

        Theater theater = theaterMapper.toEntity(theaterRequestDTO);
        Theater savedTheater = theaterRepository.save(theater);

        return theaterMapper.toResponseDTO(savedTheater);
    }
}
