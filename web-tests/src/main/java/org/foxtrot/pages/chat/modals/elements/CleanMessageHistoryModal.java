package org.foxtrot.pages.chat.modals.elements;

import lombok.Getter;
import org.openqa.selenium.By;

@Getter
public class CleanMessageHistoryModal {

    private final By cleanHistoryModal = By.cssSelector("section[class*='ModalBody']");
    private final By clearHistoryForAllButton  = By.xpath("//span[text() = 'Очистить у всех']");
    private final By chatClearForSelfButton = By.xpath("//span[text() ='Очистить только у себя']");
    private final By clearHistoryForParticipantButton = By.xpath("//span[text() ='Очистить']");
}
