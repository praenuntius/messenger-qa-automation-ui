package org.foxtrot.pages.modals.elements;

import lombok.Getter;
import org.openqa.selenium.By;

import static org.openqa.selenium.By.cssSelector;

@Getter
public class AttachImagesModal {
    private final By attachImageModalCloseButtonSelector = cssSelector("[data-cy='attachmentModalCloseButton']");
    private final By attachImageModalUncompressedButtonSelector = cssSelector("[data-cy='attachmentModalUncompressedButton']");
    private final By attachImageModalCompressedButtonSelector = cssSelector("[data-cy='attachmentModalCompressedButton']");
    private final By attachImageModalSendButtonSelector = cssSelector("[data-cy='attachmentModalSendButton']");
    private final By attachImageModalDeleteItemButtonSelector = cssSelector("[data-cy='attachmentModalFileListItem{filename}-{fileid}}-compressed']"); //Спросить как это работает у dev
}
