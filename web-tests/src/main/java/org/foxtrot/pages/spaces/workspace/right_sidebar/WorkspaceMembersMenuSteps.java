package org.foxtrot.pages.spaces.workspace.right_sidebar;

import io.qameta.allure.Step;
import org.foxtrot.pages.chat.right_sidebar_menu.modals.MakeSpaceAdminModalSteps;
import org.foxtrot.pages.modals.RemoveUserFromSpaceModalSteps;
import org.foxtrot.pages.spaces.workspace.right_sidebar.elements.WorkspaceMembersMenu;
import org.openqa.selenium.By;

import java.time.Duration;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;
import static org.openqa.selenium.By.xpath;

public class WorkspaceMembersMenuSteps {

    private final ThreeDotMenuSteps threeDotMenuSteps = new ThreeDotMenuSteps();
    private final WorkspaceMembersMenu elements = new WorkspaceMembersMenu();
    private final MakeSpaceAdminModalSteps makeSpaceAdminModalSteps = new MakeSpaceAdminModalSteps();
    private final RemoveUserFromSpaceModalSteps removeUserFromSpaceModalSteps = new RemoveUserFromSpaceModalSteps();
    // добавить селекторы и описание кликов в поле ввода поиска по участникам, сброс поискового запроса
    // добавить селекторы и описание клика по кнопке [X] закрыть меню участников пространства

    @Step("Проверить наличие заглушки с инициалами у пользователя {userName} списке подписчиков")
    public void verifyChannelSubscribersHasInitialsAvatarPlaceholder(String userName, String phone) {
        String photo = elements.getDefinedUserWithNameAndAvatar().replace("{username}", userName);
        String placeholder = elements.getDefinedUserWithAvatarPlaceholder().replace("{username}", phone);
        checkUserPresenceInSpace(userName);
        $(xpath(photo)).shouldNotBe(visible);
        $(xpath(placeholder)).shouldNotBe(visible);
    }

    @Step("Проверить наличие серой заглушки у пользователя {userName} списке подписчиков")
    public void verifyChannelSubscribersHasAvatarPlaceholder(String userName, String phone, boolean phoneVisible) {
        $(xpath(elements.getDefinedUserWithNameAndAvatar().replace("{username}", userName))).shouldNotBe(visible);

        String selector = phoneVisible
                ? elements.getDefinedUserWithAvatarPlaceholder().replace("{username}", phone)
                : elements.getDefinedUserWithAvatarPlaceholder().replace("{username}", userName);

        $(xpath(selector)).shouldBe(visible, Duration.ofSeconds(10));
    }

    @Step("Проверить наличие аватарки у пользователя {userName} в списке подписчиков")
    public void verifyChannelSubscribersHasAvatar(String userName, String phone) {
        String photo = elements.getDefinedUserWithNameAndAvatar().replace("{username}", userName);
        String placeholder = elements.getDefinedUserWithAvatarPlaceholder().replace("{username}", phone);
        checkUserPresenceInSpace(userName);
        $(xpath(photo)).shouldBe(visible);
        $(xpath(placeholder)).shouldNotBe(visible);
    }

    @Step("Проверить наличие пользователя {userName} в пространстве")
    public void checkUserPresenceInSpace(String userName) {
        $(By.xpath(elements.getDefinedUserButton().replace("{username}", userName)))
                .shouldBe(visible);
    }

    @Step("Проверить отсутствие пользователя {userName} в пространстве")
    public void checkUserNotPresentInSpace(String userName) {
        $(By.xpath(elements.getDefinedUserButton().replace("{username}", userName)))
                .shouldNotBe(visible);
    }

    @Step("Навести курсор на карточку контакта '{memberName}'")
    public void findAndHoverUser(String memberName) {
        String selector = elements.getDefinedUserButton().replace("{username}", memberName);
        $(xpath(selector)).shouldBe(visible).hover();
        openUserContextMenu(memberName);
    }

    @Step("Назначить '{memberName}' админом пространства")
    public void appointAsAdmin(String memberName) {
        findAndHoverUser(memberName);
        threeDotMenuSteps.MakeAdminButton();
    }

    @Step("Удалить '{memberName}' из администраторов пространства")
    public void removeFromAdmins(String memberName) {
        findAndHoverUser(memberName);
        threeDotMenuSteps.RemoveFromAdminButton();
        makeSpaceAdminModalSteps.removeUserFromAdmins();
    }

    @Step("Удалить '{memberName}' из пространства")
    public void deleteFromWorkspace(String memberName) {
        findAndHoverUser(memberName);
        threeDotMenuSteps.deleteFromSpaceButton();
        removeUserFromSpaceModalSteps.clickDeleteButton();
    }

    @Step("Вызвать контекстное меню у пользователя {userName} ")
    public void openUserContextMenu(String memberName) {
        String selector = elements.getDefinedThreeDotMenuButton().replace("{username}", memberName);
        $x(selector).shouldBe(visible).click();
    }

    @Step("Проверить отсутствие контекстного меню у '{memberName}'")
    public void verifyContextMenuNotAvailableForUser(String memberName) {
        String selector = elements.getDefinedThreeDotMenuButton().replace("{username}", memberName);
        $x(selector).shouldNotBe(visible);
    }

    @Step("Назначить {userName} админом пространства")
    public void appointUserAsSpaceAdmin(String userName) {
        findAndHoverUser(userName);
        threeDotMenuSteps.MakeAdminButton();
        makeSpaceAdminModalSteps.appointAsAdmin();
    }

    @Step("Проверить, что пользователь {userName} является администратором")
    public void verifyUserIsAdministrator(String userName) {
        findAndHoverUser(userName);
        threeDotMenuSteps.verifyRemoveAdminButtonVisible();
    }

    @Step("Проверить, что пользователь {userName} не является администратором")
    public void verifyUserIsNotAdministrator(String userName) {
        findAndHoverUser(userName);
        threeDotMenuSteps.verifyMakeAdminButtonVisible();
    }
}
