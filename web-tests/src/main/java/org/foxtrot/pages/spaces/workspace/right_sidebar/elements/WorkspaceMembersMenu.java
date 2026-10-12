package org.foxtrot.pages.spaces.workspace.right_sidebar.elements;

import lombok.Getter;
import org.openqa.selenium.By;

import static org.openqa.selenium.By.cssSelector;

@Getter
public class WorkspaceMembersMenu {

    private final By closeWorkspaceMembersButton = cssSelector("[data-cy='searchPanelCloseButton']");
    private final By searchInputWorkspaceMembers = cssSelector("[data-cy='sidebarSearchInput']");
    private final By resetSearchWorkspaceMembersButton = cssSelector("[data-cy='sidebarSearchClearButton']");
    private final By inviteToWorkspaceButton = cssSelector("[data-cy='inviteToWorkspaceAction']");
    private final By workspaceMembersCounter = cssSelector("[data-cy='workspaceMembersCount']"); //{countvalue}
    private final By workspaceParticipantCard = cssSelector("[data-cy='participant']"); //participant{id}
    private final By workspaceParticipantContextMenuButton = cssSelector("[data-cy='participantMenu']"); //{participant_id_Menu}

    private final String DefinedThreeDotMenuButton = ("//div[contains(@class,'VerticalScroll')]//div[contains(@class,'Wrapper')][.//div[text()='{username}']]//div[contains(@class,'SettingsMenuWrapper')]");
    private final String DefinedUserButton = ("//div[contains(@class, 'ListWrapper')]//div[text() = '{username}']");

    private final String DefinedUserWithNameAndAvatar = ("//div[contains(@class, 'VerticalScroll')]//div[.//div[contains(@style, 'background')] and .//div[text()='{username}']]");
    private final String DefinedUserWithAvatarPlaceholder = ("//div[contains(@class, 'VerticalScroll')]//div[.//*[contains(@fill, 'rgba')] and .//div[text()='{username}']]");
}
