package ru.mai.trpo.dto.user;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;
}
