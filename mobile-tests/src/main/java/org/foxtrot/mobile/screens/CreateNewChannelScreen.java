package org.foxtrot.mobile.screens;

import org.foxtrot.mobile.elements.*;

import static org.foxtrot.mobile.config.MobilePlatformResolver.isIos;
import static org.foxtrot.mobile.elements.base.BaseElement.*;

public class CreateNewChannelScreen {

    private final Button nextButton = new Button(
            byResourceId("next"),
            byAccessibilityId("NewGroup.navigationNextButton")
    );

    private final Button doneButton = new Button(
            byResourceId("done"),
            byAccessibilityId("NewGroup.navigationNextButton")
    );

    private final Button uploadAvatarButton = new Button(
            byResourceId("buttonUpload"),
            byAccessibilityId("NewGroup.headerButton")
    );

    private final TextBox channelNameInput = new TextBox(
            byResourceId("etName"),
            byAccessibilityId("NewGroup.headerTitleField")
    );

    private final TextBox channelDescriptionInput = new TextBox(
            byResourceId("etDescription"),
            byAccessibilityId("NewGroup.headerSubtitleField")
    );

    private final Image privateCheckmark = new Image(
            byAccessibilityId("NewGroup.privateCheckmarkImageView")
    );

    private final Image publicCheckmark = new Image(
            byAccessibilityId("NewGroup.publicCheckmarkImageView")
    );

    private final Image chatAvatar = new Image(
            byResourceId("image"),
            byAccessibilityId("NewGroup.headerImage")
    );

    private final RadioButton publicChannelButton = new RadioButton(
            byResourceId("rbPublic"),
            byAccessibilityId("NewGroup.publicCheckmarkTitle")
    );

    private final RadioButton privateChannelButton = new RadioButton(
            byResourceId("rbPrivate"),
            byAccessibilityId("NewGroup.privateCheckmarkTitle")
    );

    private TextView screenTitle(String text) {
        return new TextView(
                byEqualsText(text),
                byAccessibilityIdAndText("NewGroup.navigationTitle", text)
        );
    }

    public boolean isScreenTitleDisplayed(String text, long seconds) {
        return screenTitle(text).isDisplayed(seconds);
    }

    public void clickNextBtn() {
        nextButton.click();
    }

    public void clickDoneBtn() {
        doneButton.click();
    }

    public void clickUploadAvatarBtn() {
        uploadAvatarButton.click();
    }

    public void fillChannelName(String channelName) {
        channelNameInput.clear();
        channelNameInput.sendKeys(channelName);
    }

    public void fillChannelDescription(String channelDescription) {
        channelDescriptionInput.clear();
        channelDescriptionInput.sendKeys(channelDescription);
    }

    public boolean isPublicChannelButtonChecked() {
        return isIos() ? publicCheckmark.isDisplayed() : publicChannelButton.isChecked();
    }

    public boolean isPrivateChannelButtonChecked() {
        return isIos() ? privateCheckmark.isDisplayed() : privateChannelButton.isChecked();
    }

    public void clickPublicChannelButton() {
        publicChannelButton.click();
    }

    public void clickPrivateChannelButton() {
        privateChannelButton.click();
    }

    public boolean isChatAvatarPlaceholder() {
        return chatAvatar.isPlaceholder();
    }
}
