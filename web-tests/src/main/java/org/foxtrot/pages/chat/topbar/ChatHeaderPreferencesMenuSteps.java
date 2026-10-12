package org.foxtrot.pages.chat.topbar;

import io.qameta.allure.Step;
import org.foxtrot.pages.chat.topbar.elements.ChatHeaderPreferencesMenu;

import java.time.Duration;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class ChatHeaderPreferencesMenuSteps {

    private final ChatHeaderPreferencesMenu elements = new ChatHeaderPreferencesMenu();

    @Step("Проверить, что кнопка экспорта истории отображается в меню чата")
    public ChatHeaderPreferencesMenuSteps verifyExportHistoryButtonIsVisible() {
        $(elements.getExportHistoryButton()).shouldBe(visible, Duration.ofSeconds(5));
        return this;
    }

    @Step("Проверить, что кнопка экспорта истории не отображается в меню чата")
    public ChatHeaderPreferencesMenuSteps verifyExportHistoryButtonIsNotVisible() {
        $(elements.getMenuContainer()).shouldBe(visible, Duration.ofSeconds(5));
        $(elements.getExportHistoryButton()).shouldNotBe(visible, Duration.ofSeconds(3));
        return this;
    }

    @Step("Кликнуть по кнопке 'Экспорт истории чата'")
    public ChatHeaderPreferencesMenuSteps clickExportHistoryButton() {
        $(elements.getExportHistoryButton()).shouldBe(visible).click();
        return this;
    }
}
