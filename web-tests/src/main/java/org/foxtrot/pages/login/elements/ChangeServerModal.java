package org.foxtrot.pages.login.elements;

import lombok.Getter;
import org.openqa.selenium.By;

import static org.openqa.selenium.By.cssSelector;

@Getter
public class ChangeServerModal {

    private final By saveButtonSelector = cssSelector("[data-cy='changeServerSaveButton']");
    private final By cancelButtonSelector = cssSelector("[data-cy='changeServerCancelButton']");
    private final By inputFieldSelector = cssSelector("[data-cy='changeServerInput']");
    private final By clearFieldButtonSelector = cssSelector("[data-cy='changeServerClearButton']");
}
