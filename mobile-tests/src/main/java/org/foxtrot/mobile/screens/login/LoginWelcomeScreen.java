package org.foxtrot.mobile.screens.login;

import org.foxtrot.mobile.elements.Block;
import org.foxtrot.mobile.elements.Button;
import org.foxtrot.mobile.elements.TextBox;
import org.foxtrot.mobile.elements.TextView;
import org.foxtrot.mobile.elements.actions.Direction;

import static org.foxtrot.mobile.elements.base.BaseElement.byAccessibilityId;
import static org.foxtrot.mobile.elements.base.BaseElement.byAccessibilityIdAndText;
import static org.foxtrot.mobile.elements.base.BaseElement.byResourceId;
import static org.foxtrot.mobile.elements.base.BaseElement.byResourceIdAndText;

public class LoginWelcomeScreen {

    private final Button continueBtn = new Button(
            byResourceId("btnSendCode"),
            byAccessibilityId("Login.nextButton")
    );
    private final TextBox phoneInput = new TextBox(
            byResourceId("etInputField"),
            byAccessibilityId("Common.textField")
    );

    private TextView screenTitle(String text) {
        return new TextView(
                byResourceIdAndText("textTitle", text),
                byAccessibilityIdAndText("Login.greetingLabel", text)
        );
    }

    /** Welcome без привязки к бренду в тексте (Foxtrot/Tango). */
    private final TextView greetingLabel = new TextView(
            byResourceId("textTitle"),
            byAccessibilityId("Login.greetingLabel")
    );

    private final TextView serverConfigBtn = new TextView(
            byResourceId("serverSettingsIcon")
    );
    private final Button serverSwitcherBtn = new Button(byAccessibilityId("Login.serverChangeLabel"));
    private final Button serverOptionPreprodBtn = new Button(byAccessibilityId("Alert.preprod"));

    private Button serverOptionStageBtn(int stageNumber) {
        return new Button(byAccessibilityId("Alert.stage" + stageNumber));
    }

    private final Block serverChangeAlert = new Block(byAccessibilityId("Alert.container"));

    public void clickContinueBtn() {
        continueBtn.click();
    }

    public void clickOnceServerSwitcherBtn() {
        serverSwitcherBtn.click();
    }

    public void clickServerSwitcherBtn() {
        serverSwitcherBtn.doubleTapAndLongPress();
    }

    public void clickServerOptionPreprodBtn() {
        serverOptionPreprodBtn.click();
    }

    public void clickServerOptionStageBtn(int stageNumber) {
        serverOptionStageBtn(stageNumber).click();
    }

    public void clickServerConfigBtn() {
        serverConfigBtn.click();
    }

    public void swipeInOptionContainer(int stageNumber) {
        serverOptionStageBtn(stageNumber).swipe(Direction.UP);
    }

    public void fillPhone(String phone) {
        phoneInput.sendKeys(phone);
    }

    public boolean isWelcomeScreenTitleDisplayed(String text, long seconds) {
        return screenTitle(text).isDisplayed(seconds);
    }

    /**
     * Ждём greetingLabel, затем любой известный текст заголовка;
     * если label есть, а текст бренда другой — всё равно считаем welcome открытым.
     */
    public boolean isAnyWelcomeScreenTitleDisplayed(String[] candidates, long seconds) {
        if (!greetingLabel.isDisplayed(seconds)) {
            return false;
        }
        for (String candidate : candidates) {
            if (screenTitle(candidate).isDisplayed(1L)) {
                return true;
            }
        }
        return true;
    }

    public boolean checkAlertChangeServerDisplayed() {
        return serverChangeAlert.isDisplayed();
    }
}
