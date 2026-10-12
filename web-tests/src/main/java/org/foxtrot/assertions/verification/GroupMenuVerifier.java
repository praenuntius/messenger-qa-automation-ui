package org.foxtrot.assertions.verification;

import io.qameta.allure.Step;
import org.foxtrot.assertions.ToggleAssertions;
import org.foxtrot.pages.chat.right_sidebar_menu.primary_bar.elements.GroupMenu;

import java.time.Duration;

import static com.codeborne.selenide.Condition.not;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class GroupMenuVerifier {

    private final GroupMenu groupMenu = new GroupMenu();

    @Step("Проверить все элементы сайдбар-меню группы: имя={groupName}, тип={groupType})")
    public void verifyOwnerMenu(String groupName, String groupType) {
        verifySettingsButton()
                .verifyCloseSidebarMenuButton()
                .verifyNotificationToggleOn()
                .verifyAttachmentsButton()
                .verifyPinnedMessagesButton()
                .verifyInviteByLinkButton()
                .verifyMembersButton()
                .verifyAdminsButton()
                .verifyAutodeleteMessagesButton()
                .verifyTransferOwnershipButton()
                .verifyClearHistoryButton()
                .verifyDeleteGroupButton()
                .verifyLeaveGroupButton()
                .verifyGroupName(groupName)
                .verifyGroupType(groupType);
    }

    @Step("Проверить меню для роли Admin: имя={groupName}, тип={groupType}")
    public void verifyAdminMenu(String groupName, String groupType) {
        verifySettingsButton()
                .verifyCloseSidebarMenuButton()
                .verifyNotificationToggleOn()
                .verifyAttachmentsButton()
                .verifyPinnedMessagesButton()
                .verifyAppearanceButton()
                .verifyInviteByLinkButton()
                .verifyMembersButton()
                .verifyLeaveGroupButton()
                .verifyGroupName(groupName)
                .verifyGroupType(groupType);
    }

    @Step("Проверить меню для роли Participant: имя={groupName}, тип={groupType}")
    public void verifyParticipantMenu(String groupName, String groupType) {
        verifyCloseSidebarMenuButton()
                .verifyNotificationToggleOn()
                .verifyAttachmentsButton()
                .verifyPinnedMessagesButton()
                .verifyAppearanceButton()
                .verifyGroupName(groupName)
                .verifyGroupType(groupType);
    }

    @Step("Проверить меню для роли Non-Participant: имя={groupName}, тип={groupType}")
    public GroupMenuVerifier verifyNonParticipantMenu(String groupName, String groupType) {
        verifyCloseSidebarMenuButton()
                .verifyAttachmentsButton()
                .verifyPinnedMessagesButton()
                .verifyInviteByLinkButton()
                .verifyGroupName(groupName)
                .verifyGroupType(groupType);
        return this;
    }

    @Step("Проверить имя группы: {expectedName}")
    public GroupMenuVerifier verifyGroupName(String expectedName) {
        $(groupMenu.getGroupName())
                .shouldBe(visible, Duration.ofSeconds(5))
                .shouldHave(text(expectedName));
        return this;
    }

    @Step("Проверить тип группы: {expectedType}")
    public GroupMenuVerifier verifyGroupType(String expectedType) {
        $(groupMenu.getGroupType())
                .shouldBe(visible, Duration.ofSeconds(5))
                .shouldHave(text(expectedType));
        return this;
    }

    @Step("Проверить наличие кнопки [X] (Закрыть сайдбар-меню группы)")
    public GroupMenuVerifier verifyCloseSidebarMenuButton() {
        $(groupMenu.getMenuCloseButton())
                .shouldBe(visible, Duration.ofSeconds(5));
        return this;
    }

    @Step("Проверить наличие кнопки 'Настройки'")
    public GroupMenuVerifier verifySettingsButton() {
        $(groupMenu.getOpenMenuButton())
                .shouldBe(visible, Duration.ofSeconds(5));
        return this;
    }

    @Step("Проверить отсутствие кнопки 'Настройки'")
    public GroupMenuVerifier verifySettingsButtonAbsent() {
        $(groupMenu.getOpenMenuButton())
                .shouldBe(not(visible), Duration.ofSeconds(5));
        return this;
    }

    @Step("Проверить наличие тоггла 'Уведомления: ВКЛ'")
    public GroupMenuVerifier verifyNotificationToggleOn() {
        ToggleAssertions.assertChecked(groupMenu.getNotificationsOnToggle(), true, Duration.ofSeconds(5), false);
        return this;
    }

    @Step("Проверить наличие тоггла 'Уведомления: ВЫКЛ'")
    public GroupMenuVerifier verifyNotificationToggleOff() {
        $(groupMenu.getNotificationsOffToggle())
                .shouldBe(visible, Duration.ofSeconds(5));
        return this;
    }

    @Step("Проверить наличие кнопки 'Вложения'")
    public GroupMenuVerifier verifyAttachmentsButton() {
        $(groupMenu.getAttachmentsButton())
                .shouldBe(visible, Duration.ofSeconds(5));
        return this;
    }

    @Step("Проверить наличие кнопки 'Закрепленные сообщения'")
    public GroupMenuVerifier verifyPinnedMessagesButton() {
        $(groupMenu.getPinnedMessagesButton())
                .shouldBe(visible, Duration.ofSeconds(5));
        return this;
    }

    @Step("Проверить наличие кнопки 'Оформление чата'")
    public GroupMenuVerifier verifyAppearanceButton() {
        $(groupMenu.getAppearanceButton())
                .shouldBe(visible, Duration.ofSeconds(5));
        return this;
    }

    @Step("Проверить наличие кнопки 'Пригласить по ссылке'")
    public GroupMenuVerifier verifyInviteByLinkButton() {
        $(groupMenu.getInviteButton())
                .shouldBe(visible, Duration.ofSeconds(5));
        return this;
    }

    @Step("Проверить наличие кнопки 'Участники'")
    public GroupMenuVerifier verifyMembersButton() {
        $(groupMenu.getMembersButton())
                .shouldBe(visible, Duration.ofSeconds(5));
        return this;
    }

    @Step("Проверить наличие кнопки 'Администраторы'")
    public GroupMenuVerifier verifyAdminsButton() {
        $(groupMenu.getAdminsButton())
                .shouldBe(visible, Duration.ofSeconds(5));
        return this;
    }

    @Step("Проверить наличие кнопки 'Автоудаление сообщений'")
    public GroupMenuVerifier verifyAutodeleteMessagesButton() {
        $(groupMenu.getOpenAutodeleteButton())
                .shouldBe(visible, Duration.ofSeconds(5));
        return this;
    }

    @Step("Проверить наличие кнопки 'Передать права Владельца'")
    public GroupMenuVerifier verifyTransferOwnershipButton() {
        $(groupMenu.getTransferOwnershipButton())
                .shouldBe(visible, Duration.ofSeconds(5));
        return this;
    }

    @Step("Проверить наличие кнопки 'Очистить историю сообщений'")
    public GroupMenuVerifier verifyClearHistoryButton() {
        $(groupMenu.getClearHistoryButton())
                .shouldBe(visible, Duration.ofSeconds(5));
        return this;
    }

    @Step("Проверить наличие кнопки 'Удалить группу'")
    public GroupMenuVerifier verifyDeleteGroupButton() {
        $(groupMenu.getDeleteGroupButton())
                .shouldBe(visible, Duration.ofSeconds(5));
        return this;
    }

    @Step("Проверить наличие кнопки 'Покинуть группу'")
    public GroupMenuVerifier verifyLeaveGroupButton() {
        $(groupMenu.getLeaveButton())
                .shouldBe(visible, Duration.ofSeconds(5));
        return this;
    }
}