package org.foxtrot.pages.spaces.homespace.modals.elements;

import lombok.Getter;
import org.openqa.selenium.By;

@Getter
public class DeleteChatModal {

    private final By deleteButton = By.xpath("//span[text() = 'Удалить']");
    private final By cancelButton = By.xpath("//span[text() = 'Отмена']");
}
