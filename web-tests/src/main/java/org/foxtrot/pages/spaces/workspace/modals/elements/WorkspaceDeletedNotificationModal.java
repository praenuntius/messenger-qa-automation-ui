package org.foxtrot.pages.spaces.workspace.modals.elements;

import lombok.Getter;
import org.openqa.selenium.By;

@Getter
public class WorkspaceDeletedNotificationModal {

    private final By workspaceDeletedConfirmButton = By.xpath(
            "//*[@data-cy='dialogOverlay']//span[normalize-space()='Понятно']"
                    + " | //div[contains(@class,'DialogContent')]//span[normalize-space()='Понятно']");
    private final By workspaceDeletedTitle = By.xpath(
            "//span[contains(normalize-space(.), 'Все чаты и файлы в пространстве были удалены')]"
                    + " | //*[contains(@aria-label,'было удалено')]");

}
