package org.foxtrot.pages.chat.right_sidebar_menu.primary_bar.elements;

import lombok.Getter;
import org.openqa.selenium.By;

import static org.openqa.selenium.By.cssSelector;
import static org.openqa.selenium.By.xpath;

@Getter
public class ChannelMenu {

    private final By sidebarOpenSettingsButton = cssSelector("[data-cy='sidebarOpenSettingsButton']");
    private final By channelName = cssSelector("[data-cy='channelName']");
    private final By channelType = cssSelector("[data-cy='channelType']");
    private final By sidebarCloseMenuButton = cssSelector("button[class *= 'CloseButtonWrapper']");
    private final By notificationsUnit = cssSelector("[data-cy='sidebarChannelSettingsNotifications']");
    private final By notificationsOnToggle = cssSelector("[data-cy='sidebarChannelSettingsNotificationsOn']");
    private final By notificationsOffToggle = cssSelector("[data-cy='sidebarChannelSettingsNotificationsOff']");
    private final By attachmentsButton = cssSelector("[data-cy='sidebarChannelSettingsAttachments']");
    private final By pinnedMessagesButton = cssSelector("[data-cy='sidebarChannelSettingsPinnedMessages']");
    private final By appearanceButton = cssSelector("[data-cy='sidebarChannelSettingsAppearance']");
    private final By inviteButton = cssSelector("[data-cy='sidebarChannelSettingsInvite']");
    private final By subscribersButton = cssSelector("[data-cy^='sidebarChannelSettingsSubscrib']");//Ждем фикс
    private final By transferOwnershipButton = cssSelector("[data-cy='sidebarChannelSettingsTransferOwnership']");
    private final By deleteChannelButton = cssSelector("[data-cy='sidebarDeleteButton']");
    private final By leaveChannelButton = cssSelector("[data-cy='sidebarChannelSettingsLeaveChannel']");
    private final By headerAvatar = cssSelector("div[class*='SideBar'] [class*='AvatarRoot'] [class*='ImageLayer']");
    private final By headerInfoBlock = cssSelector("div[class*='SideBar'] div[class*='Header']");
}