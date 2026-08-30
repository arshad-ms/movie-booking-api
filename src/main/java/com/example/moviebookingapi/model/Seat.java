package com.example.moviebookingapi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String rowLetter;
    private Integer seatNumber;

    @Enumerated(EnumType.STRING)
    private SeatStatus status = SeatStatus.AVAILABLE;

    @Version
    private Integer version;

    @ManyToOne(optional = false)
    @JoinColumn(name = "screen_id")
    @JsonIgnore
    private Screen screen;

    @ManyToOne(optional = true)
    @JoinColumn(name = "currentBooking_id")
    @JsonIgnore
    private Booking currentBooking;

}

