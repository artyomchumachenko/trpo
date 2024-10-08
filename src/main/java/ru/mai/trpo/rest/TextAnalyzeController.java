package ru.mai.trpo.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import ru.mai.trpo.dto.TextAnalyzeResponseDto;
import ru.mai.trpo.service.TextAnalyzeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class TextAnalyzeController {

    private final TextAnalyzeService textAnalyzeService;

    @PostMapping("/analyze")
    public ResponseEntity<TextAnalyzeResponseDto> analyzeText(@RequestParam("file") MultipartFile file) {
        TextAnalyzeResponseDto result = textAnalyzeService.analyzeText(file);
        return ResponseEntity.ok(result);
    }
}
