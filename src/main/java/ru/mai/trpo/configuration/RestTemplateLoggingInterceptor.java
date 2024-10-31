package ru.mai.trpo.configuration;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

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
        String responseBody = StreamUtils.copyToString(response.getBody(), StandardCharsets.UTF_8); // Указываем UTF-8
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.getFactory().configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, false);
            Object json = mapper.readValue(responseBody, Object.class);
            ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
            String prettyResponseBody = writer.writeValueAsString(json);
            log.info("Response Status Code: {}", response.getStatusCode());
            log.info("Response Headers: {}", response.getHeaders());
            log.info("Response Body: {}", prettyResponseBody);
        } catch (Exception e) {
            log.info("Response Status Code: {}", response.getStatusCode());
            log.info("Response Headers: {}", response.getHeaders());
            log.info("Response Body: {}", responseBody);
        }
    }
}
