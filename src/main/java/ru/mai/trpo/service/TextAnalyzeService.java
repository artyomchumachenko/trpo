package ru.mai.trpo.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import ru.mai.trpo.dto.TextAnalyzeResponseDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TextAnalyzeService {

    public TextAnalyzeResponseDto analyzeText(MultipartFile file) {
        return null;
    }
}
