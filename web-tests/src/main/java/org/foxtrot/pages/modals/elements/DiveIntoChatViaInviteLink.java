package org.foxtrot.pages.modals.elements;

import lombok.Getter;
import org.openqa.selenium.By;

@Getter
public class DiveIntoChatViaInviteLink {

    private final By closeModalButton = By.xpath("//div[span[contains(text(), 'Приглашение')]]//button[contains(@class, 'IconButton')]");
    private final By joinGroupButton = By.xpath("//button[@data-cy='joinToGroupButton']");
}
