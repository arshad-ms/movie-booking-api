package com.example.moviebookingapi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Screen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer screenNumber;
    private Integer totalSeats;

    // Many screens belong to 1 Theater
    @ManyToOne(optional = false)
    @JoinColumn(name = "theater_id")
    private Theater theater;

    // 1 Screen has many seats
    @OneToMany(mappedBy = "screen", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Seat> seats = new ArrayList<>();
}
