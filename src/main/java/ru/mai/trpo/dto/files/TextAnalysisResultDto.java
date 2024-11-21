package ru.mai.trpo.dto.files;

import java.util.List;

import ru.mai.trpo.dto.integration.SentenceResponseDto;

import lombok.Data;

@Data
public class TextAnalysisResultDto {
    private Long fileId; // Идентификатор файла
    private String fileName; // Название файла
    private List<SentenceResponseDto> analysisResults; // Результаты анализа текста
}
