package org.foxtrot.mobile.screens.modals;

import org.foxtrot.mobile.core.driver.DriverManager;
import org.foxtrot.mobile.elements.Block;
import org.foxtrot.mobile.elements.Button;
import org.foxtrot.mobile.elements.TextView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.foxtrot.mobile.elements.base.BaseElement.*;

public class OnboardingScreen {

    private static final Logger log = LoggerFactory.getLogger(OnboardingScreen.class);

    /** Старый popup (homespace / read info). */
    private final Button popupReadInfoBtn = new Button(
            byResourceId("tvPopupReadInfoSuccess"),
            byAccessibilityId("InfoBubble.okButton")
    );

    /**
     * Онбординг меток/доски после создания группы (foxtrot stage14):
     * resource-id = tvGroupReadInfoSuccess, текст «Понятно».
     */
    private final Button groupReadInfoBtn = new Button(
            byResourceId("tvGroupReadInfoSuccess"),
            byAccessibilityId("InfoBubble.okButton")
    );

    /** Fallback без привязки к resource-id. */
    private final Button byTextBtn = new Button(
            byEqualsText("Понятно")
    );

    /**
     * Тултип записи голоса/видео. Appium видит только это popup-окно,
     * из-за него «не находится» экран чата и кнопка «Понятно».
     */
    private final Block voiceTooltipPopup = new Block(
            byResourceId("popup_content")
    );

    /**
     * Контекстное меню ячейки чата (iOS long-press / Selenide click по title).
     */
    private final TextView chatContextMenu = new TextView(
            byEqualsText("Отключить уведомления")
    );

    public boolean dismissOverlays() {
        boolean dismissed = false;
        for (int i = 0; i < 4; i++) {
            if (voiceTooltipPopup.isDisplayed()) {
                if (dismissVoiceTooltip()) {
                    dismissed = true;
                    continue;
                }
            }
            if (chatContextMenu.isDisplayed()) {
                if (dismissVoiceTooltip()) {
                    dismissed = true;
                    continue;
                }
            }
            if (clickOnboardingIfVisible()) {
                dismissed = true;
                continue;
            }
            break;
        }
        return dismissed;
    }

    public void clickOnboardingActionBtn() {
        if (!clickOnboardingIfVisible()) {
            byTextBtn.click();
        }
    }

    private boolean clickOnboardingIfVisible() {
        if (groupReadInfoBtn.isDisplayed()) {
            groupReadInfoBtn.click();
            return true;
        }
        if (popupReadInfoBtn.isDisplayed()) {
            popupReadInfoBtn.click();
            return true;
        }
        if (byTextBtn.isDisplayed()) {
            byTextBtn.click();
            return true;
        }
        return false;
    }

    private boolean dismissVoiceTooltip() {
        try {
            DriverManager.getDriver().navigate().back();
            log.info("Закрыл overlay через Back");
        } catch (Exception e) {
            log.warn("Не удалось закрыть overlay через Back: {}", e.getMessage());
        }
        if (!chatContextMenu.isDisplayed() && !voiceTooltipPopup.isDisplayed()) {
            return true;
        }
        try {
            var size = DriverManager.getDriver().manage().window().getSize();
            int x = size.getWidth() / 2;
            int y = Math.max(40, size.getHeight() / 10);
            org.openqa.selenium.interactions.PointerInput finger =
                    new org.openqa.selenium.interactions.PointerInput(
                            org.openqa.selenium.interactions.PointerInput.Kind.TOUCH, "finger");
            org.openqa.selenium.interactions.Sequence sequence =
                    new org.openqa.selenium.interactions.Sequence(finger, 1);
            sequence.addAction(finger.createPointerMove(java.time.Duration.ZERO,
                    org.openqa.selenium.interactions.PointerInput.Origin.viewport(), x, y));
            sequence.addAction(finger.createPointerDown(
                    org.openqa.selenium.interactions.PointerInput.MouseButton.LEFT.asArg()));
            sequence.addAction(new org.openqa.selenium.interactions.Pause(finger, java.time.Duration.ofMillis(30)));
            sequence.addAction(finger.createPointerUp(
                    org.openqa.selenium.interactions.PointerInput.MouseButton.LEFT.asArg()));
            DriverManager.getDriver().perform(java.util.List.of(sequence));
            log.info("Закрыл overlay тапом вне меню");
            return true;
        } catch (Exception e) {
            log.warn("Не удалось закрыть тултип голоса: {}", e.getMessage());
            return false;
        }
    }
}
