package org.foxtrot.pages.spaces.homespace.modals;

import io.qameta.allure.Step;
import org.foxtrot.pages.spaces.homespace.modals.elements.DeleteChatModal;

import java.time.Duration;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class DeleteChatModalSteps {

    DeleteChatModal elements = new DeleteChatModal();

    @Step("Нажать кнопку, удалить чат")
    public DeleteChatModalSteps clickDeleteChatButton() {
        $(elements.getDeleteButton()).shouldBe(visible, Duration.ofSeconds(5)).click();
        return null;
    }
}
