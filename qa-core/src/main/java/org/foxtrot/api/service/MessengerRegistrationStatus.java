package org.foxtrot.api.service;

/**
 * Статус регистрации пользователя в мессенджере после auth/start (+ verifyCode при необходимости).
 */
public enum MessengerRegistrationStatus {
    /** verifyCode → следующий шаг verifyPassword (пользователь уже зарегистрирован). */
    REGISTERED,
    /** verifyCode → следующий шаг submitAccount (нужна регистрация). */
    NOT_REGISTERED,
    /** verifyCode не прошёл (rate limit, неверный код) — статус неизвестен. */
    UNVERIFIED
}
