package com.kadir.smartirrigation.common.exception;

import com.kadir.smartirrigation.common.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ScheduleNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleScheduleNotFound(ScheduleNotFoundException ex, HttpServletRequest request) {
        log.warn("Schedule not found: {}", ex.getMessage());
        return createErrorResponse(ex.getMessage(), request, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(SensorDataNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleSensorDataNotFound(SensorDataNotFoundException ex, HttpServletRequest request) {
        log.warn("Sensor data not found: {}", ex.getMessage());
        return createErrorResponse(ex.getMessage(), request, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        String paramName = ex.getName();
        String requiredType = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown";
        String message = "Invalid value for parameter '" + paramName + "'. Expected type: " + requiredType;

        log.warn("Type mismatch: {} at URI: {}", message, request.getRequestURI());
        return createErrorResponse(message, request, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MqttConnectionException.class)
    public ResponseEntity<ErrorResponse> handleMqttConnectionError(MqttConnectionException ex, HttpServletRequest request) {
        return createErrorResponse("MQTT connection error: " + ex.getMessage(), request, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, HttpServletRequest request) {
        log.error("Unexpected error at {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        return createErrorResponse("An unexpected error occurred", request, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String errorMessage = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .findFirst()
                .orElse("Validation failed");

        log.warn("Validation error at {}: {}", request.getRequestURI(), errorMessage);
        return createErrorResponse(errorMessage, request, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadableMessage(HttpMessageNotReadableException ex, HttpServletRequest request) {
        log.warn("Malformed JSON request: {}", ex.getMessage());
        return createErrorResponse(ex.getMessage(), request, HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<ErrorResponse> createErrorResponse(String msg, HttpServletRequest request, HttpStatus status) {
        ErrorResponse response = new ErrorResponse(
                msg,
                request.getRequestURI(),
                LocalDateTime.now(),
                status.value()
        );
        return new ResponseEntity<>(response, status);
    }
}
