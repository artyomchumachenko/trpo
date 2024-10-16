package ru.mai.trpo.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import ru.mai.trpo.client.TextAnalyzePyModelClient;
import ru.mai.trpo.dto.SentenceResponseDto;
import ru.mai.trpo.dto.integration.TextRequestDto;

import lombok.RequiredArgsConstructor;

/**
 * Сервис "бизнес-логики", связанной с анализом текста
 */
@Service
@RequiredArgsConstructor
public class TextAnalyzeService {

    private final TextAnalyzePyModelClient client;

    /**
     * Метод синхронного анализа текста
     * @param file Файл с текстом
     * @return Результат анализа
     */
    public SentenceResponseDto[] analyzeText(MultipartFile file) {
        String text = extractTextFromFile(file);

        // Конструирование объекта запроса на анализ текста в PyModel
        TextRequestDto requestDto = TextRequestDto.builder()
                .text(text)
                .build();

        // Отправка запроса в PyModel клиент на анализ текста
        return client.analyzeText(requestDto);
    }

    /**
     * Извлечь текст из файла
     * @param file Файл с текстом
     * @return Строка с текстом
     */
    private String extractTextFromFile(MultipartFile file) {
        try {
            // Преобразуем содержимое файла в строку
            return new String(file.getBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при чтении файла", e);
        }
    }
}
