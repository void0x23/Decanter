package com.wine.api.api_gateway.model.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthResponse {

    private String token;

    public AuthResponse(String token) {
        this.token = token;
    }

}