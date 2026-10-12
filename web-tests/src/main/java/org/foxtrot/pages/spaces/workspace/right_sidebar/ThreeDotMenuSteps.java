package org.foxtrot.pages.spaces.workspace.right_sidebar;

import io.qameta.allure.Step;
import org.foxtrot.pages.spaces.workspace.right_sidebar.elements.ThreeDotMenu;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class ThreeDotMenuSteps {

    private final ThreeDotMenu elements = new ThreeDotMenu();

    @Step("Сделать пользователя администратором")
    public void MakeAdminButton() {
        $(elements.getMakeAdminButton()).shouldBe(visible).click();
    }

    @Step("Удалить пользователя из списка администраторов")
    public void RemoveFromAdminButton() {
        $(elements.getRemoveFromAdminsButton()).shouldBe(visible).click();
    }

    @Step("Удалить пользователя из пространства")
    public void deleteFromSpaceButton() {
        $(elements.getDeleteFromSpaceButton()).shouldBe(visible).click();
    }

    @Step("Проверить видимость кнопки 'Убрать из администраторов группы'")
    public void verifyRemoveAdminButtonVisible() {
        $(elements.getRemoveFromAdminsButton()).shouldBe(visible);
    }

    @Step("Проверить видимость кнопки 'Сделать администратором'")
    public void verifyMakeAdminButtonVisible() {
        $(elements.getMakeAdminButton()).shouldBe(visible);
    }

}
