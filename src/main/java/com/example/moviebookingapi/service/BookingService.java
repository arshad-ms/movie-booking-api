package com.example.moviebookingapi.service;

import com.example.moviebookingapi.dto.BookingRequestDTO;
import com.example.moviebookingapi.dto.BookingResponseDTO;
import com.example.moviebookingapi.dto.PagedResponseDTO;
import com.example.moviebookingapi.exception.*;
import com.example.moviebookingapi.mapper.BookingMapper;
import com.example.moviebookingapi.model.*;
import com.example.moviebookingapi.repository.BookingRepository;
import com.example.moviebookingapi.repository.SeatRepository;
import com.example.moviebookingapi.repository.ShowtimeRepository;
import com.example.moviebookingapi.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.AccessDeniedException;
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

    @Autowired
    private BookingMapper bookingMapper;

    @Transactional
    public BookingResponseDTO createBooking(BookingRequestDTO bookingRequestDTO) {

        List<Long> seatIds = bookingRequestDTO.getSeatIds();

        if (seatIds == null || seatIds.isEmpty()) {
            throw new InvalidBookingException("At least one seat must be selected");
        }

        if (seatIds.size() != seatIds.stream().distinct().count()) {
            throw new InvalidBookingException("Duplicate seat IDs are not allowed");
        }


        Showtime showtime = showtimeRepository.findById(bookingRequestDTO.getShowtimeId())
                .orElseThrow(() -> new ResourceNotFoundException("Showtime not found with id: " + bookingRequestDTO.getShowtimeId()));

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

        // Get currently authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new UserNotAuthenticatedException("User is not Authenticated");
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();
        User user = userRepository.findByEmail(email)
                .orElseThrow( () -> new ResourceNotFoundException("User not found with email: " + email) );

        Booking booking = new Booking();

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

        // Build the flattened DTO while we're still inside
        // the transactional Hibernate session.
        return bookingMapper.toResponseDTO(savedBooking, selectedSeats);

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

    @Transactional
    public PagedResponseDTO<BookingResponseDTO> getMyBookings(int page, int size) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException("User not found with email: " + email)
        );

//        Pageable pageable = PageRequest.of(page, size);
        Pageable pageable = createPageable(page, size);
        Page<Booking> bookingPage = bookingRepository.findByUserId(user.getId(), pageable);

        List<BookingResponseDTO> content = bookingPage.getContent()
                .stream()
                .map(booking -> bookingMapper.toResponseDTO(
                        booking, seatRepository.findByCurrentBooking(booking)))
                .toList();

        return new PagedResponseDTO<>(
                content,
                bookingPage.getNumber(),
                bookingPage.getSize(),
                bookingPage.getTotalElements(),
                bookingPage.getTotalPages(),
                bookingPage.isLast()
        );
    }

    @Transactional
    public BookingResponseDTO getBookingById(Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                ()-> new ResourceNotFoundException("Booking not found with the id: "+bookingId)
        );

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow(
                ()-> new ResourceNotFoundException("User not found with email: " + email)
        );

        boolean isAdmin = authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN"));

        boolean isOwner = booking.getUser().getId().equals(user.getId());

        if(!isAdmin && !isOwner){
            throw new AccessDeniedException("You do not have permission to access this booking");
        }

        List<Seat> seats = seatRepository.findByCurrentBooking(booking);

        return bookingMapper.toResponseDTO(booking, seats);
    }

    @Transactional
    public PagedResponseDTO<BookingResponseDTO> getAllBookings(int page, int size) {
        
//        Pageable pageable = PageRequest.of(page, size);
        Pageable pageable = createPageable(page, size);

        Page<Booking> bookingPage = bookingRepository.findAll(pageable);

        List<BookingResponseDTO> content = bookingPage.getContent()
                .stream()
                .map(booking -> bookingMapper.toResponseDTO(
                        booking, seatRepository.findByCurrentBooking(booking)))
                .toList();

        return new PagedResponseDTO<>(
                content,
                bookingPage.getNumber(),
                bookingPage.getSize(),
                bookingPage.getTotalElements(),
                bookingPage.getTotalPages(),
                bookingPage.isLast()
        );
    }

    @Transactional
    public BookingResponseDTO cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new ResourceNotFoundException("Booking not found with the id: "+bookingId)
        );

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication == null || !authentication.isAuthenticated()){
            throw new UserNotAuthenticatedException("User not authenticated");
        }

        String email = authentication.getName();

        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException("User not found with email: " + email)
        );

        boolean isAdmin = authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN"));

        boolean isOwner = booking.getUser().getId().equals(user.getId());

        if(!isAdmin && !isOwner){
            throw new AccessDeniedException("You do not have permission to cancel this booking");
        }

        if(booking.getStatus() != BookingStatus.PENDING && booking.getStatus() != BookingStatus.CONFIRMED){
            throw new BusinessRuleException("Booking with id " + bookingId
                    + " cannot be cancelled because its current status is " + booking.getStatus());
        }

        List<Seat> seats = seatRepository.findByCurrentBooking(booking);
        for(Seat seat: seats){
            seat.setStatus(SeatStatus.AVAILABLE);
            seat.setCurrentBooking(null);
        }

        booking.setStatus(BookingStatus.CANCELLED);

        seatRepository.saveAll(seats);
        Booking cancelledBooking = bookingRepository.save(booking);

        return bookingMapper.toResponseDTO(cancelledBooking, seats);

    }

    private Pageable createPageable(int page, int size) {

        int safePage = Math.max(page, 0);

        // TODO: enforce via validation
        int safeSize = Math.min(Math.max(size, 1), 100);

        return PageRequest.of(safePage, safeSize);
    }


}
