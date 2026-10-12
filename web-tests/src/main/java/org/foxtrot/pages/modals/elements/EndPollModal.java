package org.foxtrot.pages.modals.elements;

import lombok.Getter;
import org.openqa.selenium.By;
import static org.openqa.selenium.By.cssSelector;

@Getter
public class EndPollModal {
    private final By closeModalButton = By.xpath("//*[@id=\"dialog-root\"]/div/div/div/div[2]/section/div[1]/button");
    private final By cancelButton = By.xpath("//*[@id=\"dialog-root\"]/div/div/div/div[2]/section/div[3]/button[1]");
    private final By finishPollButton = By.xpath("//*[@id='dialog-root']//button[.//span[contains(., 'Завершить')]]");
}
