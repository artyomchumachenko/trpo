package ru.mai.trpo.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import ru.mai.trpo.client.TextAnalyzePyModelClient;
import ru.mai.trpo.dto.TextAnalyzeResponseDto;
import ru.mai.trpo.dto.integration.TextRequestDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TextAnalyzeService {

    private final TextAnalyzePyModelClient client;

    public TextAnalyzeResponseDto analyzeText(MultipartFile file) {
        String text = extractTextFromFile(file);

        TextRequestDto requestDto = TextRequestDto.builder()
                .text(text)
                .build();

        return client.analyzeText(requestDto);
    }

    private String extractTextFromFile(MultipartFile file) {
        try {
            // Преобразуем содержимое файла в строку
            return new String(file.getBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при чтении файла", e);
        }
    }
}
