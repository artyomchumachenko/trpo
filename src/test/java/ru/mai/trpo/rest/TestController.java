package ru.mai.trpo.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TestController {

    @GetMapping("/throwException")
    public void throwException() {
        throw new RuntimeException("Test Exception");
    }

    @GetMapping("/throwIllegalArgument")
    public void throwIllegalArgumentException() {
        throw new IllegalArgumentException("Illegal Argument Exception");
    }
}
