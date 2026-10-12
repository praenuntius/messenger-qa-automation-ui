package org.foxtrot.api.manager;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TokenStorage {
    private static final String FILE_PATH = "target/user-tokens.json";
    private final ObjectMapper mapper = new ObjectMapper();

    public Map<String, String> loadTokens() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return new HashMap<>();
        }
        try {
            return mapper.readValue(file, new TypeReference<Map<String, String>>() {
            });
        } catch (IOException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    public void saveTokens(Map<String, String> tokens) {
        try {
            mapper.writeValue(new File(FILE_PATH), tokens);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean hasToken(String login) {
        Map<String, String> tokens = loadTokens();
        return tokens.containsKey(login);
    }

    public String getToken(String login) {
        Map<String, String> tokens = loadTokens();
        return tokens.get(login);
    }

    public void putToken(String login, String token) {
        Map<String, String> tokens = loadTokens();
        tokens.put(login, token);
        saveTokens(tokens);
    }
}