package org.foxtrot.pages.spaces.workspace.modals.elements;

import lombok.Getter;
import org.openqa.selenium.By;

import static org.openqa.selenium.By.cssSelector;

@Getter
public class InviteMembersToWorkspaceModal {

    private final By workspaceInviteUserModalCloseButton = cssSelector("[data-cy='workspaceInviteUserModalCloseButton']");
    private final By workspaceInviteUserTabContactButton = cssSelector("[data-cy='workspaceInviteUserTabContact']");
    private final By workspaceInviteUserTabContactInviteButton = cssSelector("[data-cy='inviteByContactModalFormSubmitButton']");
    private final By workspaceInviteUserTabLinkButton = cssSelector("[data-cy='workspaceInviteUserTabLink']");
    private final By workspaceInviteUserTabLinkCopyButton = cssSelector("[data-cy='workspaceInviteUserTabLinkCopy']");
    private final By workspaceInviteUserTabLinkGenerateButton = cssSelector("[data-cy='workspaceInviteUserTabLinkGenerate']");
    private final By workspaceInviteUserTabEmailButton = cssSelector("[data-cy='workspaceInviteUserTabEmail']");
    private final By workspaceInviteUserTabEmailInputField = cssSelector("[data-cy='inviteByEmailInput']");
    private final By workspaceInviteUserTabEmailInviteButton = cssSelector("[data-cy='inviteModalFormSubmitButton']");
    private final By tabContactInputField = cssSelector("[data-cy='homeSpaceSearchUserInput']");
    private final String tabContactDefinedUserButton = ("//div[contains(@class, 'VerticalScroll')]//div[text() = '{username}']");
    private final By contactCard = cssSelector("div[class*='PersonInfo']");
    private final By contactName = cssSelector("div[class*='PersonName']");
}
