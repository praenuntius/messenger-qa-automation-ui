package org.foxtrot.pages.chat.right_sidebar_menu.primary_bar.elements;

import lombok.Getter;
import org.openqa.selenium.By;

import static org.openqa.selenium.By.cssSelector;
import static org.openqa.selenium.By.xpath;

@Getter
public class GroupMenu {

    private final By menuCloseButton = cssSelector("aside[class*='InfoSideBarWrapper'] button");
    private final By notificationsOnToggle = cssSelector("[data-cy = 'sidebarGroupSettingsNotificationsOn']");
    private final By notificationsOffToggle = cssSelector("[data-cy='sidebarGroupSettingsNotificationsOff']");
    private final By attachmentsButton = cssSelector("[data-cy='sidebarGroupSettingsAttachments']");
    private final By pinnedMessagesButton = cssSelector("[data-cy='sidebarGroupSettingsPinnedMessages']");
    private final By appearanceButton = xpath("//div[text() = 'Оформление чата']");
    private final By inviteButton = cssSelector("[data-cy='sidebarGroupSettingsInvite']");
    private final By membersButton = cssSelector("[data-cy='sidebarGroupSettingsMembers']");
    private final By adminsButton = cssSelector("[data-cy='sidebarGroupSettingsAdmins']");
    private final By openAutodeleteButton = cssSelector("[data-cy='sidebarGroupSettingsOpenAutodelete']");
    private final By transferOwnershipButton = cssSelector("[data-cy='sidebarGroupSettingsTransferOwnership']");
    private final By openMenuButton = cssSelector("[data-cy='sidebarOpenSettingsButton']");
    private final By clearHistoryButton = cssSelector("[data-cy='sidebarGroupSettingsClearHistory']");
    private final By deleteGroupButton = cssSelector("[data-cy='sidebarGroupSettingsDelete']");
    private final By leaveButton = cssSelector("[data-cy='sidebarGroupSettingsLogout']");
    private final By groupName = cssSelector("[data-cy='groupName']");
    private final By groupType = cssSelector("[data-cy='groupType']");
    private final By headerAvatar = cssSelector("div[class*='SideBar'] [class*='ImageLayer']");
    private final By headerInfoBlock = cssSelector("div[class*='HeaderBlock']");
}
