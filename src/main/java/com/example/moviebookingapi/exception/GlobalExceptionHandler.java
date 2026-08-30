package com.example.moviebookingapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<String> handleObjectOptimisticLockingFailureException(
            ObjectOptimisticLockingFailureException ex) {
//        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body("Seat was just taken by someone else, please refresh");
    }
}
