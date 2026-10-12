package org.foxtrot.pages.modals.elements;

import lombok.Getter;
import org.openqa.selenium.By;

@Getter
public class JoinGroupModalElements {
    private final By joinGroupButton = By.cssSelector("[data-cy='joinToGroupButton']");
    private final By closeButton = By.cssSelector("section button[class*='IconButtonNone']");
}