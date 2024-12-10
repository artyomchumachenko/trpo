package ru.mai.trpo.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import ru.mai.trpo.configuration.properties.PyModelProperties;
import ru.mai.trpo.dto.integration.SentenceResponseDto;
import ru.mai.trpo.dto.integration.TextRequestDto;

import lombok.SneakyThrows;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

class TextAnalyzePyModelClientTest extends AbstractWireMockTest {

    @Test
    @DisplayName("Должен корректно отправить POST запрос и получить массив SentenceResponseDto")
    @SneakyThrows
    void shouldReturnSentenceResponseDtoArray() {
        // Подготавливаем тестовые данные для ответа
        SentenceResponseDto[] expectedResponse = {
                new SentenceResponseDto(
                        "Sentence 1",
                        java.util.List.of("word1", "word2"),
                        java.util.List.of("lemma1", "lemma2"),
                        java.util.List.of("NOUN", "VERB"),
                        java.util.List.of("ROOT", "OBJ"),
                        java.util.List.of(0, 1)
                ),
                new SentenceResponseDto(
                        "Sentence 2",
                        java.util.List.of("word3"),
                        java.util.List.of("lemma3"),
                        java.util.List.of("ADJ"),
                        java.util.List.of("MOD"),
                        java.util.List.of(2)
                )
        };

        // Конвертируем ожидаемый ответ в JSON
        ObjectMapper objectMapper = new ObjectMapper();
        String responseJson = objectMapper.writeValueAsString(expectedResponse);

        // Настраиваем WireMock для обработки POST запроса к /process
        wireMockServer.stubFor(WireMock.post(urlEqualTo("/process"))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(responseJson)));

        // Создаем тестовые properties на основе WireMock сервера
        PyModelProperties testProperties = new PyModelProperties(getBaseUrl());

        // Создаем RestTemplateBuilder
        RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();

        // Создаем клиент
        TextAnalyzePyModelClient client = new TextAnalyzePyModelClient(restTemplateBuilder, testProperties);

        // Создаем запрос с помощью билдера
        TextRequestDto requestDto = TextRequestDto.builder()
                .text("Test text")
                .build();

        // Отправляем запрос
        SentenceResponseDto[] actualResponse = client.analyzeText(requestDto);

        // Проверяем результат
        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse).hasSize(2);
        assertThat(actualResponse[0].getSentence()).isEqualTo("Sentence 1");
        assertThat(actualResponse[1].getSentence()).isEqualTo("Sentence 2");
    }
}
