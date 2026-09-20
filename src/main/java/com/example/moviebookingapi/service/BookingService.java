package com.example.moviebookingapi.service;

import com.example.moviebookingapi.exception.InvalidBookingException;
import com.example.moviebookingapi.exception.ResourceNotFoundException;
import com.example.moviebookingapi.exception.SeatUnavailableException;
import com.example.moviebookingapi.exception.UserNotAuthenticatedException;
import com.example.moviebookingapi.model.*;
import com.example.moviebookingapi.repository.BookingRepository;
import com.example.moviebookingapi.repository.SeatRepository;
import com.example.moviebookingapi.repository.ShowtimeRepository;
import com.example.moviebookingapi.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingService {

    @Autowired
    private ShowtimeRepository showtimeRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public Booking createBooking(Long showtimeId, List<Long> seatIds) {

        if (seatIds == null || seatIds.isEmpty()) {
            throw new InvalidBookingException("At least one seat must be selected");
        }

        if (seatIds.size() != seatIds.stream().distinct().count()) {
            throw new InvalidBookingException("Duplicate seat IDs are not allowed");
        }


        Showtime showtime = showtimeRepository.findById(showtimeId)
                .orElseThrow(() -> new ResourceNotFoundException("Showtime not found with id: " + showtimeId));

        List<Seat> selectedSeats = seatRepository.findAllById(seatIds);

        if (selectedSeats.size() != seatIds.size()) {
            throw new ResourceNotFoundException("One or more seats don't exist");
        }

        for (Seat seat: selectedSeats) {
            // validation: Ensure all seats belong to the Screen of that Showtime
            if(!seat.getScreen().getId().equals(showtime.getScreen().getId())){
                throw new InvalidBookingException("Seat " + seat.getId() + " does not belong to the show's screen");
            }

            // validation: Check for Unavailable seats
            if(!seat.getStatus().equals(SeatStatus.AVAILABLE)){
                throw new SeatUnavailableException("Seat " + seat.getId() + " is not available");
            }
        }

        Booking booking = new Booking();

        // Get currently authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new UserNotAuthenticatedException("User is not Authenticated");
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();
        User user = userRepository.findByEmail(email)
                .orElseThrow( () -> new ResourceNotFoundException("User not found with email: " + email) );

        // Associate booking with the actual logged-in user
        booking.setUser(user);

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
