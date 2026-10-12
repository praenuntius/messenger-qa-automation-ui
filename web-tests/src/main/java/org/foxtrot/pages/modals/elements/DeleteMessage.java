package org.foxtrot.pages.modals.elements;

import lombok.Getter;
import org.openqa.selenium.By;

@Getter
public class DeleteMessage {
    private final By modalContainer = By.xpath("div[class*='ModalHeaderWithContentWrapper']");
    private final By outsideModalContainer = By.xpath("//*[@id=\"dialog-root\"]/div/div/div/div[1]");
    private final By closeModalButton = By.xpath("div[class*='ModalHeader'] button[class*='Button']");
    private final By cancelButton = By.xpath("//span[text() = 'Отмена']");
    private final By deleteButton = By.xpath("//span[text() = 'Удалить']");
    private final By cannotDeleteMessageLabel = By.cssSelector("[aria-label='Вы не можете удалять чужие сообщения']");
}
