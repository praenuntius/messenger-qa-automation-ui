package org.foxtrot.pages.left_sidebar.settings.setting_tabs;

import io.qameta.allure.Step;
import org.foxtrot.pages.left_sidebar.settings.setting_tabs.elements.NotificationsTab;

import java.time.Duration;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.executeJavaScript;

public class NotificationsTabSteps {

    private final NotificationsTab elements = new NotificationsTab();

    @Step("Проверить, что открыт раздел 'Уведомления и сообщения'")
    public NotificationsTabSteps verifyNotificationsTabOpened() {
        $(elements.getUnreadChatsCounterModeButton()).shouldBe(visible, Duration.ofSeconds(10));
        $(elements.getUnreadMessagesCounterModeButton()).shouldBe(visible, Duration.ofSeconds(10));
        return this;
    }

    @Step("Выбрать формат счетчика 'Количество чатов с непрочитанными сообщениями'")
    public NotificationsTabSteps selectUnreadChatsCounterMode() {
        var counterModeButton = $(elements.getUnreadChatsCounterModeButton())
                .should(exist, Duration.ofSeconds(10));
        executeJavaScript("arguments[0].click();", counterModeButton.toWebElement());
        return this;
    }
}