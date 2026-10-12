package org.foxtrot.pages.spaces.workspace.left_sidebar.elements;

import lombok.Getter;
import org.openqa.selenium.By;

import static org.openqa.selenium.By.cssSelector;

@Getter
public class SpaceMenu {

    private final By spaceName = cssSelector("[data-cy='workspaceName']");
    private final By spaceMenuBackButton = cssSelector("div[class*='BackButton'] button[class*='IconButton']");
    private final By workspaceInviteUserMenuButton = cssSelector("[data-cy='workspaceInviteUser']");
    private final By workspaceCreateGroupMenuButton = cssSelector("[data-cy='workspaceCreateGroup']");
    private final By workspaceCreateChannelMenuButton = cssSelector("[data-cy='workspaceCreateChannel']");
    private final By workspaceNotificationsOnToggle = cssSelector("[data-cy='workspaceNotificationsOn']");
    private final By workspaceNotificationsOffToggle = cssSelector("[data-cy='workspaceNotificationsOff']");
    private final By workspaceMembersMenuButton = cssSelector("[data-cy='workspaceMembers']");
    private final By workspaceGroupsAndChannelsMenuButton = cssSelector("[data-cy='workspaceGroupsAndChannels']");
    private final By workspaceEditMenuButton = cssSelector("[data-cy='workspaceEdit']");
    private final By workspaceLogoutButton = cssSelector("[data-cy='workspaceLogout']");
    private final By workspaceDeleteButton = cssSelector("[data-cy='workspaceDelete']");
    private final By headerAvatar = cssSelector("div[class*='Header'] [class*='ImageLayer']");
    private final By headerBlock = cssSelector("div[class*='SettingsHeaderWrapper']");

}
