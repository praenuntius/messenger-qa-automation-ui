package org.foxtrot.pages.chat.modals.elements;

import lombok.Getter;
import org.openqa.selenium.By;

@Getter
public class SendButtonContextMenu {
    private final By sendUnderSpoiler = By.xpath("//span[text() = 'Отправить под спойлером']");
    private final By deferSending = By.xpath("//span[text() = 'Отложить отправку']");
}
