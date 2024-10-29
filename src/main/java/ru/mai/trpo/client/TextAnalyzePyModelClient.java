package ru.mai.trpo.client;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import ru.mai.trpo.configuration.RestTemplateLoggingInterceptor;
import ru.mai.trpo.dto.integration.SentenceResponseDto;
import ru.mai.trpo.dto.integration.TextRequestDto;

import lombok.extern.slf4j.Slf4j;

/**
 * Клиент для взаимодействия с моделью на Python
 */
@Component
@Slf4j
public class TextAnalyzePyModelClient {

    /**
     * Объект для отправки REST запросов в PyModel
     */
    private final RestTemplate restTemplate;

    public TextAnalyzePyModelClient(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder
                .interceptors(new RestTemplateLoggingInterceptor()) // Логгер-перехватчик REST запросов
                .build();
    }

    /**
     * Метод для отправки POST запроса в PyModel для анализа текста
     * @param requestDto Объект данных для тела запроса
     * @return Массив проанализированных предложений с помощью PyModel
     */
    public SentenceResponseDto[] analyzeText(TextRequestDto requestDto) {
        String url = "http://localhost:5000/process";
        ResponseEntity<SentenceResponseDto[]>
                response = restTemplate.postForEntity(url, requestDto, SentenceResponseDto[].class);
        return response.getBody();
    }
}
