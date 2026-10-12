package org.foxtrot.pages.left_sidebar.settings.primary;

import io.qameta.allure.Step;
import org.foxtrot.pages.left_sidebar.settings.primary.elements.SettingsMenu;

import java.time.Duration;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class SettingsMenuSteps {

    private final SettingsMenu elements = new SettingsMenu();

    @Step("Открыть 'Черный список'")
    public SettingsMenuSteps openBlackList() {
        $(elements.getBlackListButton()).shouldBe(visible).click();
        return this;
    }

    @Step("Проверить отображение заголовка настроек")
    public SettingsMenuSteps verifySettingsTitleHeaderVisible() {
        $(elements.getSettingsTitleHeader())
                .shouldBe(visible, Duration.ofSeconds(10));
        return this;
    }

    @Step("Закрыть меню настроек")
    public SettingsMenuSteps closeSettingsMenu() {
        $(elements.getCloseSettingsButton()).shouldBe(visible).click();
        return this;
    }

    @Step("Открыть раздел 'Оформление'")
    public SettingsMenuSteps openDesignTab() {
        $(elements.getDesignButton()).shouldBe(visible).click();
        return this;
    }

    @Step("Открыть модалку выхода из аккаунта (кнопка Exit в настройках)")
    public SettingsMenuSteps clickExit() {
        $(elements.getExitButton())
                .shouldBe(visible, Duration.ofSeconds(10))
                .scrollIntoView("{block: 'center'}")
                .click();
        return this;
    }
}
