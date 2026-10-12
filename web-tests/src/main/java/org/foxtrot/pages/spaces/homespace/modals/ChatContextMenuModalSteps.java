package org.foxtrot.pages.spaces.homespace.modals;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import io.qameta.allure.Step;
import org.foxtrot.pages.spaces.homespace.modals.elements.ChatContextMenuModal;

import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class ChatContextMenuModalSteps {

    private final ChatContextMenuModal elements = new ChatContextMenuModal();

    @Step("Нажать кнопку, удалить чат")
    public ChatContextMenuModalSteps clickDeleteChatButton() {
        $(elements.getDeleteChatButton()).shouldBe(visible).click();
        return this;
    }

    @Step("Нажать кнопку, 'Пометить прочитанным'")
    public ChatContextMenuModalSteps clickMarkAsReadButton() {
        $(elements.getMarkAsReadButton()).shouldBe(visible).click();
        return this;
    }

    @Step("Нажать кнопку 'Пометить непрочитанным'")
    public ChatContextMenuModalSteps clickMarkAsUnreadButton() {
        $(elements.getMarkAsUnreadButton()).shouldBe(clickable).click();
        return this;
    }

    @Step("Проверить наличие пункта 'Пометить непрочитанным'")
    public ChatContextMenuModalSteps verifyMarkAsUnreadIsVisible() {
        $(elements.getMarkAsUnreadButton()).shouldBe(visible);
        return this;
    }

    @Step("Проверить отсутствие пункта 'Пометить непрочитанным'")
    public ChatContextMenuModalSteps verifyMarkAsUnreadIsNotVisible() {
        $(elements.getMarkAsUnreadButton()).shouldNotBe(visible, Duration.ofSeconds(5));
        return this;
    }

    @Step("Проверить наличие пункта 'Пометить прочитанным'")
    public ChatContextMenuModalSteps verifyMarkAsReadIsVisible() {
        $(elements.getMarkAsReadButton()).shouldBe(visible);
        return this;
    }

    @Step("Проверить отсутствие пункта 'Пометить прочитанным'")
    public ChatContextMenuModalSteps verifyMarkAsReadIsNotVisible() {
        $(elements.getMarkAsReadButton()).shouldNot(visible);
        return this;
    }
}
