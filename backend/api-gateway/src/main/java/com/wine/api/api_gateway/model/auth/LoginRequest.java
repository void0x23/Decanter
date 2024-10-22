package com.wine.api.api_gateway.model.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class LoginRequest {

    @NotNull(message = "Google ID cannot be null")
    @NotBlank(message = "Google ID cannot be empty")
    private String googleIdToken;
}
