package com.example.moviebookingapi.service;

import com.example.moviebookingapi.dto.BookingRequestDTO;
import com.example.moviebookingapi.dto.BookingResponseDTO;
import com.example.moviebookingapi.dto.PagedResponseDTO;
import com.example.moviebookingapi.exception.*;
import com.example.moviebookingapi.mapper.BookingMapper;
import com.example.moviebookingapi.model.*;
import com.example.moviebookingapi.repository.*;
import com.example.moviebookingapi.security.SecurityUtils;
import com.example.moviebookingapi.specification.BookingSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final ShowtimeRepository showtimeRepository;
    private final SeatRepository seatRepository;
    private final BookingRepository bookingRepository;
    private final BookingSeatRepository bookingSeatRepository;
    private final BookingMapper bookingMapper;
    private final SecurityUtils securityUtils;

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

        for (Seat seat : selectedSeats) {
            if (!seat.getScreen().getId().equals(showtime.getScreen().getId())) {
                throw new InvalidBookingException("Seat " + seat.getId() + " does not belong to the show's screen");
            }

            if (!seat.getStatus().equals(SeatStatus.AVAILABLE)) {
                throw new SeatUnavailableException("Seat " + seat.getId() + " is not available");
            }
        }

        User user = securityUtils.getCurrentUser();

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setShowtime(showtime);

        LocalDateTime now = LocalDateTime.now();

        booking.setBookingTime(now);
        booking.setExpiresAt(now.plusMinutes(10));
        booking.setStatus(BookingStatus.PENDING);
        booking.setTotalPrice(selectedSeats.size() * 10.0);

        Booking savedBooking = bookingRepository.save(booking);

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

        for (Seat seat : selectedSeats) {
            seat.setStatus(SeatStatus.RESERVED);
            seat.setCurrentBooking(savedBooking);
        }

        seatRepository.saveAll(selectedSeats);

        return bookingMapper.toResponseDTO(savedBooking, bookingSeats);
    }

    @Scheduled(fixedRate = 1 * 60 * 1000)
    @Transactional
    public void expirePendingBookings() {
        LocalDateTime now = LocalDateTime.now();
        List<Booking> expiredBookings = bookingRepository.findByStatusAndExpiresAtBefore(BookingStatus.PENDING, now);

        for (Booking booking : expiredBookings) {
            booking.setStatus(BookingStatus.EXPIRED);

            List<BookingSeat> bookingSeats = bookingSeatRepository.findByBooking(booking);

            for (BookingSeat bookingSeat : bookingSeats) {
                bookingSeat.setStatus(BookingSeatStatus.EXPIRED);

                Seat seat = bookingSeat.getSeat();

                if (booking.equals(seat.getCurrentBooking())) {
                    seat.setStatus(SeatStatus.AVAILABLE);
                    seat.setCurrentBooking(null);
                }
            }
        }
    }

    @Transactional(readOnly = true)
    public PagedResponseDTO<BookingResponseDTO> getMyBookings(int page, int size, List<BookingStatus> bookingStatus) {

        User user = securityUtils.getCurrentUser();

        Pageable pageable = createPageable(page, size);

        Specification<Booking> spec = Specification.allOf(
                BookingSpecification.belongsToUser(user.getId()),
                BookingSpecification.hasStatusIn(bookingStatus)
        );

        Page<Booking> bookingPage = bookingRepository.findAll(spec, pageable);

        return toPagedResponse(bookingPage);
    }

    @Transactional(readOnly = true)
    public BookingResponseDTO getBookingById(Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new ResourceNotFoundException("Booking not found with the id: " + bookingId)
        );

        User user = securityUtils.getCurrentUser();
        boolean isAdmin = securityUtils.isCurrentUserAdmin();
        boolean isOwner = booking.getUser().getId().equals(user.getId());

        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("You do not have permission to access this booking");
        }

        return bookingMapper.toResponseDTO(booking, getBookingSeats(booking));
    }

    @Transactional(readOnly = true)
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
                () -> new ResourceNotFoundException("Booking not found with the id: " + bookingId)
        );

        User user = securityUtils.getCurrentUser();
        boolean isAdmin = securityUtils.isCurrentUserAdmin();
        boolean isOwner = booking.getUser().getId().equals(user.getId());

        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("You do not have permission to cancel this booking");
        }

        if (booking.getStatus() != BookingStatus.PENDING && booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new BusinessRuleException("Booking with id " + bookingId
                    + " cannot be cancelled because its current status is " + booking.getStatus());
        }

        List<BookingSeat> bookingSeats = getBookingSeats(booking);

        for (BookingSeat bookingSeat : bookingSeats) {
            bookingSeat.setStatus(BookingSeatStatus.CANCELLED);

            Seat seat = bookingSeat.getSeat();

            if (booking.equals(seat.getCurrentBooking())) {
                seat.setStatus(SeatStatus.AVAILABLE);
                seat.setCurrentBooking(null);
            }
        }

        booking.setStatus(BookingStatus.CANCELLED);

        return bookingMapper.toResponseDTO(booking, bookingSeats);
    }

    private Pageable createPageable(int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 100);
        return PageRequest.of(safePage, safeSize);
    }

    private List<BookingSeat> getBookingSeats(Booking booking) {
        return bookingSeatRepository.findByBooking(booking);
    }

    private PagedResponseDTO<BookingResponseDTO> toPagedResponse(Page<Booking> bookingPage) {
        List<Booking> bookings = bookingPage.getContent();
        if (bookings.isEmpty()) {
            return new PagedResponseDTO<>(
                    List.of(),
                    bookingPage.getNumber(),
                    bookingPage.getSize(),
                    bookingPage.getTotalElements(),
                    bookingPage.getTotalPages(),
                    bookingPage.isLast()
            );
        }

        List<Long> bookingIds = bookings.stream().map(Booking::getId).toList();
        List<BookingSeat> allSeats = bookingSeatRepository.findByBookingIdIn(bookingIds);

        Map<Long, List<BookingSeat>> seatsByBookingId = allSeats.stream()
                .collect(Collectors.groupingBy(bs -> bs.getBooking().getId()));

        List<BookingResponseDTO> content = bookings.stream()
                .map(b -> bookingMapper.toResponseDTO(
                        b, seatsByBookingId.getOrDefault(b.getId(), List.of())))
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