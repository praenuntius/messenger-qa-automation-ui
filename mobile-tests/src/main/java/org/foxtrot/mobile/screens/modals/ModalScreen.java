package org.foxtrot.mobile.screens.modals;

import org.foxtrot.mobile.elements.Button;
import org.foxtrot.mobile.elements.TextView;

import static org.foxtrot.mobile.elements.base.BaseElement.*;

public class ModalScreen {

    private final TextView modalHeader = new TextView(
            byResourceId("textTitle"),
            byAccessibilityId("Alert.title")
    );
    private final TextView modalText = new TextView(
            byResourceId("textMessage"),
            byAccessibilityId("Alert.message")
    );
    private final Button modalCancelBtn = new Button(
            byResourceId("buttonCancel"),
            byAccessibilityId("Alert.cancelButton")
    );
    private final Button modalActionBtn = new Button(
            byResourceId("buttonAction"),
            byAccessibilityId("Alert.logoutButton")
    );

    private final Button modalLeaveBtn = new Button(
            byResourceId("buttonAction"),
            byAccessibilityId("Alert.leaveButton")
    );

    private final Button modalInviteBtn = new Button(
            byResourceId("buttonAction"),
            byAccessibilityId("Alert.inviteButton")
    );

    private final Button modalEnterBtn = new Button(
            byResourceId("buttonAction"),
            byAccessibilityId("Alert.enterButton")
    );

    private Button modalActionBtnByText(String modalText) {
        return new Button(byEqualsText(modalText));
    }

    public void clickCancelBtn() {
        modalCancelBtn.click();
    }

    public String getCancelBtnText() {
        return modalCancelBtn.getText();
    }

    public void clickActionBtn() {
        modalActionBtn.click();
    }

    public void clickLeaveBtn() {
        modalLeaveBtn.click();
    }

    public void clickInviteBtn() {
        modalInviteBtn.click();
    }

    public void clickEnterBtn() {
        modalEnterBtn.click();
    }

    public void clickActionBtnByText(String modalText) {
        modalActionBtnByText(modalText).click();
    }

    public String getActionBtnText() {
        return modalActionBtn.getText();
    }

    public String getModalHeaderText() {
        return modalHeader.getText();
    }

    public String getModalText() {
        return modalText.getText();
    }

    public boolean isMessageDisplayed() {
        return modalText.isDisplayed();
    }

    public boolean isHeaderDisplayed() {
        return modalHeader.isDisplayed();
    }
}
