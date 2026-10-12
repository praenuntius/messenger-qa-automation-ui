package org.foxtrot.pages.chat.input_bar.elements;

import lombok.Getter;
import org.openqa.selenium.By;

import static org.openqa.selenium.By.cssSelector;

@Getter
public class InputBar {
    private final By messageField = cssSelector("[data-cy='messageFieldInput']");
    private final By messageEditor = cssSelector("[class='ql-editor']");
    private final By mentionInInputField = cssSelector("span.mention");
    private final By messageSendButton = cssSelector("[data-cy='sendMessageAreaSubmitMessage']");
    private final By addEmojiButton = cssSelector("[data-cy='addEmojiButton']");
    private final By openAttachContextMenuButton = cssSelector(
            "form[class*='SendMessageForm'] [aria-haspopup='menu'], button[class*='AttachActionButton']");
    private final By emptyInputFieldTextPlaceholder = By.xpath("/html/body/div[1]/div[1]/div[4]/div[3]/form/div[1]/div/div/div");
    private final By openEmojiBarButton = By.xpath("/html/body/div[1]/div[1]/div[4]/div[3]/form/div[2]/button/svg");
    private final By recordVoiceMessageButton = By.xpath("/html/body/div[1]/div[1]/div[4]/div[3]/form/span[2]/button/svg");
    private final By sendMessageButton = By.cssSelector("sendMessageAreaSubmitMessage");
    private final By mentionButton = By.cssSelector("[data-cy='mentionButton']");
    private final By joinToGroupButton = By.xpath("//div[text() = 'Вступить в группу']");
    private final By subscribeToChannelButton = By.xpath("//div[text()='Подписаться на канал']");
    private final By tagMenuButton = cssSelector("svg use[href='#icTagBadge']");
    private final By tagCounterInInputBar = cssSelector("div[class*='TagCounter']");
    private final By scheduledMessagesButton = cssSelector("[data-cy='scheduled']");
    private final By autoDeleteStatus = cssSelector("span[class*='AutodeleteSettingsTitle']");
    private final By replyFieldView = cssSelector("div[class*='ReplyFieldView']");
    private final By attachedLinkPreview = cssSelector("div[class*='AttachedLinkPreview']");
    private final By attachedMediaImagePreview = cssSelector("div[class*='MediaContainer'] div[src*='data:image']");
    private final By attachedMediaVideoPreview = cssSelector("div[class*='MediaContainer'] div[src*='blob']");
    private final By editorLine = cssSelector("div");
    private final By editorLink = cssSelector("a");
}
