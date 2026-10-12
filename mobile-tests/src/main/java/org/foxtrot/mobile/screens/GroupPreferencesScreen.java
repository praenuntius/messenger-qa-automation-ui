package org.foxtrot.mobile.screens;

import org.foxtrot.mobile.elements.Block;
import org.foxtrot.mobile.elements.Button;
import org.foxtrot.mobile.elements.Image;
import org.foxtrot.mobile.elements.TextView;

import static org.foxtrot.mobile.elements.base.BaseElement.*;

public class GroupPreferencesScreen {

    private final Block screenHeader = new Block(
            byResourceId("blockContent"),
            byAccessibilityId("MessengerUI.ChatProfileView")
    );

    private final TextView groupName = new TextView(
            byResourceId("textTitle"),
            byAccessibilityId("ChatProfile.chatName")
    );

    private final TextView groupSubName = new TextView(
            byResourceId("textSubtitle"),
            byAccessibilityId("ChatProfile.chatTypeLabel")
    );

    private final Button participantsBtn = new Button(
            byResourceId("buttonMembers"),
            byAccessibilityIdAndText("MenuButton.titleLabel", "Участники")
    );

    private final Button participantsNumber = new Button(
            byResourceId("textNumberOfMembers"),
            byAccessibilityId("MenuButton.detailsLabel")
    );

    private final TextView groupDescription = new TextView(
            byResourceId("textDescription"),
            byAccessibilityId("ChatProfile.chatDescriptionLabel")
    );

    private final Button settingsBtn = new Button(
            byResourceId("buttonSettings"),
            byAccessibilityIdAndText("MenuButton.titleLabel", "Настройки")
    );

    private final Image chatAvatar = new Image(
            byResourceId("imageGroup"),
            byAccessibilityId("ChatProfile.avatar")
    );

    private final Button buttonLeaveChat = new Button(
            byResourceId("buttonLeaveChat"),
            byAccessibilityIdAndText("MenuButton.titleLabel", "Покинуть группу")
    );

    private final Button copyGroupLinkBtn = new Button(
            byResourceId("buttonCopyLink"),
            byAccessibilityIdAndText("MenuButton.titleLabel", "Скопировать ссылку на группу")
    );

    private final Button inviteByLinkBtn = new Button(
            byResourceId("buttonInvite"),
            byAccessibilityIdAndText("MenuButton.titleLabel", "Пригласить по ссылке")
    );

    private TextView toastText(String text) {
        return new TextView(
                byResourceIdAndText("snackbar_text", text),
                byAccessibilityIdAndText("Toast.titleLabel", text)
        );
    }

    public boolean isScreenHeaderDisplayed(long seconds) {
        return screenHeader.isDisplayed(seconds);
    }

    public String getGroupNameInHeader() {
        return groupName.getText();
    }

    public String getGroupSubNameInHeader() {
        return groupSubName.getText();
    }

    public void clickParticipantsBtn() {
        participantsBtn.click();
    }

    public boolean isParticipantsButtonDisplayed() {
        return participantsBtn.isDisplayed();
    }

    public void clickSettingsBtn() {
        settingsBtn.click();
    }

    public String getParticipantsNumber() {
        return participantsNumber.getText();
    }

    public String getGroupDescriptionInHeader() {
        return groupDescription.getText();
    }

    public boolean isChatAvatarPlaceholder() {
        return chatAvatar.isPlaceholder();
    }

    public boolean isLeaveGroupButtonDisplayed() {
        return buttonLeaveChat.isDisplayed();
    }

    public void clickLeaveGroupButton() {
        buttonLeaveChat.click();
    }

    public boolean isCopyGroupLinkButtonDisplayed() {
        return copyGroupLinkBtn.isDisplayed();
    }

    public void clickCopyGroupLinkButton() {
        copyGroupLinkBtn.click();
    }

    public void clickInviteByLinkButton() {
        inviteByLinkBtn.click();
    }

    public boolean isToastDisplayed(String text, long seconds) {
        return toastText(text).isDisplayed(seconds);
    }

}
