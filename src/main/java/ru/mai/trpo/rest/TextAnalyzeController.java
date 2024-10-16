package ru.mai.trpo.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import ru.mai.trpo.dto.SentenceResponseDto;
import ru.mai.trpo.service.TextAnalyzeService;

import lombok.RequiredArgsConstructor;

/**
 * Основной контроллер для обработки запросов на анализ текста
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class TextAnalyzeController {

    private final TextAnalyzeService textAnalyzeService;

    /**
     * Запрос на синхронный анализ текста через Python модель
     * @param file Файл с текстом (.txt / .pdf todo обработка pdf)
     * @return Результат анализа
     */
    @PostMapping("/analyze")
    public ResponseEntity<SentenceResponseDto[]> analyzeText(@RequestParam("file") MultipartFile file) {
        SentenceResponseDto[] result = textAnalyzeService.analyzeText(file);
        return ResponseEntity.ok(result);
    }
}
