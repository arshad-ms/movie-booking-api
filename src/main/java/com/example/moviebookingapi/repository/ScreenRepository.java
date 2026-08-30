package com.example.moviebookingapi.repository;

import com.example.moviebookingapi.model.Screen;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScreenRepository extends JpaRepository<Screen,Long> {
}
