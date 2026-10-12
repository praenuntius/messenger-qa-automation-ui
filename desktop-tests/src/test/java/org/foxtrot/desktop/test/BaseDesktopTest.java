package org.foxtrot.desktop.test;

import com.microsoft.playwright.Page;
import org.foxtrot.desktop.extensions.DesktopTestLifecycleExtension;
import org.foxtrot.desktop.playwright.DesktopLoginHelper;
import org.foxtrot.desktop.playwright.PlaywrightManager;
import org.foxtrot.enums.UsersEnum;
import org.foxtrot.utils.UserTestDataCleaner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Базовый класс для всех desktop-тестов.
 * Аналог BaseMobileTest и BaseTest.
 * <p>
 * Наследники объявляют пользователей и вызывают cleaner в @AfterEach своего класса.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(DesktopTestLifecycleExtension.class)
public abstract class BaseDesktopTest {

    /**
     * Текущая Playwright Page для текущего потока.
     * Доступна после beforeEach.
     */
    protected Page page() {
        return PlaywrightManager.currentPage();
    }

    /**
     * Helper для логина/логаута.
     */
    protected DesktopLoginHelper loginHelper() {
        return new DesktopLoginHelper(page());
    }

    /**
     * Выполняет API-чистку тест-данных для указанных пользователей.
     * Вызывайте в @AfterEach своего теста вместе с logout.
     */
    protected void cleanUsers(UsersEnum... users) {
        new UserTestDataCleaner().clean(users);
    }
}
