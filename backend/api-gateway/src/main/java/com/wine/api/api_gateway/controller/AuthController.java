package com.wine.api.api_gateway.controller;

import com.wine.api.api_gateway.model.LoginRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class AuthController {

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest loginRequest) {

        // Estrarre il google id e autenticarlo con il server google

        // Controllare se l'utente esiste  nel database, in caso non esista crearlo
    }
}
