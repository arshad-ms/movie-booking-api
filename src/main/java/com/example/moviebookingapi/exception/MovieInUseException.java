package com.example.moviebookingapi.exception;

public class MovieInUseException extends RuntimeException {
    public MovieInUseException(String message) {
        super(message);
    }
}
