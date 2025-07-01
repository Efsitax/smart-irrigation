package com.kadir.smartirrigation.motor.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OnOffDto {
    @NotBlank(message = "Status is required")
    private String status;
}
