package org.foxtrot.pages.chat.forward.elements;

import lombok.Getter;
import org.openqa.selenium.By;

import static org.openqa.selenium.By.cssSelector;

@Getter
public class AvailableWorkspacesDropDownList {
    private final By openWorkspaceDropDownList = cssSelector("[data-cy='workspaceSelect']");

    public By getAvailableWorkspace(String workspaceName) {
        return cssSelector("[data-cy='select{" + workspaceName + "}']");
    }
}
