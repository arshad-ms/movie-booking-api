package com.example.moviebookingapi.service;

import com.example.moviebookingapi.model.*;
import com.example.moviebookingapi.repository.BookingRepository;
import com.example.moviebookingapi.repository.SeatRepository;
import com.example.moviebookingapi.repository.ShowtimeRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class BookingService {

    @Autowired
    private ShowtimeRepository showtimeRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Transactional
    public Booking createBooking(Long showtimeId, List<Long> seatIds) {

        if (seatIds == null || seatIds.isEmpty()) {
            throw new RuntimeException("At least one seat must be selected");
        }

        if (seatIds.size() != seatIds.stream().distinct().count()) {
            throw new RuntimeException("Duplicate seat IDs are not allowed");
        }


        Showtime showtime = showtimeRepository.findById(showtimeId)
                .orElseThrow(() -> new RuntimeException("show doesn't exist"));

        List<Seat> selectedSeats = seatRepository.findAllById(seatIds);

        if (selectedSeats.size() != seatIds.size()) {
            throw new RuntimeException("One or more seats don't exist");
        }

        for (Seat seat: selectedSeats) {
            // validation: Ensure all seats belong to the Screen of that Showtime
            if(! seat.getScreen().getId().equals(showtime.getScreen().getId()) ){
                throw new RuntimeException("Seats doesn't match the provided screen");
            }

            // validation: Check for Unavailable seats
            if(!seat.getStatus().equals(SeatStatus.AVAILABLE)){
                throw new RuntimeException("Seat " + seat.getId() + " is not available");
            }
        }

        Booking booking = new Booking();

        // TODO: Get userId from Spring Security Authentication instead of hardcoding it
        booking.setUserId(1L);

        booking.setShowtime(showtime);

        LocalDateTime now = LocalDateTime.now();
        booking.setBookingTime(now);
        booking.setExpiresAt(now.plusMinutes(10));

        booking.setStatus(BookingStatus.PENDING);
        booking.setTotalPrice(selectedSeats.size() * 10.0);

        for(Seat seat: selectedSeats){
            seat.setStatus(SeatStatus.RESERVED);
            seat.setCurrentBooking(booking);
        }

        Booking savedBooking = bookingRepository.save(booking);
        seatRepository.saveAll(selectedSeats);

        return savedBooking;

    }

    @Scheduled(fixedRate = 1 * 60 * 1000 )
    @Transactional
    public void expirePendingBookings(){
        LocalDateTime now = LocalDateTime.now();
        List<Booking> expiredBookings = bookingRepository.findByStatusAndExpiresAtBefore(BookingStatus.PENDING, now);

        for (Booking booking: expiredBookings) {
            booking.setStatus(BookingStatus.EXPIRED);

            // releasing the expired  seats
            List<Seat> seats = seatRepository.findByCurrentBooking(booking);
            for (Seat seat: seats){
                seat.setStatus(SeatStatus.AVAILABLE);
                seat.setCurrentBooking(null);
            }
        }
    }

}
