package ru.mai.trpo.client;

import com.github.tomakehurst.wiremock.WireMockServer;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

public abstract class AbstractWireMockTest {

    protected static WireMockServer wireMockServer;

    @BeforeAll
    static void startWireMock() {
        // Запускаем WireMock на свободном порту
        wireMockServer = new WireMockServer(0);
        wireMockServer.start();
    }

    @AfterAll
    static void stopWireMock() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }

    protected String getBaseUrl() {
        // Получаем базовый URL WireMock, например: http://localhost:12345
        return "http://localhost:" + wireMockServer.port();
    }
}
