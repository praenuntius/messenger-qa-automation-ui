package org.foxtrot.mobile.screens;

import io.appium.java_client.AppiumBy;
import org.foxtrot.mobile.elements.*;

import static org.foxtrot.mobile.elements.base.BaseElement.*;

public class ChatScreen {

    private final Block screenHeader = new Block(
            byResourceId("toolbarContainer"),
            byAccessibilityId("MessengerUI.ChatView")
    );

    private final Button chatName = new Button(screenHeader,
            byResourceId("toolbarTitle"),
            byAccessibilityId("Chat.NavigationBar.contactNameLabel")
    );

    private final Block chatScreen = new Block(
            byResourceId("rvMessages"),
            byAccessibilityId("Chat.chatScreen")
    );

    private final Block messageContentView = new Block(
            byAccessibilityId("ChatMessage.messageContentView")
    );

    private final TextBox messageInput = new TextBox(
            byResourceId("inputMessageText"),
            byAccessibilityId("Chat.TextInputPanel.messageTextView")
    );

    /**
     * Fallback для сборок без accessibility id (stage14 Messenger.app / foxtrot).
     */
    private final TextBox messageInputFallback = new TextBox(
            byResourceId("inputMessageText"),
            AppiumBy.iOSClassChain("**/XCUIElementTypeTextView[`visible == 1`]")
    );

    private final Button sendButton = new Button(
            byResourceId("buttonSend"),
            byAccessibilityId("Chat.TextInputPanel.rightButton")
    );

    private final Button sendButtonFallback = new Button(
            byResourceId("buttonSend"),
            AppiumBy.iOSNsPredicateString(
                    "name CONTAINS 'Send' OR label CONTAINS 'Send' OR name CONTAINS 'Отправ' OR label CONTAINS 'Отправ'"
            )
    );

    private final Button attachButton = new Button(
            byResourceId("buttonAttach"),
            byAccessibilityId("Chat.TextInputPanel.attachButton")
    );

    private TextView systemMessageText(int number) {
        return new TextView(
                byResourceId("systemMessageText"), number - 1,
                byAccessibilityId("ChatMessage.systemMessageLabel"), number - 1
        );
    }

    private final TextView chatStatus = new TextView(screenHeader,
            byResourceId("toolbarSubTitle"),
            byAccessibilityId("Chat.NavigationBar.statusLabel")
    );

    private final Block chatsBlock = new Block(
            byResourceId("rvMessages"),
            byAccessibilityId("123")
    );

    private TextView chatMessage(int number) {
        return new TextView(
                byResourceId("messageText"), number - 1, // todo добавить локатор
                byAccessibilityId("ChatMessage.messageTextView"), number - 1
        );
    }

    private TextView chatMessage(String text) {
        return new TextView(
                byEqualsText(text),
                byAccessibilityIdAndText("ChatMessage.messageTextView", text)
        );
    }

    private TextView alertText(String text) {
        return new TextView(
                byResourceIdAndText("snackbar_text", text),
                byAccessibilityIdAndText("Toast.titleLabel", text)
        );
    }

    private final Image chatAvatar = new Image(
            byResourceId("ivChannel"),
            byAccessibilityId("Chat.NavigationBar.avatarImageView")
    );

    private Image mediaImage(int number) {
        return new Image(
                byResourceId("ivPreview"), number - 1,
                byAccessibilityId("123"), number - 1
        );
    }

    private Button mediaImageRemoveBtn(int number) {
        return new Button(
                byResourceId("btnRemoveMedia"), number - 1,
                byAccessibilityId("123"), number - 1
        );
    }

    private Button joinBtn(String text) {
        return new Button(
                byResourceIdAndText("buttonJoin", text),
                byAccessibilityIdAndText("Chat.ChatActionPanel.secondaryButton", text)
        );
    }

    private Button subscribeBtn(String text) {
        return new Button(
                byResourceIdAndText("buttonSubscribe", text),
                byAccessibilityIdAndText("Chat.ChatActionPanel.primaryButton", text)
        );
    }

    private TextView mediaFileTitle(int number) {
        return new TextView(
                byResourceId("tvTitle"), number - 1,
                byAccessibilityId("123"), number - 1
        );
    }

    public boolean isChatScreenDisplayed(long seconds) {
        // Chat.chatScreen часто отсутствует; любой маркер чата = экран открыт
        long slice = Math.max(2L, seconds / 4);
        return screenHeader.isDisplayed(slice)
                || chatScreen.isDisplayed(slice)
                || chatName.isDisplayed(slice)
                || sendButton.isDisplayed(slice)
                || messageInput.isDisplayed(slice)
                || messageInputFallback.isDisplayed(slice)
                || sendButtonFallback.isDisplayed(slice);
    }

    /**
     * На foxtrot resourceId хедера/инпута могут отличаться от tango —
     * имя чата в дереве (textContains) надёжнее фиксированных id.
     */
    public boolean isChatScreenDisplayed(String expectedChatName, long seconds) {
        if (isChatScreenDisplayed(Math.max(2L, seconds / 2))) {
            return true;
        }
        if (expectedChatName == null || expectedChatName.isBlank()) {
            return false;
        }
        TextView byName = new TextView(byContainsText(expectedChatName), byContainsText(expectedChatName));
        return byName.isDisplayed(seconds);
    }

    public boolean isMessageContentViewDisplayed() {
        return messageContentView.isDisplayed();
    }

    public String getSystemMessageText(int number) {
        return systemMessageText(number).getText();
    }

    public String getChatNameFromHeader() {
        return chatName.getText();
    }

    public String getChatStatusFromHeader() {
        return chatStatus.getText();
    }

    public void clickChatName() {
        chatName.click();
    }

    public void enterMessage(String text) {
        if (messageInput.isDisplayed(3L)) {
            messageInput.click();
            messageInput.sendKeys(text);
            return;
        }
        if (messageInputFallback.isDisplayed(3L)) {
            messageInputFallback.click();
            messageInputFallback.sendKeys(text);
            return;
        }
        // foxtrot: resourceId инпута может отличаться — любой видимый EditText / TextView
        TextBox anyInput = new TextBox(
                byClassName("android.widget.EditText"),
                AppiumBy.iOSClassChain("**/XCUIElementTypeTextView[`visible == 1`]")
        );
        anyInput.click();
        anyInput.sendKeys(text);
    }

    public void clickSend() {
        if (sendButton.isDisplayed(3L)) {
            sendButton.click();
            return;
        }
        sendButtonFallback.click();
    }

    public String getChatMessageText(int number) {
        return chatMessage(number).getText();
    }

    public void clickChatMessage(int number) {
        chatMessage(number).click();
    }

    public boolean isChatMessageTextDisplayed(String text, long seconds) {
        return chatMessage(text).isDisplayed(seconds);
    }

    public boolean isChatAvatarPlaceholder() {
        return chatAvatar.isPlaceholder();
    }

    public boolean isAlertMessageDisplayed(String text, long seconds) {
        return alertText(text).isDisplayed(seconds);
    }

    public boolean isMediaImageDisplayed(int number, long seconds) {
        return mediaImage(number).isDisplayed(seconds);
    }

    public void clickAttachBtn() {
        attachButton.click();
    }

    public void clickJoinBtn(String text) {
        joinBtn(text).click();
    }

    public boolean isJoinBtnDisplayed(String text) {
        return joinBtn(text).isDisplayed();
    }

    public void clickSubscribeBtn(String text) {
        subscribeBtn(text).click();
    }

    public boolean isSubscribeBtnDisplayed(String text) {
        return subscribeBtn(text).isDisplayed();
    }
}
