package com.wine.api.api_gateway.service;

import com.wine.api.api_gateway.api.DefaultApi;
import com.wine.api.api_gateway.invoker.ApiClient;
import com.wine.api.api_gateway.invoker.ApiException;
import com.wine.api.api_gateway.model.User;
import org.springframework.stereotype.Service;

@Service
public class DatabaseService {

    private final DefaultApi defaultApi;

    public DatabaseService(ApiClient apiClient) {
        this.defaultApi = new DefaultApi(apiClient);
    }

    public User retrieveOrCreateUserById(User user) throws ApiException {

            return defaultApi.apiV1UsersPost(user);
    }
}
