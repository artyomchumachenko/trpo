package ru.mai.trpo.configuration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import lombok.extern.slf4j.Slf4j;

/**
 * Класс для логирования запросов и ответов, отправляемых через REST во внешние системы
 * (в нашем случае только модель на Python)
 */
@Slf4j
public class RestTemplateLoggingInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws
            IOException {
        logRequestDetails(request, body);

        ClientHttpResponse response = execution.execute(request, body);

        logResponseDetails(response);

        return response;
    }

    /**
     * Логирование запросов от внешних систем
     * @param request Объект запроса
     * @param body Тело запроса
     */
    private void logRequestDetails(HttpRequest request, byte[] body) {
        log.info("Request URI: {}", request.getURI());
        log.info("HTTP Method: {}", request.getMethod());
        log.info("Request Headers: {}", request.getHeaders());
        log.info("Request Body: {}", new String(body, StandardCharsets.UTF_8));
    }

    /**
     * Логирование ответов от внешних систем
     * @param response Объект ответа
     */
    private void logResponseDetails(ClientHttpResponse response) throws IOException {
        String responseBody = new BufferedReader(new InputStreamReader(response.getBody(), StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        log.info("Response Status Code: {}", response.getStatusCode());
        log.info("Response Headers: {}", response.getHeaders());
        log.info("Response Body: {}", responseBody);
    }
}
