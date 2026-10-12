package org.foxtrot.pages.spaces.workspace.delete_spaces.elements;

import lombok.Getter;
import org.openqa.selenium.By;

import static org.openqa.selenium.By.cssSelector;

@Getter
public class DeleteSpaceModal {
    private final By BackButtonSelector = cssSelector("[data-cy='selectWorkspaceBackButton']");
    private final By CloseButtonSelector = cssSelector("[data-cy='selectWorkspaceCloseButton']");
    private final By ListItemButtonSelector = cssSelector("[data-cy='selectWorkspaceListItem']"); //workspaceid in the spacelist
    private final By DeleteButtonSelector = cssSelector("[data-cy='selectWorkspaceDeleteButton']");
    private final By workspaceItem = cssSelector("div[class*='WorkspaceItemWith']");
    private final By selectedWorkspacesCounter = cssSelector("div[class*='WorkspacesToDeleteLength']");
    private final By selectedCheckIcon = cssSelector("svg[class*='CheckIcon']");
}
