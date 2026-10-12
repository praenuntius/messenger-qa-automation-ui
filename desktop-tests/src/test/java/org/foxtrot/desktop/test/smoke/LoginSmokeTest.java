package org.foxtrot.desktop.test.smoke;

import io.qameta.allure.AllureId;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.foxtrot.desktop.test.BaseDesktopTest;
import org.foxtrot.enums.UsersEnum;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@Feature("Auth")
@Story("Логин / Логаут")
class LoginSmokeTest extends BaseDesktopTest {

    // TODO: заменить на пользователей из UsersEnum при первом реальном запуске
    private final UsersEnum user = UsersEnum.TEST50;

    @AfterEach
    void tearDown() {
        try {
            loginHelper().logout();
        } catch (Exception e) {
            // beforeEach мог не поднять сессию — logout пропускаем
        }
        cleanUsers(user);
    }

    @Test
    @AllureId("DESKTOP-001")
    void loginWithPhoneAndTfa() {
        // given
        loginHelper().login(user);

        // then — главная кнопка сайдбара видна
        assertThat(page().locator("[data-cy='sidebarWorkspaceButtonЧаты']"))
                .isVisible();
    }

    @Test
    @AllureId("DESKTOP-002")
    void logoutShowsLoginScreen() {
        // given
        loginHelper().login(user);

        // when
        loginHelper().logout();

        // then
        assertThat(page().locator("[data-cy='signInPhoneInput']"))
                .isVisible();
    }
}
