package org.foxtrot.mobile.screens;

import org.foxtrot.mobile.elements.Block;
import org.foxtrot.mobile.elements.Button;
import org.foxtrot.mobile.elements.Image;
import org.foxtrot.mobile.elements.TextView;

import static org.foxtrot.mobile.elements.base.BaseElement.*;

public class ChannelPreferencesScreen {

    private final Block screenHeader = new Block(
            byResourceId("toolbar"),
            byAccessibilityId("MessengerUI.ChatProfileView")
    );
    private final TextView channelName = new TextView(
            byResourceId("name"),
            byAccessibilityId("ChatProfile.chatName")
    );

    private final TextView channelSubName = new TextView(
            byResourceId("subtitle"),
            byAccessibilityId("ChatProfile.chatTypeLabel")
    );

    private final TextView channelDescription = new TextView(
            byResourceId("description"),
            byAccessibilityId("ChatProfile.chatDescriptionLabel")
    );

    private final Button participantsBtn = new Button(
            byResourceId("subscribers"),
            byAccessibilityIdAndText("MenuButton.titleLabel", "Подписчики")
    );

    private final Button settingsBtn = new Button(
            byResourceId("settings"),
            byAccessibilityIdAndText("MenuButton.titleLabel", "Настройки")
    );

    private final Image chatAvatar = new Image(
            byResourceId("image"),
            byAccessibilityId("ChatProfile.avatar")
    );

    public boolean isScreenHeaderDisplayed(long seconds) {
        return screenHeader.isDisplayed(seconds);
    }

    public String getChannelNameInHeader() {
        return channelName.getText();
    }

    public String getChannelSubNameInHeader() {
        return channelSubName.getText();
    }

    public String getChannelDescriptionInHeader() {
        return channelDescription.getText();
    }

    public void clickParticipantsBtn() {
        participantsBtn.click();
    }

    public void clickSettingsBtn() {
        settingsBtn.click();
    }

    public boolean isChatAvatarPlaceholder() {
        return chatAvatar.isPlaceholder();
    }
}
