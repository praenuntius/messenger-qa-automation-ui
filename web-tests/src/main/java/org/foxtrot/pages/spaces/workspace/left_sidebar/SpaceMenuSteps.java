package org.foxtrot.pages.spaces.workspace.left_sidebar;

import io.qameta.allure.Step;
import org.foxtrot.pages.chat.right_sidebar_menu.tabs.members_menu.elements.MembersMenu;
import org.foxtrot.pages.modals.WebOnboardingOverlay;
import org.foxtrot.pages.spaces.workspace.left_sidebar.elements.SpaceMenu;

import java.time.Duration;

import static com.codeborne.selenide.Condition.clickable;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class SpaceMenuSteps {

    private final SpaceMenu elements = new SpaceMenu();
    private final MembersMenu membersMenuElements = new MembersMenu();

    @Step("Закрыть меню")
    public void closeMenuButton() {
        $(elements.getSpaceMenuBackButton()).click();
    }

    @Step("Пригласить пользователя в рабочее пространство")
    public void clickInviteUsertoWorkspaceButton() {
        WebOnboardingOverlay.dismissIfPresent();
        $(elements.getWorkspaceInviteUserMenuButton())
                .shouldBe(clickable, Duration.ofSeconds(15))
                .click();
    }

    @Step("Создать группу")
    public void clickCreateGroupButton() {
        $(elements.getWorkspaceCreateGroupMenuButton()).click();
    }

    @Step("Создать канал")
    public void clickCreateChannelButton() {
        $(elements.getWorkspaceCreateChannelMenuButton()).click();
    }

    @Step("Включить уведомления для рабочего пространства")
    public void notificationsOnToggle() {
        $(elements.getWorkspaceNotificationsOffToggle()).click();
    }

    @Step("Выключить уведомления для рабочего пространства")
    public void notificationsOffToggle() {
        $(elements.getWorkspaceNotificationsOnToggle()).click();
    }

    @Step("Открыть меню участников")
    public void membersButton() {
        WebOnboardingOverlay.dismissIfPresent();
        $(elements.getWorkspaceMembersMenuButton())
                .shouldBe(clickable, Duration.ofSeconds(15))
                .click();
    }

    @Step("Открыть раздел групп и каналов рабочего пространства")
    public void groupsAndChannelsButton() {
        $(elements.getWorkspaceGroupsAndChannelsMenuButton()).click();
    }

    @Step("Редактировать пространство")
    public void editButton() {
        $(elements.getWorkspaceEditMenuButton()).click();
    }

    @Step("Выйти из пространства")
    public void logoutButton() {
        $(elements.getWorkspaceLogoutButton()).click();
    }

    @Step("Удалить пространство")
    public void deleteButton() {
        $(elements.getWorkspaceDeleteButton()).click();
    }

    @Step("Проверить, что данные пространства отредактированы")
    public void verifyEditSpace(String name, String description) {
        verifyAvatar();
        verifyName(name);
        verifyDescription(description);
    }

    @Step("Проверить, что появился аватар")
    public void verifyAvatar() {
        $(elements.getHeaderAvatar()).shouldBe(visible);
    }
    @Step("Проверить, что имя равно {name}")
    public void verifyName(String name) {
        $(elements.getHeaderBlock()).shouldHave(text(name));
    }
    @Step("Проверить, что описание равно {description}")
    public void verifyDescription(String description) {
        $(elements.getHeaderBlock()).shouldHave(text(description));
    }
}
