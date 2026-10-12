package org.foxtrot.api.service;

/**
 * Пользователь уже прошёл регистрацию в мессенджере (verifyCode → verifyPassword).
 */
public class UserAlreadyRegisteredException extends RuntimeException {

    private final String phone;

    public UserAlreadyRegisteredException(String phone) {
        super("Пользователь уже зарегистрирован в мессенджере: " + phone);
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }
}
