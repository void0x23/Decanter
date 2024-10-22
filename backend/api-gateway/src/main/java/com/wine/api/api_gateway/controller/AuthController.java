package com.wine.api.api_gateway.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wine.api.api_gateway.invoker.ApiException;
import com.wine.api.api_gateway.model.auth.AuthResponse;
import com.wine.api.api_gateway.model.auth.LoginRequest;
import com.wine.api.api_gateway.model.User;
import com.wine.api.api_gateway.service.DatabaseService;
import com.wine.api.api_gateway.service.GoogleService;
import com.wine.api.api_gateway.service.JwtAuthService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
public class AuthController {

    @Value("${google.auth.url}")
    private String googleTokenAuthUrl;

    @Autowired
    private GoogleService googleService;

    @Autowired
    private DatabaseService databaseService;

    @Autowired
    private JwtAuthService jwtAuthService;

    @PostMapping("/auth/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest loginRequest) throws ApiException, JsonProcessingException {

        //test
        log.info("Login request {}", loginRequest.toString());

        String idToken = loginRequest.getGoogleIdToken();
        String response = googleService.authenticateUser(idToken);

        if (response == null) {
            throw new ApiException("Authentication failed: no response from Google service");
        }

        //test
        log.info("Google auth response {}", response);

        User user = databaseService.retrieveOrCreateUserById(createUserFromGoogleResponse(response));

        if (user == null) {
            throw new ApiException("Failed retrieving user, user is null");
        }

        log.info("Logged in user {}", user);

        String jwt = jwtAuthService.createJwtToken(user.getGoogleId(), user.getName(), user.getEmail());

        log.info("Jwt created {}", jwt);



        return ResponseEntity.ok(new AuthResponse(jwt));
    }


    private User createUserFromGoogleResponse(String response) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> googleResponse = objectMapper.readValue(response, new TypeReference<Map<String, Object>>(){});

        User user = new User();
        user.setGoogleId((String) googleResponse.get("sub"));
        user.setEmail((String) googleResponse.get("email"));
        user.setName((String) googleResponse.get(("name")));

        return user;
    }
}
