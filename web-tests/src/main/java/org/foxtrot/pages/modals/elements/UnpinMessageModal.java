package org.foxtrot.pages.modals.elements;


import lombok.Getter;
import org.openqa.selenium.By;

@Getter
public class UnpinMessageModal {
    private final By modalContainer = By.xpath("//*[@id='dialog-root']//section[.//button[contains(., 'Открепить')]]");
    private final By outsideModalContainer = By.xpath("//*[@id='dialog-root']/div/div[1]");
    private final By cancelButton = By.xpath("//*[@id='dialog-root']//button[contains(., 'Отмена')]");
    private final By unpinButton = By.xpath("//*[@id='dialog-root']//button[contains(., 'Открепить')]");
}

