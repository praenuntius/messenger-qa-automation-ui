package org.foxtrot.pages.spaces.workspace.chats.elements;

import lombok.Getter;
import org.openqa.selenium.By;

import static org.openqa.selenium.By.cssSelector;

@Getter
public class Space {
    private final By participantsButton = cssSelector("[data-cy='workspaceParticipants']");
    private final By openSpaceMenuButton = cssSelector("div[class*='WorkspaceSettingsButton']");
    private final By openWorkspaceMainChat = cssSelector("[data-cy='openChatГлавный']");
    private final By publicChannelPrefix = cssSelector("[data-cy='WorkspacePublicChannel_");
    private final By privateChannelPrefix = cssSelector("[data-cy='WorkspacePrivateChannel_");
    private final By publicGroupPrefix = cssSelector("[data-cy='WorkspacePublicGroup_");
    private final By privateGroupPrefix = cssSelector("[data-cy='WorkspacePrivateGroup_");
    private final By spaceName = cssSelector("[data-cy='workspaceName']");
    private final By chatName = cssSelector("[data-cy='chatPreviewName']");
}
