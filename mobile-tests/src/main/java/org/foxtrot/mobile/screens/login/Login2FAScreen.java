package org.foxtrot.mobile.screens.login;

import org.foxtrot.mobile.elements.Button;
import org.foxtrot.mobile.elements.TextBox;
import org.foxtrot.mobile.elements.TextView;

import static org.foxtrot.mobile.elements.base.BaseElement.*;

public class Login2FAScreen {

    private final Button continueBtn = new Button(
            byResourceId("btnDone"),
            byAccessibilityId("TFACheckPasscode.nextButton"));
    private final TextBox codeInput = new TextBox(
            byResourceId("etInputField"),
            byAccessibilityId("Common.textField"));
    private final TextView screenTitle = new TextView(
            byResourceId("tvTitle"),
            byAccessibilityId("TFACheckPasscode.titleLabel")
    );
    private final TextView validationText = new TextView(
            byResourceId("tvHint"),
            byAccessibilityId("Common.textFieldHint")
    );

    public void clickContinueBtn() {
        continueBtn.click();
    }

    public void fillCode(String code) {
        codeInput.sendKeys(code);
    }

    public boolean is2FAScreenTitleDisplayed(long seconds) {
        return screenTitle.isDisplayed(seconds);
    }

    public String get2FAScreenTitleText() {
        return screenTitle.getText();
    }

    public String getValidationText() {
        return validationText.getText();
    }
}
