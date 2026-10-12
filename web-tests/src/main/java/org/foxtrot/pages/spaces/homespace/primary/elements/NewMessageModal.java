package org.foxtrot.pages.spaces.homespace.primary.elements;

import lombok.Getter;
import org.openqa.selenium.By;

import static org.openqa.selenium.By.cssSelector;
import static org.openqa.selenium.By.xpath;

@Getter
public class NewMessageModal {

    private final By closeModalButton = cssSelector("[data-cy='homeSpaceCloseButton']");
    //***data-cy пропали
    private final By createNewGroupButton = xpath("//span[text() = 'Создать группу']");
    private final By createNewChannelButton = xpath("//span[text() = 'Создать канал']");
    private final By inviteNewUserButton = cssSelector("[data-cy='homeSpaceInviteNewUser']");
    private final By searchUserInputField = cssSelector("[data-cy='homeSpaceSearchUserInput']");
}
