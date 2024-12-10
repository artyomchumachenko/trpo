package ru.mai.trpo.token;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import ru.mai.trpo.configuration.jwt.JwtTokenProvider;
import ru.mai.trpo.configuration.jwt.service.CustomUserDetailsService;
import ru.mai.trpo.configuration.properties.OutsideRequestsProperties;
import ru.mai.trpo.rest.TestSecurityConfig;
import ru.mai.trpo.rest.TextAnalyzeController;
import ru.mai.trpo.service.TextAnalyzeService;
import ru.mai.trpo.service.UserService;

import lombok.SneakyThrows;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TextAnalyzeController.class)
@Import(TestSecurityConfig.class)
public class AuthenticationTokenRestTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TextAnalyzeService textAnalyzeService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private OutsideRequestsProperties outsideRequestsProperties;

    @MockBean
    private UserService userService;

    /**
     * Тест проверяет, что при отсутствии заголовка Authorization контроллер вернёт статус 4xx (например 401),
     * так как метод требует извлечения имени пользователя из токена.
     */
    @Test
    @DisplayName("Должен вернуть статус 401 при отсутствии заголовка Authorization")
    @SneakyThrows
    void shouldReturnUnauthorizedWithoutAuthorizationHeader() {
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "Это тестовый текст".getBytes()
        );

        mockMvc.perform(MockMvcRequestBuilders
                        .multipart("/api/analyze")
                        .file(mockFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Тест проверяет, что при неверном токене метод вернёт статус 401.
     */
    @Test
    @DisplayName("Должен вернуть статус 401 при некорректном токене")
    @SneakyThrows
    void shouldReturnUnauthorizedWithInvalidToken() {
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "Это тестовый текст".getBytes()
        );

        when(jwtTokenProvider.validateToken("Bearer invalid_token")).thenReturn(Boolean.FALSE);

        mockMvc.perform(MockMvcRequestBuilders
                        .multipart("/api/analyze")
                        .file(mockFile)
                        .header("Authorization", "Bearer invalid_token")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isUnauthorized());
    }
}
