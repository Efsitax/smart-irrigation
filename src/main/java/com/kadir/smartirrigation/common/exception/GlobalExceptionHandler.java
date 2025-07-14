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
import java.util.stream.Collectors;

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

    @ExceptionHandler(TemperatureConfigNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTemperatureConfigNotFound(TemperatureConfigNotFoundException ex, HttpServletRequest request) {
        log.warn("Temperature config not found: {}", ex.getMessage());
        return createErrorResponse(ex.getMessage(), request, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TemperatureServiceException.class)
    public ResponseEntity<ErrorResponse> handleTemperatureServiceException(TemperatureServiceException ex, HttpServletRequest request) {
        log.warn("Temperature service error: {}", ex.getMessage());
        return createErrorResponse(ex.getMessage(), request, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(NotificationException.class)
    public ResponseEntity<ErrorResponse> handleNotificationException(NotificationException ex, HttpServletRequest request) {
        log.warn("Notification error: {}", ex.getMessage());
        return createErrorResponse(ex.getMessage(), request, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MqttConnectionException.class)
    public ResponseEntity<ErrorResponse> handleMqttConnectionError(MqttConnectionException ex, HttpServletRequest request) {
        log.error("MQTT connection error: {}", ex.getMessage());
        return createErrorResponse("MQTT connection error: " + ex.getMessage(), request, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        String paramName = ex.getName();
        String requiredType = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown";
        String message = "Invalid value for parameter '" + paramName + "'. Expected type: " + requiredType;

        log.warn("Type mismatch: {} at URI: {}", message, request.getRequestURI());
        return createErrorResponse(message, request, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadableMessage(HttpMessageNotReadableException ex, HttpServletRequest request) {
        log.warn("Malformed JSON request: {}", ex.getMessage());
        return createErrorResponse("Malformed JSON request", request, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String errorMessage = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining("; "));

        log.warn("Validation error at {}: {}", request.getRequestURI(), errorMessage);
        return createErrorResponse(errorMessage, request, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MotorNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleMotorNotFound(MotorNotFoundException ex, HttpServletRequest request) {
        log.warn("Motor not found: {}", ex.getMessage());
        return createErrorResponse(ex.getMessage(), request, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(SensorDataFormatException.class)
    public ResponseEntity<ErrorResponse> handleSensorDataFormat(SensorDataFormatException ex, HttpServletRequest request) {
        log.warn("Sensor data format issue: {}", ex.getMessage());
        return createErrorResponse(ex.getMessage(), request, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, HttpServletRequest request) {
        log.error("Unexpected error at {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        return createErrorResponse("An unexpected error occurred", request, HttpStatus.INTERNAL_SERVER_ERROR);
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