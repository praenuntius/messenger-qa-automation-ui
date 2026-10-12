package org.foxtrot.pages.chat.system_messages;

import io.qameta.allure.Step;
import org.foxtrot.pages.chat.system_messages.elements.SystemMessages;

import java.time.Duration;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class SystemMessagesSteps {

    private final SystemMessages elements = new SystemMessages();

    @Step("Проверить системное сообщение о закреплении с цитатой '{messageText}'")
    public SystemMessagesSteps verifyPinnedSystemMessage(String messageText) {
        $(elements.getPinnedSystemMessage())
                .shouldBe(visible, Duration.ofSeconds(10))
                .shouldHave(text(messageText));
        return this;
    }

    @Step("Кликнуть по системному уведомленю о закреплении сообщения '{messageText}'")
    public SystemMessagesSteps clickPinnedSystemMessage(String messageText) {
        org.foxtrot.pages.modals.WebOnboardingOverlay.dismissIfPresent();
        $(elements.getPinnedSystemMessage())
                .shouldBe(visible, Duration.ofSeconds(10))
                .shouldHave(text(messageText))
                .click();
        return this;
    }
}
