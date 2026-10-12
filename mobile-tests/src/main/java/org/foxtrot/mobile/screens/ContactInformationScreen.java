package org.foxtrot.mobile.screens;

import org.foxtrot.mobile.elements.TextView;

import static org.foxtrot.mobile.elements.base.BaseElement.byAccessibilityId;
import static org.foxtrot.mobile.elements.base.BaseElement.byResourceId;

public class ContactInformationScreen {

    private final TextView contactName = new TextView(
            byResourceId("textTitle"),
            byAccessibilityId("OpponentProfile.titleNameLabel")
    );

    private final TextView messageBtn = new TextView(
            byResourceId("buttonMessage"),
            byAccessibilityId("OpponentProfile.sendMessageButton")
    );

    public String getContactName() {
        return contactName.getText();
    }

    public void clickMessageBtn() {
        messageBtn.click();
    }
}
