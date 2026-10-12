package org.foxtrot.pages.chat.right_sidebar_menu.modals;

import io.qameta.allure.Step;
import org.foxtrot.pages.chat.right_sidebar_menu.modals.elements.LeaveGroupAndChannelModal;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class LeaveGroupAndChannelModalSteps {

    LeaveGroupAndChannelModal elements = new LeaveGroupAndChannelModal();

    @Step("Нажать кнопку 'Покинуть'")
    public void clickLeaveButton() {
        $(elements.getLeaveButton()).shouldBe(visible).click();
    }

    @Step("Нажать кнопку 'Отмена'")
    public void clickCancelButton() {
        $(elements.getCancelButton()).shouldBe(visible).click();
    }
}