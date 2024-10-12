package ru.mai.trpo.client;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import ru.mai.trpo.configuration.RestTemplateLoggingInterceptor;
import ru.mai.trpo.dto.TextAnalyzeResponseDto;
import ru.mai.trpo.dto.integration.TextRequestDto;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class TextAnalyzePyModelClient {

    private final RestTemplate restTemplate;

    public TextAnalyzePyModelClient(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder
                .interceptors(new RestTemplateLoggingInterceptor())
                .build();
    }

    public TextAnalyzeResponseDto analyzeText(TextRequestDto requestDto) {
        String url = "http://localhost:5000/process";
        ResponseEntity<TextAnalyzeResponseDto>
                response = restTemplate.postForEntity(url, requestDto, TextAnalyzeResponseDto.class);
        return response.getBody();
    }
}
