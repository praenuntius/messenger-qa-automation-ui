package org.foxtrot.pages.modals.elements;

import lombok.Getter;
import org.openqa.selenium.By;

@Getter
public class DiveIntoChatViaUrlLink {

    private final By closeModalButton = By.xpath(
            "//span[normalize-space()='Закрыть' or normalize-space()='Понятно']");
    private final By noAccessMessage = By.xpath(
            "//span[contains(normalize-space(.), 'У вас нет доступа')]"
                    + " | //span[contains(normalize-space(.), 'Все чаты и файлы в пространстве были удалены')]"
                    + " | //*[contains(@aria-label,'было удалено')]");
}
