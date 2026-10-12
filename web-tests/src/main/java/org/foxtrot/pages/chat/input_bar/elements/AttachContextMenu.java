package org.foxtrot.pages.chat.input_bar.elements;

import lombok.Getter;
import org.openqa.selenium.By;

import static org.openqa.selenium.By.cssSelector;
import static org.openqa.selenium.By.xpath;

@Getter
public class AttachContextMenu {
    private final By attachButton = xpath("//span[text() = 'Прикрепить вложения']");
    private final By shareContactsButton = xpath("//span[normalize-space(.)='Поделиться контактом']");
    private final By createPollButton = xpath("//span[text() = 'Создать опрос']");
    private final By fileInput = cssSelector("input[type='file']");
}
