package com.wine.api.api_gateway.service;

import com.wine.api.api_gateway.exception.GoogleAuthenticationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

@Service
public class GoogleService {

    @Value("${google.auth.url}")
    private String googleTokenAuthUrl;

    private final RestTemplate restTemplate;

    public GoogleService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String authenticateUser(String idToken) {

        String url = googleTokenAuthUrl + idToken;

        try {
            return restTemplate.getForObject(url, String.class);
        } catch (HttpClientErrorException e) { //  (4xx)
            throw new GoogleAuthenticationException("Client error during Google authentication: " + e.getStatusCode() + " - " + e.getResponseBodyAsString(), e.getStatusCode().value());
        } catch (HttpServerErrorException e) { // (5xx)
            throw new GoogleAuthenticationException("Server error during Google authentication: " + e.getStatusCode() + " - " + e.getResponseBodyAsString(), e.getStatusCode().value());
        } catch (RestClientException e) {
            throw new GoogleAuthenticationException("Google authentication failed: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
}
