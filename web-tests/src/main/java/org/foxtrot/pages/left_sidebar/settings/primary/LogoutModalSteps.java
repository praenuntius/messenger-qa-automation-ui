package org.foxtrot.pages.left_sidebar.settings.primary;

import io.qameta.allure.Step;
import org.foxtrot.pages.left_sidebar.settings.primary.elements.LogoutModal;

import java.time.Duration;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class LogoutModalSteps {

    private final LogoutModal elements = new LogoutModal();

    @Step("Проверить, что модалка 'Выйти из приложения?' отображается")
    public LogoutModalSteps verifyLogoutModalVisible() {
        $(elements.getTitle()).shouldBe(visible, Duration.ofSeconds(10));
        $(elements.getCancelButton()).shouldBe(visible, Duration.ofSeconds(10));
        $(elements.getConfirmButton()).shouldBe(visible, Duration.ofSeconds(10));
        return this;
    }

    @Step("Нажать 'Отмена' в модалке выхода из приложения")
    public LogoutModalSteps clickCancel() {
        $(elements.getCancelButton())
                .shouldBe(visible, Duration.ofSeconds(10))
                .click();
        return this;
    }

    @Step("Нажать 'Покинуть' в модалке выхода из приложения")
    public LogoutModalSteps clickConfirmLeave() {
        $(elements.getConfirmButton())
                .shouldBe(visible, Duration.ofSeconds(10))
                .click();
        return this;
    }
}
