package org.foxtrot.pages.modals.elements;

import lombok.Getter;
import org.openqa.selenium.By;

@Getter
public class DeletePhotoModal {

    private final By deleteButton = By.xpath("//span[text() = 'Удалить']");
    private final By cancelButton = By.xpath("//span[text() = 'Отмена']");
}
