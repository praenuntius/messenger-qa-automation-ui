package org.foxtrot.pages.spaces.workspace.modals.elements;

import lombok.Getter;
import org.openqa.selenium.By;

import static org.openqa.selenium.By.cssSelector;

@Getter
public class AvailableWorkspacesModal {

    private final By createNewWorkspaceButton = cssSelector("[data-cy='createNewWorkspaceButton']");
    private final By newSpaceTitleInput = cssSelector("[data-cy='newSpaceTitleInput']");
    private final By newSpaceDescriptionInput = cssSelector("[data-cy='newSpaceDescriptionInput']");
    private final By newSpaceCreateButton = cssSelector("[data-cy='newSpaceCreateButton']");
    private final By workspaceAvailableSpacesCloseButton = cssSelector("[data-cy='workspaceAvailableSpacesCloseButton']");
    private final By workspaceAvailableSpacesDeleteButton = cssSelector("[data-cy='workspaceAvailableSpacesDeleteButton']");
    private final By workspaceAvailableSpacesLeaveButton = cssSelector("[data-cy='workspaceAvailableSpacesLeaveButton']"); // spacename in the selector
    private final By workSpaceName = cssSelector("div[class*='WorkspaceItemInfo']");
    private final By workspaceNamesInList = cssSelector(
            "section[class*='ModalBody'] div[class*='VerticalScroll'] div[class*='WorkspaceItemInfo']"
    );

    public By getWorkspaceExitingButton(String value) {
        return By.cssSelector(String.format("[data-cy='workspaceAvailableSpacesLeaveButton%s']", value));
    }

    public By getWorkspaceLoginButtonByName(String value) {
        return By.cssSelector(String.format("[data-cy='workspaceAvailableSpacesJoinButton%s']", value));
    }
}
