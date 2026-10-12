package org.foxtrot.pages.chat.modals;

import io.qameta.allure.Step;
import org.foxtrot.pages.chat.modals.elements.ChatHistoryExportModal;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class ChatHistoryExportModalSteps {

    ChatHistoryExportModal elements = new ChatHistoryExportModal();

    @Step("Кликнуть по кнопке 'Экспорт'")
    public ChatHistoryExportModalSteps clickExportButton() {
        $(elements.getModalContainer()).shouldBe(visible);
        $(elements.getExportButton()).shouldBe(visible).click();
        return this;
    }
}