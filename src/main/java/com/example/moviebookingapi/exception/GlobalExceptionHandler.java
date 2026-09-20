package com.example.moviebookingapi.exception;

import com.example.moviebookingapi.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // 404 not found
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex, HttpServletRequest request) {

        logger.warn("{} at {}: {}", ex.getClass().getSimpleName(), request.getRequestURI(), ex.getMessage());

        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);

    }

    // 409 conflict
    @ExceptionHandler(SeatUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleSeatUnavailableException(
            SeatUnavailableException ex, HttpServletRequest request) {

        logger.warn("{} at {}: {}", ex.getClass().getSimpleName(), request.getRequestURI(), ex.getMessage());

        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage(), request);

    }

    // 400 bad request
    @ExceptionHandler(InvalidBookingException.class)
    public ResponseEntity<ErrorResponse> handleInvalidBookingException(
            InvalidBookingException ex, HttpServletRequest request) {

        logger.warn("{} at {}: {}", ex.getClass().getSimpleName(), request.getRequestURI(), ex.getMessage());

        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);

    }

    @ExceptionHandler(TheaterScreenMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTheaterScreenMismatchException(
            TheaterScreenMismatchException ex, HttpServletRequest request) {

        logger.warn("{} at {}: {}", ex.getClass().getSimpleName(), request.getRequestURI(), ex.getMessage());

        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);

    }

    // 401 not authenticated
    @ExceptionHandler(UserNotAuthenticatedException.class)
    public ResponseEntity<ErrorResponse> handleUserNotAuthenticatedException(
            UserNotAuthenticatedException ex, HttpServletRequest request) {

        logger.warn("{} at {}: {}", ex.getClass().getSimpleName(), request.getRequestURI(), ex.getMessage());

        return buildErrorResponse(HttpStatus.UNAUTHORIZED, ex.getMessage(), request);

    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ErrorResponse> handleObjectOptimisticLockingFailureException(
            ObjectOptimisticLockingFailureException ex, HttpServletRequest request) {

        logger.warn("{} at {}: {}", ex.getClass().getSimpleName(), request.getRequestURI(), ex.getMessage());

        return buildErrorResponse(HttpStatus.CONFLICT,
                "Seat was just taken by someone else, please refresh", request);

    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex, HttpServletRequest request) {

        logger.error("Unhandled exception at {}: ", request.getRequestURI(), ex);

        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,"An unexpected error occurred", request);

    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, String message, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI()
        );

        return new ResponseEntity<>(errorResponse, status);
    }
}
