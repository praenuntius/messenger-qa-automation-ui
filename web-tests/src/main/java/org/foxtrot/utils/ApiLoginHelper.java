package org.foxtrot.utils;

import com.codeborne.selenide.WebDriverRunner;
import org.foxtrot.pages.spaces.homespace.primary.HomespaceSteps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.codeborne.selenide.Selenide.executeJavaScript;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.refresh;


public class ApiLoginHelper {
    private static final Logger logger = LoggerFactory.getLogger(ApiLoginHelper.class);

    private static String authToken;
    private static long tokenExpirationTime;

    public static HomespaceSteps performLoginWithToken(String token, String path) {
        open(UrlConstants.WORKSPACE_URL.replace("{workspaceId}", "") + (path != null ? path : ""));
        String authObject = String.format(
                "{\"access_token\": \"%s\", \"refresh_token\": \"\", \"expires_in\": 3600, \"token_type\": \"Bearer\"}",
                token
        );
        executeJavaScript("localStorage.setItem('auth', arguments[0]);", authObject);
        refresh();
        logger.info("Current URL after refresh: {}", WebDriverRunner.getWebDriver().getCurrentUrl());

        HomespaceSteps homespaceSteps = new HomespaceSteps();
        homespaceSteps.checkPageIsLoaded();
        return homespaceSteps;
    }

    public static String extractTokenFromLocalStorage() {
        String authJson = executeJavaScript("return localStorage.getItem('auth');").toString();
        if (authJson == null || authJson.isEmpty()) {
            throw new RuntimeException("Failed to extract auth object from localStorage");
        }
        try {
            String token = executeJavaScript("return JSON.parse(arguments[0]).access_token;", authJson).toString();
            if (token == null || token.isEmpty()) {
                throw new RuntimeException("Failed to extract access_token from auth object");
            }
            return token;
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse auth object from localStorage: " + authJson, e);
        }
    }

    public static boolean isTokenExpired() {
        return System.currentTimeMillis() >= tokenExpirationTime;
    }

    public static String getAuthToken() {
        if (authToken == null || isTokenExpired()) {
            throw new RuntimeException("Auth token is expired or not set. Please perform UI login first.");
        }
        return authToken;
    }

    public static void clearAuthToken() {
        authToken = null;
        tokenExpirationTime = 0;
    }
}
