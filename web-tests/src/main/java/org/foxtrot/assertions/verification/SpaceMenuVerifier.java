package org.foxtrot.assertions.verification;

import io.qameta.allure.Step;
import org.foxtrot.pages.spaces.workspace.left_sidebar.elements.SpaceMenu;

import java.time.Duration;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class SpaceMenuVerifier {

    private final SpaceMenu spaceMenu = new SpaceMenu();

    @Step("Проверить меню Владельца рабочего пространства: имя={spaceName}")
    public SpaceMenuVerifier verifyOwnerMenu(String spaceName) {
        verifyCloseSpaceMenuButton()
                .verifyInviteUserButton()
                .verifyCreateGroupButton()
                .verifyCreateChannelButton()
                .verifyNotificationsONToggle()
                .verifyParticipantsButton()
                .verifyGroupsAndChannelsButton()
                .verifyEditSpaceButton()
                .verifyQuitSpaceButton()
                .verifyDeleteSpaceButton()
                .verifySpaceName(spaceName);
        return this;
    }

    @Step("Проверить меню Админа пространства: имя={spaceName}")
    public SpaceMenuVerifier verifyAdminMenu(String spaceName) {
        verifyCloseSpaceMenuButton()
                .verifyInviteUserButton()
                .verifyCreateGroupButton()
                .verifyCreateChannelButton()
                .verifyNotificationsONToggle()
                .verifyParticipantsButton()
                .verifyGroupsAndChannelsButton()
                .verifyEditSpaceButton()
                .verifyQuitSpaceButton()
                .verifyDeleteSpaceButton()
                .verifySpaceName(spaceName);
        return this;
    }

    @Step("Проверить меню Участника пространства: имя={spaceName}")
    public SpaceMenuVerifier verifyParticipantMenu(String spaceName) {
        verifyCloseSpaceMenuButton()
                .verifyCreateGroupButton()
                .verifyCreateChannelButton()
                .verifyNotificationsONToggle()
                .verifyParticipantsButton()
                .verifyGroupsAndChannelsButton()
                .verifyQuitSpaceButton()
                .verifySpaceName(spaceName);
        return this;
    }

    @Step("Проверить имя пространства: {expectedName}")
    public SpaceMenuVerifier verifySpaceName(String expectedName) {
        $(spaceMenu.getSpaceName())
                .shouldBe(visible, Duration.ofSeconds(5))
                .shouldHave(text(expectedName));
        return this;
    }

    @Step("Проверить наличие кнопки [←] (закрыть меню пространства)")
    public SpaceMenuVerifier verifyCloseSpaceMenuButton() {
        $(spaceMenu.getSpaceMenuBackButton())
                .shouldBe(visible, Duration.ofSeconds(5));
        return this;
    }

    @Step("Проверить наличие кнопки 'Пригласить в Пространство'")
    public SpaceMenuVerifier verifyInviteUserButton() {
        $(spaceMenu.getWorkspaceInviteUserMenuButton())
                .shouldBe(visible, Duration.ofSeconds(5));
        return this;
    }

    @Step("Проверить наличие кнопки 'Создать группу'")
    public SpaceMenuVerifier verifyCreateGroupButton() {
        $(spaceMenu.getWorkspaceCreateGroupMenuButton())
                .shouldBe(visible, Duration.ofSeconds(5));
        return this;
    }

    @Step("Проверить наличие кнопки 'Создать канал'")
    public SpaceMenuVerifier verifyCreateChannelButton() {
        $(spaceMenu.getWorkspaceCreateChannelMenuButton())
                .shouldBe(visible, Duration.ofSeconds(5));
        return this;
    }

    @Step("Проверить, что тоггл 'Уведомления' включен")
    public SpaceMenuVerifier verifyNotificationsONToggle() {
        $(spaceMenu.getWorkspaceNotificationsOnToggle())
                .shouldBe(visible, Duration.ofSeconds(5));
        return this;
    }

    @Step("Проверить, что тоггл 'Уведомления' выключен")
    public SpaceMenuVerifier verifyNotificationsOFFToggle() {
        $(spaceMenu.getWorkspaceNotificationsOffToggle())
                .shouldBe(visible, Duration.ofSeconds(5));
        return this;
    }

    @Step("Проверить наличие кнопки 'Участники пространства'")
    public SpaceMenuVerifier verifyParticipantsButton() {
        $(spaceMenu.getWorkspaceMembersMenuButton())
                .shouldBe(visible, Duration.ofSeconds(5));
        return this;
    }

    @Step("Проверить наличие кнопки 'Группы и каналы'")
    public SpaceMenuVerifier verifyGroupsAndChannelsButton() {
        $(spaceMenu.getWorkspaceGroupsAndChannelsMenuButton())
                .shouldBe(visible, Duration.ofSeconds(5));
        return this;
    }

    @Step("Проверить наличие кнопки 'Редактировать пространство'")
    public SpaceMenuVerifier verifyEditSpaceButton() {
        $(spaceMenu.getWorkspaceEditMenuButton())
                .shouldBe(visible, Duration.ofSeconds(5));
        return this;
    }

    @Step("Проверить наличие кнопки 'Выйти из пространства'")
    public SpaceMenuVerifier verifyQuitSpaceButton() {
        $(spaceMenu.getWorkspaceLogoutButton())
                .shouldBe(visible, Duration.ofSeconds(5));
        return this;
    }

    @Step("Проверить наличие кнопки 'Удалить пространство'")
    public SpaceMenuVerifier verifyDeleteSpaceButton() {
        $(spaceMenu.getWorkspaceDeleteButton())
                .shouldBe(visible, Duration.ofSeconds(5));
        return this;
    }
}