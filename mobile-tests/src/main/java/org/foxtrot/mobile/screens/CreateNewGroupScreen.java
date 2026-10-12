package org.foxtrot.mobile.screens;

import org.foxtrot.mobile.elements.*;

import static org.foxtrot.mobile.config.MobilePlatformResolver.isIos;
import static org.foxtrot.mobile.elements.base.BaseElement.*;

public class CreateNewGroupScreen {

    private final Button nextButton = new Button(
            byResourceId("next"),
            byAccessibilityId("NewGroup.navigationNextButton")
    );

    private final Button saveButton = new Button(
            byResourceId("buttonSave"),
            byAccessibilityId("NewGroup.navigationNextButton")
    );

    private final Button uploadAvatarButton = new Button(
            byResourceId("buttonUpload"),
            byAccessibilityId("NewGroup.headerButton")
    );

    private final TextBox groupNameInput = new TextBox(
            byResourceId("etName"),
            byAccessibilityId("NewGroup.headerTitleField")
    );

    private final TextBox groupDescriptionInput = new TextBox(
            byResourceId("etDescription"),
            byAccessibilityId("NewGroup.headerSubtitleField")
    );

    private final Image privateCheckmark = new Image(
            byAccessibilityId("NewGroup.privateCheckmarkImageView")
    );

    private final Image publicCheckmark = new Image(
            byAccessibilityId("NewGroup.publicCheckmarkImageView")
    );

    private final RadioButton publicGroupButton = new RadioButton(
            byResourceId("rbPublic"),
            byAccessibilityId("NewGroup.publicCheckmarkTitle")
    );

    private final RadioButton privateGroupButton = new RadioButton(
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

    public void clickSaveBtn() {
        saveButton.click();
    }

    public void clickUploadAvatarBtn() {
        uploadAvatarButton.click();
    }

    public void fillGroupName(String groupName) {
        groupNameInput.clear();
        groupNameInput.sendKeys(groupName);
    }

    public void fillGroupDescription(String groupDescription) {
        groupDescriptionInput.clear();
        groupDescriptionInput.sendKeys(groupDescription);
    }

    public boolean isPublicGroupButtonChecked() {
        return isIos() ? publicCheckmark.isDisplayed() : publicGroupButton.isChecked();
    }

    public boolean isPrivateGroupButtonChecked() {
        return isIos() ? privateCheckmark.isDisplayed() : privateGroupButton.isChecked();
    }

    public void clickPublicGroupButton() {
        publicGroupButton.click();
    }

    public void clickPrivateGroupButton() {
        privateGroupButton.click();
    }
}
