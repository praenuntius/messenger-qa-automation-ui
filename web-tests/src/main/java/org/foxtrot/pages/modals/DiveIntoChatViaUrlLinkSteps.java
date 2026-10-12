package org.foxtrot.pages.modals;

import io.qameta.allure.Step;
import org.foxtrot.pages.modals.elements.DiveIntoChatViaUrlLink;

import static com.codeborne.selenide.Condition.interactable;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class DiveIntoChatViaUrlLinkSteps {

    DiveIntoChatViaUrlLink elements = new DiveIntoChatViaUrlLink();

    @Step("Проверить, что отображается сообщение об ограничении доступа")
    public DiveIntoChatViaUrlLinkSteps verifyAccessRestrictionMessageVisible() {
        $(elements.getNoAccessMessage()).shouldBe(visible);
        return this;
    }

    @Step("Нажать кнопку 'Закрыть'")
    public DiveIntoChatViaUrlLinkSteps clickCloseButton() {
        $(elements.getCloseModalButton()).shouldBe(interactable).click();
        return this;
    }
}
