package org.foxtrot.mobile.screens.login;

import org.foxtrot.mobile.elements.Button;
import org.foxtrot.mobile.elements.TextBox;
import org.foxtrot.mobile.elements.TextView;

import static org.foxtrot.mobile.elements.base.BaseElement.byAccessibilityId;
import static org.foxtrot.mobile.elements.base.BaseElement.byAccessibilityIdAndText;
import static org.foxtrot.mobile.elements.base.BaseElement.byResourceId;
import static org.foxtrot.mobile.elements.base.BaseElement.byResourceIdAndText;

public class LoginVerificationCodeScreen {

    private final Button continueBtn = new Button(
            byResourceId("btnVerifyCode"),
            byAccessibilityId("EnterCodeWhileLogin.nextButton")
    );
    private final TextBox codeInput = new TextBox(
            byResourceId("etInputField"),
            byAccessibilityId("Common.textField")
    );

    private TextView screenTitle(String text) {
        return new TextView(
                byResourceIdAndText("tvCodeTitle", text),
                byAccessibilityIdAndText("EnterCodeWhileLogin.enterCodeLabel", text)
        );
    }

    public void clickContinueBtn() {
        continueBtn.click();
    }

    public boolean checkContinueBtnEnabled() {
        return continueBtn.isEnabled();
    }

    public void fillCode(String code) {
        codeInput.sendKeys(code);
    }

    public boolean isVerificationCodeScreenTitleDisplayed(String text, long seconds) {
        return screenTitle(text).isDisplayed(seconds);
    }
}
