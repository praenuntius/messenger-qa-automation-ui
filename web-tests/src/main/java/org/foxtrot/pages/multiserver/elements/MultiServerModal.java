package org.foxtrot.pages.multiserver.elements;

import lombok.Getter;
import org.openqa.selenium.By;

import static org.openqa.selenium.By.cssSelector;

@Getter
public class MultiServerModal {

    private final By changeServerInputFieldSelector = cssSelector("[data-cy='changeServerInput']");
    private final By changeServerClearButtonSelector = cssSelector("[data-cy='changeServerClearButton']");
    private final By changeServerSaveButtonSelector = cssSelector("[data-cy='changeServerSaveButton']");
    private final By changeServerCancelButtonSelector = cssSelector("[data-cy='changeServerCancelButton']");
}
