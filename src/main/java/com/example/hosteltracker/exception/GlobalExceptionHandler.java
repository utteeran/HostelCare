package com.example.hosteltracker.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ComplaintNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleComplaintNotFound(
            ComplaintNotFoundException exception) {

        Map<String, String> error = Map.of(
                "error", exception.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(error);
    }
}