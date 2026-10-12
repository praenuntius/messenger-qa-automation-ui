package org.foxtrot.pages.modals;

import io.qameta.allure.Step;
import org.foxtrot.pages.modals.elements.RemoveUserFromSpaceModal;

import java.time.Duration;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class RemoveUserFromSpaceModalSteps {

    RemoveUserFromSpaceModal elements = new RemoveUserFromSpaceModal();


    @Step("Нажать на кнопку 'Удалить'")
    public RemoveUserFromSpaceModalSteps clickDeleteButton() {
        $(elements.getDeleteButton()).shouldBe(visible, Duration.ofSeconds(15)).click();
        return this;
    }

    @Step("Нажать на кнопку 'Отмена'")
    public RemoveUserFromSpaceModalSteps clickCancelButton() {
        $(elements.getCancelButton()).shouldBe(visible, Duration.ofSeconds(15)).click();
        return this;
    }
}
