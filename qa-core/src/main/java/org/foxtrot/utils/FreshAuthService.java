package org.foxtrot.utils;

import io.restassured.response.Response;
import org.foxtrot.api.dto.auth.response.SignInResponse;
import org.foxtrot.api.service.AuthApiService;
import org.foxtrot.enums.UsersEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Получение актуальных данных авторизации через цепочку API (start → verifyCode → verifyPassword → signIn).
 * Не использует сохранённый localStorage; каждый вызов выполняет все 4 запроса и возвращает полный ответ signIn.
 */
public class FreshAuthService {

    private static final Logger logger = LoggerFactory.getLogger(FreshAuthService.class);
    private final AuthApiService authApiService = new AuthApiService();
    private final EncryptionHelper encryptionHelper = new EncryptionHelper();

    /**
     * Выполняет полную цепочку авторизации по номеру телефона и возвращает полный ответ signIn.
     * Номер может быть в формате 9XXXXXXXXX или 7XXXXXXXXXX (приведётся к 7XXXXXXXXXX).
     */
    public SignInResponse getFreshSignInResponse(String phone) {
        EncryptionHelper.EncryptKeyInfo keys = encryptionHelper.setupEncryptionKeys();
        return getFreshSignInResponse(phone, keys.getMessageKey());
    }

    /**
     * То же с указанием симметричного ключа для encrypted auth/start и signIn.
     */
    public SignInResponse getFreshSignInResponse(String phone, SymmetricKeyMaterial cipherKey) {
        logger.info("Получаем свежие данные авторизации для номера: {}", phone);
        SignInResponse response = authApiService.performFullAuthFlow(phone, cipherKey);
        logger.info("Авторизация успешна, accountId={}", response.getAccount() != null ? response.getAccount().getId() : "?");
        return response;
    }

    /**
     * @deprecated используйте {@link #getFreshSignInResponse(String, SymmetricKeyMaterial)}
     */
    @Deprecated
    public SignInResponse getFreshSignInResponse(String phone, long encryptionKeyId) {
        EncryptionHelper.EncryptKeyInfo keys = encryptionHelper.setupEncryptionKeys();
        return getFreshSignInResponse(phone, keys.getMessageKey());
    }

    /**
     * То же по пользователю из enum (используется {@link UsersEnum#getPhoneNumber()}).
     */
    public SignInResponse getFreshSignInResponse(UsersEnum user) {
        return getFreshSignInResponse(user.getPhoneNumber());
    }

    /**
     * По пользователю и симметричному ключу (для входа с предварительно созданными ключами).
     */
    public SignInResponse getFreshSignInResponse(UsersEnum user, SymmetricKeyMaterial cipherKey) {
        return getFreshSignInResponse(user.getPhoneNumber(), cipherKey);
    }

    /**
     * @deprecated используйте {@link #getFreshSignInResponse(UsersEnum, SymmetricKeyMaterial)}
     */
    @Deprecated
    public SignInResponse getFreshSignInResponse(UsersEnum user, long encryptionKeyId) {
        return getFreshSignInResponse(user.getPhoneNumber(), encryptionKeyId);
    }

    /**
     * Полная цепочка авторизации по пользователю с возвратом ответа signIn и cookies из ответа API
     * (для установки в браузере — сервер может требовать сессионные куки в дополнение к токену).
     */
    public AuthSessionData getFreshSignInResponseWithCookies(UsersEnum user, SymmetricKeyMaterial cipherKey) {
        logger.info("Получаем свежие данные авторизации для номера: {}", user.getPhoneNumber());
        Response signInResponse = authApiService.performFullAuthFlowResponse(user.getPhoneNumber(), cipherKey);
        SignInResponse body = signInResponse.getBody().as(SignInResponse.class);
        Map<String, String> cookies = signInResponse.getCookies();
        logger.info("Авторизация успешна, accountId={}, cookies={}", body.getAccount() != null ? body.getAccount().getId() : "?", cookies != null ? cookies.size() : 0);
        return AuthSessionData.of(body, cookies);
    }

    /**
     * @deprecated используйте {@link #getFreshSignInResponseWithCookies(UsersEnum, SymmetricKeyMaterial)}
     */
    @Deprecated
    public AuthSessionData getFreshSignInResponseWithCookies(UsersEnum user, long encryptionKeyId) {
        EncryptionHelper.EncryptKeyInfo keys = encryptionHelper.setupEncryptionKeys();
        return getFreshSignInResponseWithCookies(user, keys.getMessageKey());
    }
}
