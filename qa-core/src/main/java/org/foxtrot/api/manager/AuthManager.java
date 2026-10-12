package org.foxtrot.api.manager;

import org.foxtrot.api.service.AuthApiService;

public class AuthManager {
    private final TokenStorage tokenStorage = new TokenStorage();
    AuthApiService authApiService = new AuthApiService();


    public String getToken(String user) {
        String login = user;
        if (tokenStorage.hasToken(login)) {
            return tokenStorage.getToken(login);
        }
        String newToken = authApiService.fetchNewTokenMultiStep(user);
        tokenStorage.putToken(login, newToken);
        return newToken;
    }
}
