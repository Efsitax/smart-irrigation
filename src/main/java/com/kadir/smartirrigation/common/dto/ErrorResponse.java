package com.kadir.smartirrigation.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class ErrorResponse {
    private String message;
    private String path;
    private LocalDateTime time;
    private int status;
}
