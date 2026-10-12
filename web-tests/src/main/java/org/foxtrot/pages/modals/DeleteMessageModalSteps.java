package org.foxtrot.pages.modals;

import io.qameta.allure.Step;
import org.foxtrot.pages.modals.elements.DeleteMessage;

import java.time.Duration;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class DeleteMessageModalSteps {

    private final DeleteMessage elements = new DeleteMessage();

    @Step("Кликнуть по области внутри модального окна удаления сообщения")
    public DeleteMessageModalSteps clickModalContainer() {
        $(elements.getModalContainer())
                .shouldBe(visible, Duration.ofSeconds(10))
                .click();
        return this;
    }

    @Step("Кликнуть по области вне модального окна удаления сообщения")
    public DeleteMessageModalSteps clickOutsideModalContainer() {
        $(elements.getOutsideModalContainer())
                .shouldBe(visible, Duration.ofSeconds(10))
                .click();
        return this;
    }

    @Step("Кликнуть по кнопке закрытия модального окна удаления сообщения (крестик)")
    public DeleteMessageModalSteps clickCloseModalButton() {
        $(elements.getCloseModalButton())
                .shouldBe(visible, Duration.ofSeconds(10))
                .click();
        return this;
    }

    @Step("Кликнуть по кнопке 'Отмена' в модальном окне удаления сообщения")
    public DeleteMessageModalSteps clickCancelButton() {
        $(elements.getCancelButton())
                .shouldBe(visible, Duration.ofSeconds(10))
                .click();
        return this;
    }

    @Step("Кликнуть по кнопке 'Удалить' в модальном окне удаления сообщения")
    public DeleteMessageModalSteps clickDeleteButton() {
        $(elements.getDeleteButton())
                .shouldBe(visible, Duration.ofSeconds(10))
                .click();
        return this;
    }

    @Step("Проверить отображение сообщения 'Вы не можете удалять чужие сообщения'")
    public DeleteMessageModalSteps verifyCannotDeleteMessageLabelIsVisible() {
        $(elements.getCannotDeleteMessageLabel())
                .shouldBe(visible, Duration.ofSeconds(10));
        return this;
    }

}
