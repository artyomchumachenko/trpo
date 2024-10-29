package ru.mai.trpo.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResponseDto {
    private int statusCode;
    private String message;
    private String details;
    private LocalDateTime timestamp;
}
