package org.foxtrot.pages.spaces.workspace.modals.elements;


import lombok.Getter;
import org.openqa.selenium.By;

import static org.openqa.selenium.By.cssSelector;
import static org.openqa.selenium.By.xpath;

@Getter
public class NewWorkspaceModal {

    private final By closeModalButton = cssSelector("[data-cy='createWorkspaceCloseButton']");
    private final By uploadAvatarButton = cssSelector("[data-cy='createWorkspaceUploadAvatar']");
    private final By uploadButton = cssSelector("[data-cy='createWorkspaceUploadButton']");
    private final By backButton = cssSelector("[data-cy='createWorkspaceBackButton']");
    private final By newSpaceTitleInput = cssSelector("[data-cy='newSpaceTitleInput']");
    private final By newSpaceDescriptionInput = cssSelector("[data-cy='newSpaceDescriptionInput']");
    private final By newSpaceCreateButton = xpath("//span[text() = 'Создать']");
    private final By fileInput = cssSelector("input[type='file']");
    private final By requiredFieldError = By.xpath("//span[text() = 'Обязательное поле не может быть пустым']");
}
