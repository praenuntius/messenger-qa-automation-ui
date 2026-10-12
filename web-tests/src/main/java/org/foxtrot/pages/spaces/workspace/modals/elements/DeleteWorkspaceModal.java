package org.foxtrot.pages.spaces.workspace.modals.elements;

import lombok.Getter;
import org.openqa.selenium.By;

@Getter
public class DeleteWorkspaceModal {

    private final By deleteButton = By.cssSelector("[data-cy='deleteWorkspaceButton']");
    private final By cancelButton = By.cssSelector("[data-cy='deleteWorkspaceCancelButton']");
    private final By hardDeleteOptions = By.cssSelector("label[for = 'permanentDeletion']");
    private final By softDeleteOptions = By.cssSelector("label[for = 'archivation']");
}
