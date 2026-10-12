package org.foxtrot.pages.modals.elements;

import lombok.Getter;
import org.openqa.selenium.By;

@Getter
public class DiveIntoWorkSpaceViaUrlLink {

    private final By closeModalButton = By.xpath("//span[text()='Закрыть']");
    private final By noAccessMessage = By.xpath("//span[contains(., 'доступ')]");
}
