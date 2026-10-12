package org.foxtrot.pages.modals;

import io.qameta.allure.Step;
import org.foxtrot.pages.modals.elements.GroupUnavailableModal;

import static com.codeborne.selenide.Condition.interactable;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class GroupUnavailableModalSteps {
    GroupUnavailableModal elements = new GroupUnavailableModal();

    @Step("Проверить, отображение модального окна 'Группы/канала не существует'")
    public GroupUnavailableModalSteps verifyModalVisible() {
        $(elements.getModalContainer()).shouldBe(visible);
        return this;
    }

    @Step("Нажать кнопку 'Закрыть'")
    public GroupUnavailableModalSteps clickCloseButton() {
        $(elements.getCloseButton())
                .shouldBe(visible)
                .shouldBe(interactable)
                .click();
        return this;
    }
}
