package com.eclept.andjelazoric_eclept_be_labflow.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    // Custom Exception for full hospital order
    @ExceptionHandler(QueueFullException.class)
    public ResponseEntity<Map<String, Object>> handleQueueFull(QueueFullException ex) {
        log.warn("Hospital queue full: {}", ex.getMessage());
        return buildResponse(HttpStatus.TOO_MANY_REQUESTS, "Queue Full", ex.getMessage());
    }

    // Custom Exception for non-existent test
    @ExceptionHandler(TestNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleTestNotFound(TestNotFoundException ex) {
        log.warn("TestNotFoundException: {}", ex.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, "Test Not Found", ex.getMessage());
    }

    // Fallback for other exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAll(Exception ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", ex.getMessage());
    }

    @ExceptionHandler(LabFlowException.class)
    public ResponseEntity<Map<String, Object>> handleLabFlowException(LabFlowException ex) {
        if (ex.getMessage().contains("Test type not found")) {
            return buildResponse(HttpStatus.NOT_FOUND, "Test Type Not Found", ex.getMessage());
        }
        return buildResponse(HttpStatus.BAD_REQUEST, "LabFlow Error", ex.getMessage());
    }

    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String error, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", error);
        body.put("message", message);
        return new ResponseEntity<>(body, status);
    }
}
