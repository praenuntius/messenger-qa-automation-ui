package org.foxtrot.pages.modals.elements;

import lombok.Getter;
import org.openqa.selenium.By;

import static org.openqa.selenium.By.cssSelector;

@Getter
public class CropPhoto {

    private final By saveButton = cssSelector("[data-cy='photoUploadButton']");
    private final By cancelButton = By.xpath("//span[text() = 'Отмена']");
}
