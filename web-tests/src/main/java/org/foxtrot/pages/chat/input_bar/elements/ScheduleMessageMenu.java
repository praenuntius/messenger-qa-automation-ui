package org.foxtrot.pages.chat.input_bar.elements;

import com.codeborne.selenide.SelenideElement;
import lombok.Getter;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$$;
import static org.openqa.selenium.By.cssSelector;

@Getter
public class ScheduleMessageMenu {
    private final By dataInput = cssSelector("div[class*='DateWrapper']");
    private final By backButton = cssSelector("[data-cy='channelCommentsBackButton']");
    private final By containerWithMessages = cssSelector("div[class*='ChatMessagesContainer']");

    public SelenideElement getHoursInput() {
        return $$(cssSelector("div[class*='SelectContainer']")).get(0);
    }

    public SelenideElement getMinutesInput() {
        return $$(cssSelector("div[class*='SelectContainer']")).get(1);
    }

    public By getMinuteOption(int minute) {
        String minuteText = String.format("%02d", minute);
        return By.xpath("//*[@data-side='top']//div[text()='" + minuteText + "']");
    }

    public By getSelectedMinuteDisplay() {
        return By.xpath("(//div[contains(@class, 'SelectContainer')])[2]//span");
    }

    public By getMessageBubbleById(String messageId) {
        return By.cssSelector("[id='message-" + messageId + "']");
    }
}
