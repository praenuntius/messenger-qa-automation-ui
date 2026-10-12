package org.foxtrot.pages.chat.right_sidebar_menu.modals;

import io.qameta.allure.Step;
import org.foxtrot.pages.chat.right_sidebar_menu.modals.elements.MakeGroupAdminModal;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class MakeGroupAdminModalSteps {

    MakeGroupAdminModal elements = new MakeGroupAdminModal();

    @Step("Нажать кнопку 'Сделать администратором'")
    public void appointAsAdmin() {
        $(elements.getMakeAdminButton()).shouldBe(visible).click();
    }

    @Step("Нажать кнопку 'Убрать из администраторов группы'")
    public void removeUserFromAdmins() {
        $(elements.getRemoveUserFromAdminsButton()).shouldBe(visible).click();
    }

    @Step("Нажать кнопку 'Отмена'")
    public void clickCancelButton() {
        $(elements.getCancelButton()).shouldBe(visible).click();
    }

    @Step("Проверить видимость кнопки 'Убрать из администраторов группы'")
    public void verifyRemoveAdminButtonVisible() {
        $(elements.getRemoveUserFromAdminsButton()).shouldBe(visible);
    }



}
