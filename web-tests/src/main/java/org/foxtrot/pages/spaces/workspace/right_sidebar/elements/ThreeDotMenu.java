package org.foxtrot.pages.spaces.workspace.right_sidebar.elements;

import lombok.Getter;
import org.openqa.selenium.By;

import static org.openqa.selenium.By.cssSelector;
import static org.openqa.selenium.By.xpath;

@Getter
public class ThreeDotMenu {

    private final By makeAdminButton = cssSelector("[data-cy='menuItemMakeAdmin']");
    private final By removeFromAdminsButton = cssSelector("[data-cy='menuItemRemoveAdmin']");
    private final By deleteFromSpaceButton = cssSelector("[data-cy='menuItemRemoveUserFromWorkspace']");
}
