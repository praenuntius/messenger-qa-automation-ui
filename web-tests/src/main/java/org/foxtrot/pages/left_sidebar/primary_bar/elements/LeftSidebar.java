package org.foxtrot.pages.left_sidebar.primary_bar.elements;

import lombok.Getter;
import org.openqa.selenium.By;

import static org.openqa.selenium.By.cssSelector;

@Getter
public class LeftSidebar {
    private final By VideoConferenceButton = cssSelector("[data-cy='sidebarVideoConferenceButton']");
    private final By WorkspaceChatsButton = cssSelector("[data-cy='sidebarWorkspaceButtonЧаты']");
    private final By EnterOrCreateSpaceButton = cssSelector("[data-cy='enterOrCreateSpaceButton']");
    private final By OpenSettingsButton = cssSelector("[data-cy='openSettingsButton']");
    private final By OpenProfileMenuButton = cssSelector("[data-cy='openProfileMenu']");
    private final By helpButton = cssSelector("[data-cy='openHelpButton']");

    public By getDefinedSpaceButton(String spaceName) {
        return By.cssSelector(String.format("div[data-cy='sidebarWorkspaceButton%s']", spaceName));
    }

    public By getActiveWorkspaceIndicator(String spaceName) {
        return By.cssSelector(
                String.format("div[class*='WorkspacesListItem']:has([data-cy*='%s']):has(div[class*='ActionItem'] svg)", spaceName)
        );
    }

    public By getNewMessagesBadgeOnP2p(Long userId) {
        return By.cssSelector(
                String.format(":has(> [data-cy='sidebarP2PUnreadUserAvatar%d']) [class*='NewMessagesBadge']", userId));
    }

    public By getNewMessagesBadgeOnSpace(String chatName){
        return By.cssSelector(
                String.format("div[data-cy='sidebarWorkspaceButton%s'] div[class*='NewMessagesBadge']", chatName));
    }
}
