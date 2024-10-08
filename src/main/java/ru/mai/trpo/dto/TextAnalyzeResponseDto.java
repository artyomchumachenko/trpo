package ru.mai.trpo.dto;

import java.util.List;

import lombok.Data;

@Data
public class TextAnalyzeResponseDto {
    private List<SentenceResponseDto> sentences;
}
