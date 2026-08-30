package com.example.moviebookingapi.repository;

import com.example.moviebookingapi.model.Theater;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TheaterRepository extends JpaRepository<Theater, Long> {
}
