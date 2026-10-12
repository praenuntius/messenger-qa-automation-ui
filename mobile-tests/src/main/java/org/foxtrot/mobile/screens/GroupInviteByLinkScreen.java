package org.foxtrot.mobile.screens;

import org.foxtrot.mobile.elements.Button;
import org.foxtrot.mobile.elements.TextView;

import static org.foxtrot.mobile.elements.base.BaseElement.*;

public class GroupInviteByLinkScreen {

    private final TextView screenTitle = new TextView(
            byResourceId("inviteTitle"),
            byAccessibilityId("InviteByLink.navigationTitleLabel")
    );

    private final TextView inviteLink = new TextView(
            byResourceId("inviteLink"),
            byAccessibilityId("InviteByLink.linkField")
    );

    private final Button copyLinkBtn = new Button(
            byResourceId("copyLink"),
            byAccessibilityId("InviteByLink.copyLinkButton")
    );

    private final Button generateNewLinkBtn = new Button(
            byResourceId("generateLink"),
            byAccessibilityId("InviteByLink.generateNewLinkButton")
    );

    public boolean isScreenTitleDisplayed(long seconds) {
        return screenTitle.isDisplayed(seconds);
    }

    public String getScreenTitleText() {
        return screenTitle.getText();
    }

    public String getInviteLinkText() {
        return inviteLink.getText();
    }

    public void clickCopyLinkButton() {
        copyLinkBtn.click();
    }

    public void clickGenerateNewLinkButton() {
        generateNewLinkBtn.click();
    }
}
