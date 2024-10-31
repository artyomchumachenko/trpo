package ru.mai.trpo.client;

import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import ru.mai.trpo.configuration.RestTemplateLoggingInterceptor;
import ru.mai.trpo.configuration.properties.PyModelProperties;
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

    public TextAnalyzePyModelClient(RestTemplateBuilder restTemplateBuilder, PyModelProperties properties) {
        this.restTemplate = restTemplateBuilder
                .rootUri(properties.url()) // Установка базового URL
                .interceptors(new RestTemplateLoggingInterceptor()) // Логгер-перехватчик REST запросов
                .requestFactory(() -> new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()))
                // Настройка для обработки UTF-8
                .additionalMessageConverters(
                        new MappingJackson2HttpMessageConverter(
                                new ObjectMapper().configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, false)
                        ),
                        new StringHttpMessageConverter(StandardCharsets.UTF_8)
                )
                .build();
    }

    /**
     * Метод для отправки POST запроса в PyModel для анализа текста
     * @param requestDto Объект данных для тела запроса
     * @return Массив проанализированных предложений с помощью PyModel
     */
    public SentenceResponseDto[] analyzeText(TextRequestDto requestDto) {
        ResponseEntity<SentenceResponseDto[]>
                response = restTemplate.postForEntity("/process", requestDto, SentenceResponseDto[].class);
        return response.getBody();
    }
}
