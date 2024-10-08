package ru.mai.trpo.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TextAnalyzeRequestDto {
    private String text;
}
