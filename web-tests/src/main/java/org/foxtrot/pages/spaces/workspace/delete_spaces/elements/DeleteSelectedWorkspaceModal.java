package org.foxtrot.pages.spaces.workspace.delete_spaces.elements;

import lombok.Getter;
import org.openqa.selenium.By;

import static org.openqa.selenium.By.cssSelector;

@Getter
public class DeleteSelectedWorkspaceModal {
    private final By backButtonSelector = cssSelector("[data-cy='selectedWorkspaceBackButton']");
    private final By closeButtonSelector = cssSelector("[data-cy='selectedWorkspaceCloseButton']");
    private final By confirmCheckboxSelector = cssSelector("[data-cy='selectedWorkspaceConfirmButton']");
    private final By deleteButtonSelector = cssSelector("[data-cy='selectedWorkspaceDeleteButton']");
    private final By headerTitleSelector = cssSelector("div[class*='HeaderTitleWrapper']");
    private final By confirmCheckboxNativeInput = cssSelector("[data-cy='selectedWorkspaceConfirmButton'] input");
}
