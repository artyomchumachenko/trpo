package ru.mai.trpo.dto.integration;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TextRequestDto {
    private String text;
}
