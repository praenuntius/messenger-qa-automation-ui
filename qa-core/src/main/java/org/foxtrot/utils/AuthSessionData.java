package org.foxtrot.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.foxtrot.api.dto.auth.response.SignInResponse;

import java.util.Collections;
import java.util.Map;

/**
 * Результат авторизации через API: тело ответа signIn и куки из ответа (для установки в браузере).
 */
@Getter
@AllArgsConstructor
public class AuthSessionData {
    private final SignInResponse signInResponse;
    private final Map<String, String> cookies;

    public static AuthSessionData of(SignInResponse signInResponse, Map<String, String> cookies) {
        return new AuthSessionData(signInResponse, cookies != null ? cookies : Collections.emptyMap());
    }
}
