package org.foxtrot.mobile.screens.homespace;

import org.foxtrot.mobile.elements.Button;
import org.foxtrot.mobile.elements.TextBox;
import org.foxtrot.mobile.elements.TextView;

import static org.foxtrot.mobile.elements.base.BaseElement.*;

public class CreateNewMessageFromHomespaceScreen {

    private final Button createGroupButton = new Button(
            byResourceId("createGroup"),
            byAccessibilityId("NewMessage.createGroupContainer"));
    private final Button createChannelButton = new Button(
            byResourceId("createChannel"),
            byAccessibilityId("NewMessage.createChannelContainer"));
    private final Button inviteToAppButton = new Button(
            byResourceId("invite"),
            byAccessibilityId("NewMessage.inviteContainer"));
    private final Button closeButton = new Button(
            byResourceId("buttonBack"),
            byAccessibilityId("NewMessage.navigationClose"));
    private final Button searchButton = new Button(
            byResourceId("buttonSearch"));
    private final TextBox searchInput = new TextBox(
            byResourceId("editSearchQuery"),
            byAccessibilityId("Common.textField"));
    private final Button cancelButton = new Button(
            byResourceId("buttonCancel"),
            byAccessibilityId("123"));

    private TextView screenTitle(String text) {
        return new TextView(
                byResourceIdAndText("textTitle", text),
                byAccessibilityIdAndText("NewMessage.navigationTitle", text));
    }

    private Button screenSubtitle(String text) {
        return new Button(
                byResourceIdAndText("textSubtitle", text),
                byAccessibilityIdAndText("Common.tableViewSectionHeader", text));
    }

    private Button contactName(int number) {
        return new Button(
                byContactsListText(number), 0,
                byAccessibilityId("CommonCell.title"), number - 1);
    }

    public boolean isScreenTitleDisplayed(String text, long seconds) {
        return screenTitle(text).isDisplayed(seconds);
    }

    public String getCreateGroupButtonText() {
        return createGroupButton.getText();
    }

    public String getCreateChannelButtonText() {
        return createChannelButton.getText();
    }

    public String getInviteToAppButtonText() {
        return inviteToAppButton.getText();
    }

    public void clickCreateGroupButton() {
        createGroupButton.click();
    }

    public void clickCreateChannelButton() {
        createChannelButton.click();
    }

    public void clickInviteToAppButton() {
        inviteToAppButton.click();
    }

    public boolean isScreenSubtitleDisplayed(String text, long seconds) {
        return screenSubtitle(text).isDisplayed(seconds);
    }

    public String getContactNameText(int number, long seconds) {
        // todo задача https://allprojects.atlassian.net/browse/FB-20804
        return contactName(number).getText(seconds).replaceAll("\\s+", " ").trim();
    }

    public boolean isContactByTextDisplayed(String contactName, long seconds) {
        for (String needle : org.foxtrot.enums.UsersEnum.uiNameNeedles(contactName)) {
            TextView byText = new TextView(
                    byContactsListText(needle),
                    byAccessibilityIdAndText("CommonCell.title", needle));
            if (byText.isDisplayed(seconds)) {
                return true;
            }
            TextView byContains = new TextView(
                    byContainsText(needle),
                    byAccessibilityIdAndContainsText("CommonCell.title", needle));
            if (byContains.isDisplayed(Math.min(2L, seconds))) {
                return true;
            }
        }
        return false;
    }

    public void clickContact(int number) {
        contactName(number).click();
    }
}
