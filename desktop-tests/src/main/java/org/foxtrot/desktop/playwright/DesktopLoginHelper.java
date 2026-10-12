package org.foxtrot.desktop.playwright;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.aeonbits.owner.ConfigFactory;
import org.foxtrot.api.constants.TestConstants;
import org.foxtrot.desktop.config.DesktopConfig;
import org.foxtrot.enums.UsersEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

/**
 * Логин в десктоп-приложении через Playwright.
 * Использует те же data-cy-атрибуты, что и web-тесты (LoginHelper).
 */
public class DesktopLoginHelper {

    private static final Logger logger = LoggerFactory.getLogger(DesktopLoginHelper.class);
    private static final DesktopConfig config = ConfigFactory.create(DesktopConfig.class);

    private static final int DEFAULT_TIMEOUT_MS = 30_000;
    private static final int LOGIN_TIMEOUT_MS = 60_000;

    private final Page page;

    public DesktopLoginHelper(Page page) {
        this.page = page;
    }

    /**
     * Выполняет полный UI-логин: телефон → SMS → 2FA.
     */
    public void login(UsersEnum user) {
        login(user.getPhoneNumber(), TestConstants.SMS_CODE, TestConstants.TFA_CODE);
    }

    public void login(String phone, String smsCode, String tfaCode) {
        logger.info("Выполняю логин: phone={}", phone);

        // Переходим на baseUrl (Electron должен уже открыть страницу, но явно открываем)
        page.navigate(config.appBaseUrl());

        // Ввод телефона
        page.waitForSelector("[data-cy='signInPhoneInput']",
                new Page.WaitForSelectorOptions().setTimeout(LOGIN_TIMEOUT_MS));
        page.fill("[data-cy='signInPhoneInput']", phone);
        page.keyboard().press("Enter");

        // Ввод SMS-кода
        page.waitForSelector("[data-cy='signInSmsCodeInput']",
                new Page.WaitForSelectorOptions().setTimeout(DEFAULT_TIMEOUT_MS));
        page.fill("[data-cy='signInSmsCodeInput']", smsCode);

        // Ввод 2FA
        page.waitForSelector("[data-cy='inputLoginTFACode']",
                new Page.WaitForSelectorOptions().setTimeout(DEFAULT_TIMEOUT_MS));
        page.fill("[data-cy='inputLoginTFACode']", tfaCode);
        page.keyboard().press("Enter");

        // Ждём загрузки главного экрана
        page.waitForSelector("[data-cy='sidebarWorkspaceButtonЧаты']",
                new Page.WaitForSelectorOptions()
                        .setTimeout(LOGIN_TIMEOUT_MS)
                        .setState(WaitForSelectorState.VISIBLE));

        logger.info("Логин успешен");
    }

    /**
     * Логаут через localStorage (быстрее чем UI-логаут).
     * Эквивалент ApiLoginHelper.clearAuth из web-тестов.
     */
    public void logout() {
        logger.info("Выполняю logout");
        try {
            page.evaluate("() => { localStorage.removeItem('auth'); localStorage.removeItem('encryptKey'); }");
            page.navigate(config.appBaseUrl());
            // Убеждаемся, что открылась страница логина
            page.waitForSelector("[data-cy='signInPhoneInput']",
                    new Page.WaitForSelectorOptions().setTimeout(DEFAULT_TIMEOUT_MS));
            logger.info("Logout успешен");
        } catch (Exception e) {
            logger.warn("Ошибка при logout: {}", e.getMessage());
        }
    }
}
