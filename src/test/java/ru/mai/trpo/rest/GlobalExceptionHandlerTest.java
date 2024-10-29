package ru.mai.trpo.rest;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TestController.class)
@Import(GlobalExceptionHandler.class)
public class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void whenExceptionThrown_thenInternalServerErrorReturned() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/throwException"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.statusCode", is(HttpStatus.INTERNAL_SERVER_ERROR.value())))
                .andExpect(jsonPath("$.message", is("Test Exception")))
                .andExpect(jsonPath("$.details", is("uri=/api/throwException")))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @Disabled
    public void whenIllegalArgumentExceptionThrown_thenBadRequestReturned() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/throwIllegalArgument"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode", is(HttpStatus.BAD_REQUEST.value())))
                .andExpect(jsonPath("$.message", is("Illegal Argument Exception")))
                .andExpect(jsonPath("$.details", is("uri=/api/throwIllegalArgument")))
                .andExpect(jsonPath("$.timestamp").exists());
    }
}
