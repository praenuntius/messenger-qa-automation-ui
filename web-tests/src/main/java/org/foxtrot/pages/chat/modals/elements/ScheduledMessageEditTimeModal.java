package org.foxtrot.pages.chat.modals.elements;

import lombok.Getter;
import org.openqa.selenium.By;

@Getter
public class ScheduledMessageEditTimeModal {
    private final By inputDate = By.id("date");
    private final By todayButton = By.cssSelector("td[class*='today']");
    private final By applyButton = By.xpath("//span[text() = 'Применить']");
    private final By cancelButton = By.xpath("//span[text() = 'Отмена']");
    private final By previousMonthButton = By.cssSelector("[aria-label='Go to the Previous Month']");

}
