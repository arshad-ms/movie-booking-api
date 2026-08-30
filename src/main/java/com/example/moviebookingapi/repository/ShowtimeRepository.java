package com.example.moviebookingapi.repository;

import com.example.moviebookingapi.model.Showtime;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {
}
