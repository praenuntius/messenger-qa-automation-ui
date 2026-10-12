package org.foxtrot.pages.modals.elements;

import lombok.Getter;
import org.openqa.selenium.By;

@Getter
public class GroupUnavailableModal {
    private final By modalContainer = By.cssSelector("[aria-label='Группа (канал) не существует']");
    private final By closeButton = By.xpath("//span[text() = 'Закрыть']");
}
