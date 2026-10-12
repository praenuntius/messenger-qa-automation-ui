package org.foxtrot.mobile.screens.workspace;

import org.foxtrot.mobile.elements.Button;
import org.foxtrot.mobile.elements.TextBox;
import org.foxtrot.mobile.elements.TextView;

import static org.foxtrot.mobile.elements.base.BaseElement.*;

public class CreateNewMessageFromWorkspaceScreen {

    private final Button createGroupButton = new Button(
            byResourceId("create_group"),
            byAccessibilityId("WorkspaceGroupsList.createGroup"));
    private final Button createChannelButton = new Button(
            byResourceId("btnCreateChannel"),
            byAccessibilityId("WorkspaceGroupsList.createChannel"));
    private final Button closeButton = new Button(
            byResourceId("buttonBack"),
            byAccessibilityId("WorkspaceGroupsList.navigationClose"));
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
                byAccessibilityIdAndText("WorkspaceGroupsList.navigationTitle", text));
    }

    private TextView screenSubtitle(String text) {
        return new TextView(
                byResourceIdAndText("tvAvailableChatsAndChannels", text),
                byAccessibilityIdAndText("Common.tableViewSectionHeaderTitle", text));
    }

    private Button chatName(String text) {
        return new Button(
                byResourceIdAndText("chatTitle", text),
                byAccessibilityIdAndText("WorkspaceGroupCell.nameLabel", text));
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

    public void clickCreateGroupButton() {
        createGroupButton.click();
    }

    public void clickCreateChannelButton() {
        createChannelButton.click();
    }

    public boolean isScreenSubtitleDisplayed(String text, long seconds) {
        return screenSubtitle(text).isDisplayed(seconds);
    }

    public boolean isChatNameByTextDisplayed(String text, long seconds) {
        return chatName(text).isDisplayed(seconds);
    }
}
