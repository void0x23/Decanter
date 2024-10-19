package com.wine.api.api_gateway.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

    @NotNull(message = "Google ID cannot be null")
    @NotBlank(message = "Google ID cannot be empty")
    private String googleID;
}