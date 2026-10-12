package org.foxtrot.assertions.verification;

import io.qameta.allure.Step;
import org.foxtrot.pages.chat.right_sidebar_menu.primary_bar.elements.ChannelMenu;

import java.time.Duration;

import static com.codeborne.selenide.Condition.not;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class ChannelMenuVerifier {

    private final ChannelMenu channelMenu = new ChannelMenu();

    @Step("Проверить меню владельца канала: имя={channelName}")
    public void verifyOwnerMenu(String channelName, String channelType) {
        verifySettingsButton()
                .verifyCloseSidebarMenuButton()
                .verifyNotificationToggleOn()
                .verifyAttachmentsButton()
                .verifyPinnedMessagesButton()
                .verifyAppearanceButton()
                .verifySubscribersButton()
                .verifyTransferOwnershipButton()
                .verifyDeleteChannelButton()
                .verifyChannelName(channelName)
                .verifyChannelType(channelType);

        if ("Открытый канал".equals(channelType)) {
            verifyCopyLinkButton();
        } else {
            verifyInviteByLinkButton();
        }
    }

    @Step("Проверить меню для роли Admin: имя={channelName}, тип={channelType}")
    public void verifyAdminMenu(String channelName, String channelType) {
        verifySettingsButton()
                .verifyCloseSidebarMenuButton()
                .verifyNotificationToggleOn()
                .verifyAttachmentsButton()
                .verifyPinnedMessagesButton()
                .verifyAppearanceButton()
                .verifySubscribersButton()
                .verifyLeaveChannelButton()
                .verifyChannelName(channelName)
                .verifyChannelType(channelType);

        if ("Открытый канал".equals(channelType)) {
            verifyCopyLinkButton();
        } else {
            verifyInviteByLinkButton();
        }
    }

    @Step("Проверить меню для роли Participant: имя={channelName}, тип={channelType}")
    public void verifyParticipantMenu(String channelName, String channelType) {
        verifyCloseSidebarMenuButton()
                .verifyNotificationToggleOn()
                .verifyAttachmentsButton()
                .verifyPinnedMessagesButton()
                .verifyAppearanceButton()
                .verifyChannelName(channelName)
                .verifyChannelType(channelType);
    }

    @Step("Проверить меню для роли Non-Participant: имя={channelName}, тип={channelType}")
    public ChannelMenuVerifier verifyNonParticipantMenu(String channelName, String channelType) {
        verifyCloseSidebarMenuButton()
                .verifyAttachmentsButton()
                .verifyPinnedMessagesButton()
                .verifyInviteByLinkButton()
                .verifyChannelName(channelName)
                .verifyChannelType(channelType);
        return this;
    }

    @Step("Проверить имя канала: {expectedName}")
    public ChannelMenuVerifier verifyChannelName(String expectedName) {
        $(channelMenu.getChannelName())
                .shouldBe(visible, Duration.ofSeconds(5))
                .shouldHave(text(expectedName));
        return this;
    }

    @Step("Проверить тип канала: {expectedType}")
    public ChannelMenuVerifier verifyChannelType(String expectedType) {
        $(channelMenu.getChannelType())
                .shouldBe(visible, Duration.ofSeconds(5))
                .shouldHave(text(expectedType));
        return this;
    }

    @Step("Проверить наличие кнопки [X] (Закрыть сайдбар-меню канала)")
    public ChannelMenuVerifier verifyCloseSidebarMenuButton() {
        $(channelMenu.getSidebarCloseMenuButton())
                .shouldBe(visible, Duration.ofSeconds(5));
        return this;
    }

    @Step("Проверить наличие кнопки 'Настройки'")
    public ChannelMenuVerifier verifySettingsButton() {
        $(channelMenu.getSidebarOpenSettingsButton())
                .shouldBe(visible, Duration.ofSeconds(5));
        return this;
    }

    @Step("Проверить отсутствие кнопки 'Настройки'")
    public ChannelMenuVerifier verifySettingsButtonAbsent() {
        $(channelMenu.getSidebarOpenSettingsButton())
                .shouldBe(not(visible), Duration.ofSeconds(5));
        return this;
    }

    @Step("Проверить наличие тоггла 'Уведомления: ВКЛ'")
    public ChannelMenuVerifier verifyNotificationToggleOn() {
        $(channelMenu.getNotificationsOnToggle())
                .isSelected();
        return this;
    }

    @Step("Проверить наличие тоггла 'Уведомления: ВЫКЛ'")
    public ChannelMenuVerifier verifyNotificationToggleOff() {
        $(channelMenu.getNotificationsOffToggle())
                .shouldBe(visible, Duration.ofSeconds(5));
        return this;
    }

    @Step("Проверить наличие кнопки 'Вложения'")
    public ChannelMenuVerifier verifyAttachmentsButton() {
        $(channelMenu.getAttachmentsButton())
                .shouldBe(visible, Duration.ofSeconds(5));
        return this;
    }

    @Step("Проверить наличие кнопки 'Закрепленные сообщения'")
    public ChannelMenuVerifier verifyPinnedMessagesButton() {
        $(channelMenu.getPinnedMessagesButton())
                .shouldBe(visible, Duration.ofSeconds(5));
        return this;
    }

    @Step("Проверить наличие кнопки 'Оформление чата'")
    public ChannelMenuVerifier verifyAppearanceButton() {
        $(channelMenu.getAppearanceButton())
                .shouldBe(visible, Duration.ofSeconds(5));
        return this;
    }

    @Step("Проверить наличие кнопки 'Пригласить по ссылке'")
    public ChannelMenuVerifier verifyInviteByLinkButton() {
        $(channelMenu.getInviteButton())
                .shouldBe(visible, Duration.ofSeconds(5));
        return this;
    }

    @Step("Проверить наличие кнопки 'Скопировать ссылку'")
    public ChannelMenuVerifier verifyCopyLinkButton() {
        $(channelMenu.getInviteButton())
                .shouldBe(visible, Duration.ofSeconds(5));
        return this;
    }

    @Step("Проверить наличие кнопки 'Подписчики'")
    public ChannelMenuVerifier verifySubscribersButton() {
        $(channelMenu.getSubscribersButton())
                .shouldBe(visible, Duration.ofSeconds(5));
        return this;
    }

    @Step("Проверить наличие кнопки 'Передать права Владельца'")
    public ChannelMenuVerifier verifyTransferOwnershipButton() {
        $(channelMenu.getTransferOwnershipButton())
                .shouldBe(visible, Duration.ofSeconds(5));
        return this;
    }

    @Step("Проверить наличие кнопки 'Удалить канал'")
    public ChannelMenuVerifier verifyDeleteChannelButton() {
        $(channelMenu.getDeleteChannelButton())
                .shouldBe(visible, Duration.ofSeconds(5));
        return this;
    }

    @Step("Проверить наличие кнопки 'Покинуть канал'")
    public ChannelMenuVerifier verifyLeaveChannelButton() {
        $(channelMenu.getLeaveChannelButton())
                .shouldBe(visible, Duration.ofSeconds(5));
        return this;
    }
}