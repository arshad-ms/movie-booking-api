package com.example.moviebookingapi.service;

import com.example.moviebookingapi.dto.BookingRequestDTO;
import com.example.moviebookingapi.dto.BookingResponseDTO;
import com.example.moviebookingapi.dto.PagedResponseDTO;
import com.example.moviebookingapi.exception.*;
import com.example.moviebookingapi.mapper.BookingMapper;
import com.example.moviebookingapi.model.*;
import com.example.moviebookingapi.repository.*;
import com.example.moviebookingapi.specification.BookingSpecification;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
    private BookingSeatRepository bookingSeatRepository;

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

        booking.setUser(user);
        booking.setShowtime(showtime);

        LocalDateTime now = LocalDateTime.now();

        booking.setBookingTime(now);
        booking.setExpiresAt(now.plusMinutes(10));
        booking.setStatus(BookingStatus.PENDING);
        booking.setTotalPrice(selectedSeats.size() * 10.0);

        // Save the booking first so that it gets its generated ID.
        Booking savedBooking = bookingRepository.save(booking);

        // historical BookingSeat records
        List<BookingSeat> bookingSeats = selectedSeats.stream()
                .map(seat -> {

                    BookingSeat bookingSeat = new BookingSeat();

                    bookingSeat.setSeat(seat);
                    bookingSeat.setPriceAtBooking(10.0);
                    bookingSeat.setStatus(BookingSeatStatus.RESERVED);

                    savedBooking.addBookingSeat(bookingSeat);

                    return bookingSeat;
                })
                .toList();

        bookingSeatRepository.saveAll(bookingSeats);

        // Update the live seat state
        for(Seat seat: selectedSeats){
            seat.setStatus(SeatStatus.RESERVED);
            seat.setCurrentBooking(savedBooking);
        }

        seatRepository.saveAll(selectedSeats);

        // Build the flattened DTO while we're still inside
        // the transactional Hibernate session.
        return bookingMapper.toResponseDTO(savedBooking, bookingSeats);

    }

    @Scheduled(fixedRate = 1 * 60 * 1000 )
    @Transactional
    public void expirePendingBookings(){
        LocalDateTime now = LocalDateTime.now();
        List<Booking> expiredBookings = bookingRepository.findByStatusAndExpiresAtBefore(BookingStatus.PENDING, now);

        for (Booking booking: expiredBookings) {
            booking.setStatus(BookingStatus.EXPIRED);

            // historical booking-seat records.
            List<BookingSeat> bookingSeats = bookingSeatRepository.findByBooking(booking);

            for (BookingSeat bookingSeat : bookingSeats) {

                bookingSeat.setStatus(BookingSeatStatus.EXPIRED);

                // Release the live seat
                Seat seat = bookingSeat.getSeat();

                if (booking.equals(seat.getCurrentBooking())) {
                    seat.setStatus(SeatStatus.AVAILABLE);
                    seat.setCurrentBooking(null);
                }
            }

        }
    }

    @Transactional
    public PagedResponseDTO<BookingResponseDTO> getMyBookings(int page, int size, List<BookingStatus> bookingStatus) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UserNotAuthenticatedException("User is not authenticated");
        }

        String email = authentication.getName();

        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException("User not found with email: " + email)
        );

        Pageable pageable = createPageable(page, size);

        Specification<Booking> spec = Specification.allOf(
                BookingSpecification.belongsToUser(user.getId()),
                BookingSpecification.hasStatusIn(bookingStatus)
        );

        Page<Booking> bookingPage = bookingRepository.findAll(spec, pageable);

        return toPagedResponse(bookingPage);
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

        return bookingMapper.toResponseDTO(booking, getBookingSeats(booking));

    }

    @Transactional
    public PagedResponseDTO<BookingResponseDTO> getAllBookings(
            int page, int size, List<BookingStatus> status, LocalDate fromDate, LocalDate toDate, Long movieId) {

        if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
            throw new InvalidBookingException(
                    "fromDate must be before or equal to toDate"
            );
        }

        LocalDateTime fromTime = fromDate == null ? null : fromDate.atStartOfDay();

        LocalDateTime toTime = toDate == null ? null : toDate.atTime(LocalTime.MAX);

        Pageable pageable = createPageable(page, size);

        Specification<Booking> spec = Specification.allOf(
                BookingSpecification.hasStatusIn(status),
                BookingSpecification.bookingTimeBetween(fromTime, toTime),
                BookingSpecification.hasMovieId(movieId)
        );

        Page<Booking> bookingPage = bookingRepository.findAll(spec, pageable);

        return toPagedResponse(bookingPage);
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

        // Only PENDING and CONFIRMED bookings can be cancelled
        if(booking.getStatus() != BookingStatus.PENDING && booking.getStatus() != BookingStatus.CONFIRMED){
            throw new BusinessRuleException("Booking with id " + bookingId
                    + " cannot be cancelled because its current status is " + booking.getStatus());
        }

        // Get historical BookingSeat records
        List<BookingSeat> bookingSeats = getBookingSeats(booking);

        for (BookingSeat bookingSeat : bookingSeats) {
            bookingSeat.setStatus(BookingSeatStatus.CANCELLED);

            Seat seat = bookingSeat.getSeat();

            // Release only seats still reserved by this booking.
            if (booking.equals(seat.getCurrentBooking())) {
                seat.setStatus(SeatStatus.AVAILABLE);
                seat.setCurrentBooking(null);
            }
        }

        booking.setStatus(BookingStatus.CANCELLED);

        return bookingMapper.toResponseDTO(booking, bookingSeats);

    }

    // helper method
    private Pageable createPageable(int page, int size) {

        int safePage = Math.max(page, 0);

        // TODO: enforce via validation
        int safeSize = Math.min(Math.max(size, 1), 100);

        return PageRequest.of(safePage, safeSize);
    }

    // helper method
    private List<BookingSeat> getBookingSeats(Booking booking) {
        return bookingSeatRepository.findByBooking(booking);
    }

    // helper method
    private PagedResponseDTO<BookingResponseDTO> toPagedResponse(
            Page<Booking> bookingPage
    ) {
        List<BookingResponseDTO> content = bookingPage.getContent()
                .stream()
                .map(booking -> bookingMapper.toResponseDTO(
                        booking, getBookingSeats(booking)))
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

}
