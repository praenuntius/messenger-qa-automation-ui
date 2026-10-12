package org.foxtrot.pages.chat.chat;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.foxtrot.enums.SystemMessageType;
import org.foxtrot.pages.chat.chat.elements.Chat;
import org.foxtrot.pages.chat.comments_threads.elements.Comments;
import org.foxtrot.pages.chat.input_bar.elements.InputBar;
import org.foxtrot.pages.chat.system_messages.elements.SystemMessages;
import org.foxtrot.pages.links.LinksInBrowser;
import org.openqa.selenium.By;

import java.time.Duration;

import static com.codeborne.selenide.CollectionCondition.*;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static org.openqa.selenium.By.xpath;

public class ChatVerify {

    private final Chat elements = new Chat();
    private final Comments commentsElements = new Comments();
    private final InputBar inputBar = new InputBar();
    private final SystemMessages systemMessagesElements = new SystemMessages();
    private final LinksInBrowser linksInBrowser = new LinksInBrowser();
    private String sentMessageText;
    Duration timeout = Duration.ofSeconds(30);

    @Step("Проверить, что кастомные обои и узор применены в чате")
    public ChatVerify verifyCustomWallpaperApplied(String expectedColor, String expectedClass) {
        $(elements.getChatWallpaperPatternImage())
                .shouldBe(visible, timeout)
                .shouldHave(attribute("color", expectedColor))
                .shouldHave(attribute("class", expectedClass));
        return this;
    }

    @Step("Проверить наличие системного сообщения, о добавлении пользователя {userName} в группу")
    public ChatVerify verifyGroupInviteSystemMessage(String userName) {
        String user = elements.getDefinedGroupInviteSystemMessage().replace("{userName}", userName);
        $(xpath(user)).shouldBe(visible);
        return this;
    }

    @Step("Проверить наличие системного сообщения, об полной очистке сообщений группы")
    public ChatVerify verifyClearMessageHistorySystemMessage() {
        $(elements.getClearAllMessageHistory()).shouldBe(visible);
        return this;
    }

    @Step("Проверить имя отправителя сообщения")
    public void verifySenderName(String expectedName, String expectedMessage) {
        String selector = elements.getDefinedMessageSender()
                .replace("{sender}", expectedName)
                .replace("{message}", expectedMessage);
        $(xpath(selector)).shouldBe(visible);
    }

    @Step("Проверить наличие заглушки с инициалами у пользователя в ленте p2p чата")
    public ChatVerify verify2pHasInitialsAvatarPlaceholder() {
        $(inputBar.getMessageField()).shouldBe(interactable);
        $(elements.getUserPhoto()).shouldNotBe(visible);
        $(elements.getUserAvatarPlaceholder()).shouldNotBe(visible);
        return this;
    }

    @Step("Проверить наличие серой заглушки у пользователя в ленте p2p чата")
    public ChatVerify verify2pHasUserSilhouettePlaceholder() {
        $(elements.getUserPhoto()).shouldNotBe(visible);
        $(elements.getUserAvatarPlaceholder()).shouldBe(visible);
        return this;
    }

    @Step("Проверить наличие аватарки у пользователя в ленте p2p чата")
    public ChatVerify verify2pHasAvatar() {
        $(inputBar.getMessageField()).shouldBe(interactable);
        $(elements.getUserPhoto()).shouldBe(visible);
        $(elements.getUserAvatarPlaceholder()).shouldNotBe(visible);
        return this;
    }

    @Step("Проверить отображение имени не-контакта {name} в пузыре ответа (reply)")
    public ChatVerify verifyUserAppearanceInReplyBubble(String name) {
        $(elements.getReplyMessageByAuthorName())
                .shouldHave(text(name))
                .shouldBe(visible);
        return this;
    }

    @Step("Сохранить текст из поля ввода")
    public ChatVerify saveMessageInputText() {
        sentMessageText = $(elements.getSavedMessageText()).getText();
        return this;
    }

    @Step("Проверить, что сообщение с текстом '{messageText}' отображается в чате")
    public ChatVerify verifyMessageSent(String messageText) {
        messagesContainer()
                .$$("div")
                .findBy(text(messageText))
                .shouldBe(visible, timeout);
        return this;
    }

    private SelenideElement messagesContainer() {
        SelenideElement commentsArea = $(commentsElements.getCommentsChatArea());
        if (commentsArea.exists() && commentsArea.isDisplayed()) {
            return commentsArea.shouldBe(visible, Duration.ofSeconds(10));
        }
        return $(elements.getContainerWithMessages()).shouldBe(visible, Duration.ofSeconds(10));
    }

    private SelenideElement findLastMessageBubbleWithChild(By childLocator, Duration searchTimeout) {
        var bubbles = messagesContainer().$$(elements.getMessageBubble()).filterBy(visible);
        long deadline = System.currentTimeMillis() + searchTimeout.toMillis();
        while (System.currentTimeMillis() < deadline) {
            for (int i = bubbles.size() - 1; i >= 0; i--) {
                SelenideElement bubble = bubbles.get(i);
                SelenideElement child = bubble.$(childLocator);
                if (child.exists() && child.isDisplayed()) {
                    return bubble;
                }
            }
            sleep(200);
            bubbles = messagesContainer().$$(elements.getMessageBubble()).filterBy(visible);
        }
        return messagesContainer()
                .$$(elements.getMessageBubble())
                .last()
                .shouldBe(visible, searchTimeout);
    }

    private SelenideElement findLastMessageBubbleWithImage(Duration searchTimeout) {
        var bubbles = messagesContainer().$$(elements.getMessageBubble()).filterBy(visible);
        long deadline = System.currentTimeMillis() + searchTimeout.toMillis();
        while (System.currentTimeMillis() < deadline) {
            for (int i = bubbles.size() - 1; i >= 0; i--) {
                if (bubbleHasImage(bubbles.get(i))) {
                    return bubbles.get(i);
                }
            }
            sleep(200);
            bubbles = messagesContainer().$$(elements.getMessageBubble()).filterBy(visible);
        }
        return findLastMessageBubbleWithChild(elements.getImagesMessage(), searchTimeout);
    }

    private boolean bubbleHasImage(SelenideElement bubble) {
        SelenideElement img = bubble.$(elements.getImagesMessage());
        if (img.exists() && img.isDisplayed()) {
            return true;
        }
        SelenideElement wrapper = bubble.$(elements.getImageMessageWrapper());
        return wrapper.exists() && wrapper.isDisplayed();
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    @Step("Проверить, что в системном чате отображается сообщение об успешном экспорте чата '{chatName}' с архивом 'Messages.zip'")
    public ChatVerify verifyExportMessageAndArchiveDisplayed(String chatName) {
        SelenideElement messageRow = findExportArchiveMessage(chatName);

        findArchiveInMessage(messageRow)
                .shouldHave(Condition.text("ssages.zip"))
                .shouldBe(Condition.visible);

        return this;
    }

    private SelenideElement findExportArchiveMessage(String chatName) {
        String expectedMessagePart = String.format("Экспорт «%s» завершен", chatName);
        return $$(elements.getMessageBubble())
                .findBy(Condition.text(expectedMessagePart))
                .shouldBe(Condition.visible, timeout);
    }

    private SelenideElement findArchiveInMessage(SelenideElement messageRow) {
        return messageRow.$(elements.getFileName());
    }

    @Step("Проверить, что сообщение с текстом '{messageText}' отсутствует в чате")
    public ChatVerify verifyMessageWithTextIsNotDisplayed(String messageText, int timeoutSeconds) {
        $(elements.getContainerWithMessages())
                .$$("div")
                .findBy(text(messageText))
                .shouldNotBe(visible, Duration.ofSeconds(timeoutSeconds));
        return this;
    }

    @Step("Проверить, что сообщения с текстом '{messageTexts}' отсутствуют в чате")
    public ChatVerify verifyMessagesWithTextAreNotDisplayed(String... messageTexts) {
        for (String messageText : messageTexts) {
            $(elements.getContainerWithMessages())
                    .$$("div")
                    .findBy(text(messageText))
                    .shouldNotBe(visible, timeout);
        }
        return this;
    }

    @Step("Проверить, что в сообщении '{text}' есть жирный текст")
    public ChatVerify verifyBoldInMessage(String text) {
        bubbleWithText(text)
                .shouldBe(visible, timeout)
                .$(elements.getBoldText())
                .shouldHave(text(text))
                .shouldBe(visible, timeout);
        return this;
    }

    @Step("Проверить, что в сообщении '{text}' есть подчеркнутый текст")
    public ChatVerify verifyUnderlinedInMessage(String text) {
        bubbleWithText(text)
                .shouldBe(visible, timeout)
                .$(elements.getUnderlinedText())
                .shouldHave(text(text))
                .shouldBe(visible, timeout);
        return this;
    }

    @Step("Проверить, что в сообщении '{text}' нет подчеркнутого текста")
    public ChatVerify verifyUnderlinedAbsentInMessage(String text) {
        bubbleWithText(text)
                .shouldBe(visible, timeout)
                .$$(elements.getUnderlinedText())
                .filterBy(text(text))
                .shouldHave(size(0), timeout);
        return this;
    }

    @Step("Проверить, что в сообщении '{text}' есть курсивный текст")
    public ChatVerify verifyItalicInMessage(String text) {
        bubbleWithText(text)
                .shouldBe(visible, timeout)
                .$(elements.getItalicText())
                .shouldHave(text(text))
                .shouldBe(visible, timeout);
        return this;
    }

    @Step("Проверить, что в сообщении '{text}' есть зачёркнутый текст")
    public ChatVerify verifyStrikethroughInMessage(String text) {
        bubbleWithText(text)
                .shouldBe(visible, timeout)
                .$(elements.getStrikethroughText())
                .shouldHave(text(text))
                .shouldBe(visible, timeout);
        return this;
    }

    @Step("Проверить, что в сообщении '{text}' нет зачёркнутого текста")
    public ChatVerify verifyStrikethroughAbsentInMessage(String text) {
        bubbleWithText(text)
                .shouldBe(visible, timeout)
                .$$(elements.getStrikethroughText())
                .filterBy(text(text))
                .shouldHave(size(0), timeout);
        return this;
    }

    private boolean messageHasStrikethrough(SelenideElement messageText, String expectedText, String strikeSelector) {
        if (!messageText.$$(elements.getStrikethroughText()).filterBy(text(expectedText)).isEmpty()) {
            return true;
        }

        if (elementOrAncestorsHaveLineThrough(messageText, 8)) {
            return true;
        }

        for (SelenideElement candidate : messageText.$$("*").filterBy(text(expectedText)).asFixedIterable()) {
            if (elementOrAncestorsHaveLineThrough(candidate, 8)) {
                return true;
            }
        }
        return false;
    }

    private boolean elementOrAncestorsHaveLineThrough(SelenideElement element, int maxAncestorHops) {
        SelenideElement current = element;
        for (int i = 0; i <= maxAncestorHops; i++) {
            if (current == null) {
                return false;
            }
            String decorationLine = current.getCssValue("text-decoration-line");
            String decoration = (decorationLine == null || decorationLine.isBlank())
                    ? current.getCssValue("text-decoration")
                    : decorationLine;
            if (decoration != null && decoration.contains("line-through")) {
                return true;
            }
            try {
                current = current.$x("./parent::*");
            } catch (RuntimeException ex) {
                return false;
            }
        }
        return false;
    }

    @Step("Проверить, что в сообщении '{text}' есть моноширинный текст")
    public ChatVerify verifyMonospaceInMessage(String text) {
        bubbleWithText(text)
                .shouldBe(visible, timeout)
                .$(elements.getMonospaceText())
                .shouldHave(text(text))
                .shouldBe(visible, timeout);
        return this;
    }

    @Step("Проверить, что к тексту '{expectedText}' в сообщении применены все стили")
    public ChatVerify verifyAllStylesInMessage(String expectedText) {
        SelenideElement message = bubbleWithText(expectedText)
                .shouldBe(visible, timeout)
                .shouldHave(text(expectedText));

        verifyAllStylesApplied(message, expectedText);

        return this;
    }

    private void verifyAllStylesApplied(SelenideElement container, String expectedText) {
        container.$$(elements.getBoldText())
                .findBy(text(expectedText))
                .shouldBe(visible, timeout);
        container.$$(elements.getItalicText())
                .findBy(text(expectedText))
                .shouldBe(visible, timeout);
        container.$$(elements.getUnderlinedText())
                .findBy(text(expectedText))
                .shouldBe(visible, timeout);
        container.$$(elements.getStrikethroughText())
                .findBy(text(expectedText))
                .shouldBe(visible, timeout);
        container.$$(elements.getMonospaceText())
                .findBy(text(expectedText))
                .shouldBe(visible, timeout);
    }


    @Step("Проверить наличие эмодзи в последнем сообщении")
    public ChatVerify verifyEmojiSent() {
        $(elements.getContainerWithMessages())
                .$$(elements.getMessageBubble())
                .last()
                .$(elements.getEmojiMessage())
                .shouldBe(visible, timeout);
        return this;
    }

    @Step("Проверить наличие картинки в последнем сообщении")
    public ChatVerify verifyImagesSent() {
        SelenideElement bubbleWithImage = findLastMessageBubbleWithImage(timeout);
        SelenideElement img = bubbleWithImage.$(elements.getImagesMessage());
        if (img.exists() && img.isDisplayed()) {
            img.shouldBe(visible, timeout);
        } else {
            bubbleWithImage.$(elements.getImageMessageWrapper()).shouldBe(visible, timeout);
        }
        return this;
    }

    @Step("Проверить наличие видео в последнем сообщении")
    public ChatVerify verifyVideoSent() {
        Duration videoTimeout = Duration.ofSeconds(90);
        SelenideElement bubbleWithVideo = findLastMessageBubbleWithChild(elements.getVideoMessage(), videoTimeout);
        bubbleWithVideo.$(elements.getVideoMessage()).shouldBe(visible, videoTimeout);
        bubbleWithVideo.$(elements.getImagesMessage()).shouldBe(visible, Condition.image);
        return this;
    }

    @Step("Проверить наличие файла в последнем сообщении")
    public ChatVerify verifyFileSent() {
        findLastMessageBubbleWithChild(elements.getFileMessage(), timeout)
                .$(elements.getFileMessage())
                .shouldBe(visible, timeout);
        return this;
    }

    @Step("Проверить, что в чате отображается {expectedCount} сообщений")
    public ChatVerify verifyMessagesCount(int expectedCount) {
        $(elements.getContainerWithMessages())
                .$$(elements.getMessageBubble())
                .filterBy(visible)
                .shouldHave(size(expectedCount), timeout);
        return this;
    }

    @Step("Проверить, что последние {count} сообщений в чате содержат файл")
    public ChatVerify verifyLastMessagesAreFiles(int count) {
        SelenideElement chat = $(elements.getContainerWithMessages()).shouldBe(visible, timeout);
        var messageBubbles = chat.$$(elements.getMessageBubble()).filterBy(visible);
        for (int i = messageBubbles.size() - count; i < messageBubbles.size(); i++) {
            messageBubbles.get(i).$(elements.getFileMessage()).shouldBe(visible, timeout);
        }
        return this;
    }

    @Step("Проверить наличие мультимедиа в последнем сообщении")
    public ChatVerify verifyMultimediaSent() {
        $(elements.getContainerWithMessages())
                .$(elements.getFileMessage())
                .shouldBe(visible, timeout);

        $(elements.getContainerWithMessages())
                .$(elements.getImagesMessage())
                .shouldBe(visible, timeout);

        $(elements.getContainerWithMessages())
                .$(elements.getVideoMessage())
                .shouldBe(visible, timeout);

        return this;
    }

    @Step("Дождаться завершения загрузки медиа в чате")
    public ChatVerify waitForMediaUploadToComplete() {
        Duration uploadTimeout = Duration.ofSeconds(90);
        SelenideElement chat = messagesContainer();

        chat.$$(elements.getMediaUploadProgress())
                .filter(visible)
                .shouldHave(sizeGreaterThan(0));
        chat.$$(elements.getMediaUploadProgress())
                .filter(visible)
                .shouldBe(CollectionCondition.empty, uploadTimeout);

        return this;
    }


    @Step("Проверить, что альбом с {expectedImages} изображениями и {expectedVideos} видео отправлен в чат")
    public ChatVerify verifyAlbumSent(int expectedImages, int expectedVideos) {
        Duration timeout = Duration.ofSeconds(90);

        SelenideElement lastBubble = $(elements.getContainerWithMessages())
                .$$(elements.getMessageBubble()).last().shouldBe(visible, timeout);

        //Проверяем точечно количество элементов
        lastBubble.$$(elements.getAlbumImageItem()).shouldHave(size(expectedImages), timeout);
        lastBubble.$$(elements.getVideoMessage()).shouldHave(size(expectedVideos), timeout);

        //Проверяем общую сетку и физическую загрузку картинок средствами Selenide
        lastBubble.$$(elements.getAlbumGridImage())
                .shouldHave(size(expectedImages + expectedVideos), timeout)
                .forEach(img -> img.shouldBe(visible, Condition.image));

        return this;
    }


    @Step("Проверить, что последнее сообщение отображается в чате")
    public ChatVerify verifyMessageSent() {
        $(elements.getContainerWithMessages())
                .$$(elements.getMessageBubble())
                .last()
                .shouldBe(visible, timeout);
        return this;
    }

    @Step("Проверить, что сообщение с сохранённым текстом отображается в чате")
    public ChatVerify verifySaveMessageSent() {
        $(elements.getContainerWithMessages())
                .$$("div")
                .findBy(text(sentMessageText))
                .shouldBe(visible, timeout);
        return this;
    }

    private void findMessageByText(String messageText) {
        bubbleWithText(messageText)
                .shouldBe(visible, Duration.ofSeconds(15))
                .shouldHave(text(messageText));
    }

    private SelenideElement bubbleWithText(String text) {
        return $(elements.getContainerWithMessages())
                .$$(elements.getMessageBubble())
                .findBy(text(text));
    }

    @Step("Проверить отсутствие поля ввода и кнопки отправки сообщения")
    public ChatVerify verifyMessageInputAndSendButtonAbsence() {
        SelenideElement inputField = $(inputBar.getMessageField());
        inputField.shouldBe(not(visible), Duration.ofSeconds(10));
        SelenideElement sendButton = $(inputBar.getMessageSendButton());
        sendButton.shouldBe(not(visible), Duration.ofSeconds(10));
        return this;
    }

    @Step("Проверить, что карточка контакта с именем '{contactName}' отображается в чате")
    public ChatVerify verifyContactSent(String contactName) {
        $(elements.getContainerWithMessages())
                .$$(elements.getMessageBubble())
                .last()
                .$(elements.getContactCard())
                .shouldBe(visible, timeout)
                .shouldHave(text(contactName));
        return this;
    }

    @Step("Проверить, что упоминание пользователя '{contactName}' отправлено в сообщении")
    public ChatVerify verifyMentionSent(String contactName) {
        $(elements.getContainerWithMessages())
                .$$(elements.getMessageBubble())
                .last()
                .$(elements.getMention())
                .shouldBe(visible, timeout)
                .shouldHave(text(contactName));
        return this;
    }

    @Step("Проверить, что упоминания '{mentionNames}' отправлены в сообщении")
    public ChatVerify verifyMentionSent(String... mentionNames) {
        SelenideElement lastMessageBubble = $(elements.getContainerWithMessages())
                .$$(elements.getMessageBubble())
                .last()
                .shouldBe(visible, Duration.ofSeconds(10));
        for (String mentionName : mentionNames) {
            lastMessageBubble
                    .$$(elements.getMention())
                    .findBy(text(mentionName))
                    .shouldBe(visible, Duration.ofSeconds(10));
        }
        return this;
    }

    @Step("Проверить, что реплай с текстом '{replyText}' отправлен")
    public ChatVerify verifyReplyMessageSent(String replyText) {
        $(elements.getContainerWithMessages())
                .$$(elements.getReplyMessageText())
                .findBy(text(replyText))
                .shouldBe(visible, Duration.ofSeconds(10))
                .shouldHave(text(replyText));
        return this;
    }

    @Step("Проверить, что кнопка контекстного меню по локатору '{buttonLocator}' отсутствует")
    public ChatVerify verifyContextMenuButtonAbsent(By buttonLocator) {
        try {
            if ($(buttonLocator).exists()) {
                $(buttonLocator).shouldBe(not(visible), Duration.ofSeconds(2));
            }
        } catch (Exception e) {
        }
        return this;
    }

    @Step("Проверить, что кнопка контекстного меню по локатору '{buttonLocator}' отображается")
    public ChatVerify verifyContextMenuButtonVisible(By buttonLocator) {
        $(buttonLocator)
                .shouldBe(visible, Duration.ofSeconds(10));
        return this;
    }

    @Step("Проверить, что сообщение с текстом '{expectedText}' отображается в чате")
    public ChatVerify verifyMessageText(String expectedText) {
        findMessageByText(expectedText);
        return this;
    }

    @Step("Проверить, что сообщение '{expectedText}' содержит обычную часть '{plainText}' и зачёркнутую часть '{strikethroughText}'")
    public ChatVerify verifyPartlyStrikethroughTextInLastMessage(String expectedText, String plainText, String strikethroughText) {
        SelenideElement message = bubbleWithText(expectedText)
                .shouldBe(visible, timeout);

        message.shouldHave(text(expectedText));

        message.$$(elements.getStrikethroughText())
                .findBy(exactText(strikethroughText))
                .shouldBe(visible);

        message.$$(elements.getStrikethroughText())
                .filterBy(exactText(plainText))
                .shouldHave(size(0));

        return this;
    }

    @Step("Проверить, что сообщение '{expectedText}' — отображается как ссылка")
    public ChatVerify verifyMessageLinkText(String expectedText) {
        $(elements.getContainerWithMessages()).$$(elements.getMessageBubble())
                .findBy(text(expectedText)).$(elements.getMessageLink()).shouldHave(text(expectedText));
        return this;
    }

    @Step("Проверить, что текст '{expectedText}' в сообщении не является ссылкой")
    public ChatVerify verifyMessageTextIsNotLink(String expectedText) {
        $(elements.getContainerWithMessages()).$$(elements.getMessageBubble())
                .findBy(text(expectedText)).$$(elements.getMessageLink())
                .filterBy(text(expectedText))
                .shouldHave(CollectionCondition.size(0));
        return this;
    }

    @Step("Проверить, что messageStatusSeen отображается у сообщения '{text}'")
    public ChatVerify verifyMessageStatusSeen(String text) {
        $(elements.getContainerWithMessages())
                .$$("div")
                .findBy(text(text))
                .$(elements.getMessageStatusSeen())
                .shouldBe(visible);
        return this;
    }


    @Step("Проверить, что сообщение с текстом '{expectedText}' отображается в чате (таймаут: {timeoutSeconds} сек)")
    public ChatVerify verifyMessageText(String expectedText, int timeoutSeconds) {
        $(elements.getContainerWithMessages())
                .$$("div")
                .findBy(text(expectedText))
                .shouldBe(visible, Duration.ofSeconds(timeoutSeconds));
        return this;
    }

    @Step("Проверить, что сообщение с ID '{messageId}' содержит текст '{expectedText}'")
    public ChatVerify verifyMessageTextById(String messageId, String expectedText) {
        $(elements.getContainerWithMessages())
                .$(elements.getMessageBubbleById(messageId))
                .shouldBe(visible, Duration.ofSeconds(10))
                .shouldHave(text(expectedText));
        return this;
    }

    @Step("Проверить видимость сообщения с ID '{messageId}' (ожидается: {visible})")
    public ChatVerify verifyMessageWithIdIsDisplayed(String messageId, boolean visible) {
        SelenideElement messageElement = $(elements.getMessageBubbleById(messageId));
        if (visible) {
            messageElement.shouldBe(Condition.visible, Duration.ofSeconds(10));
        } else {
            messageElement.shouldNotBe(Condition.visible, Duration.ofSeconds(10));
        }
        return this;
    }

    @Step("Проверить подсветку сообщения {messageId} после перехода из меню закрепленных сообщений")
    public ChatVerify verifyMessageHighlightAnimation(String messageId) {
        $(elements.getMessageBubbleById(messageId))
                .shouldHave(cssClass("selected"), Duration.ofSeconds(3));
        return this;
    }

    @Step("Проверить подсветку сообщений с текстом '{texts}'")
    public ChatVerify verifyMessageHighlightAnimationByText(String... texts) {
        for (String text : texts) {
            $$(elements.getMessageBubble())
                    .findBy(text(text))
                    .shouldHave(cssClass("selected"), Duration.ofSeconds(3));
        }
        return this;
    }

    @Step("Проверить наличие пиктограммы закрепленного сообщения в баббле с ID '{messageId}'")
    public ChatVerify verifyPinnedMessagePictogramPresentById(String messageId) {
        SelenideElement messageBubble = $(elements.getContainerWithMessages())
                .$(elements.getMessageBubbleById(messageId))
                .shouldBe(visible, Duration.ofSeconds(10));
        messageBubble.$(elements.getPinnedMessagePictogram())
                .shouldBe(visible, Duration.ofSeconds(10));
        return this;
    }

    @Step("Проверить отсутствие пиктограммы закрепленного сообщения в баббле с ID '{messageId}'")
    public ChatVerify verifyPinnedMessagePictogramAbsentById(String messageId) {
        SelenideElement messageBubble = $(elements.getContainerWithMessages())
                .$(elements.getMessageBubbleById(messageId))
                .shouldBe(visible, Duration.ofSeconds(10));
        messageBubble.$(elements.getPinnedMessagePictogram())
                .shouldBe(not(visible), Duration.ofSeconds(10));
        return this;
    }

    @Step("Проверить системное сообщение типа '{type}' с цитатой '{messageText}'")
    public ChatVerify verifySystemMessage(SystemMessageType type, String messageText) {
        By locator;
        switch (type) {
            case UNPINNED:
                locator = systemMessagesElements.getUnpinnedSystemMessage();
                break;
            case DELETED:
                locator = systemMessagesElements.getDeletedSystemMessage();
                break;
            case PINNED:
            default:
                locator = systemMessagesElements.getPinnedSystemMessage();
                break;
        }
        return verifySystemMessage(locator, messageText);
    }

    private ChatVerify verifySystemMessage(By systemMessageLocator, String messageText) {
        $(elements.getContainerWithMessages())
                .$$(systemMessageLocator)
                .findBy(text(messageText))
                .shouldBe(visible, Duration.ofSeconds(10))
                .shouldHave(text(messageText));
        return this;
    }

    @Step("Проверить, что ссылка открылась в новой вкладке браузера с URL '{expectedUrl}'")
    public ChatVerify verifyLinkOpensInNewTab(String expectedUrl) {
        linksInBrowser.verifyLinkOpensInNewTab(expectedUrl);
        return this;
    }

    @Step("Проверить, что реакция '{emoji}' отображается в баббле сообщения с текстом '{messageText}'")
    public ChatVerify verifyReactionPresentInMessageBubble(String messageText, String emoji) {
        SelenideElement messageBubble = $(elements.getContainerWithMessages())
                .$$(elements.getMessageBubble())
                .findBy(text(messageText))
                .shouldBe(visible, Duration.ofSeconds(10));
        SelenideElement messageRoot = messageBubble.$(elements.getMessageRoot());
        messageRoot.$(By.cssSelector(elements.getMessageReaction(emoji)))
                .shouldBe(visible, Duration.ofSeconds(10));
        return this;
    }

    @Step("Проверить, что реакция '{emoji}' отображается в баббле сообщения с ID '{messageId}'")
    public ChatVerify verifyReactionPresentInMessageBubbleById(String messageId, String emoji) {
        SelenideElement messageBubble = $(elements.getContainerWithMessages())
                .$(elements.getMessageBubbleById(messageId))
                .shouldBe(visible, Duration.ofSeconds(10));
        SelenideElement messageRoot = messageBubble.$(elements.getMessageRoot());
        messageRoot.$(By.cssSelector(elements.getMessageReaction(emoji)))
                .shouldBe(visible, Duration.ofSeconds(10));
        return this;
    }

    @Step("Проверить, что счетчик реакции '{emoji}' в сообщении с ID '{messageId}' равен '{expectedCount}'")
    public ChatVerify verifyReactionCountInMessageBubbleById(String messageId, String emoji, int expectedCount) {
        SelenideElement messageBubble = $(elements.getContainerWithMessages())
                .$(elements.getMessageBubbleById(messageId))
                .shouldBe(visible, Duration.ofSeconds(10));
        SelenideElement messageRoot = messageBubble.$(elements.getMessageRoot());

        SelenideElement reactionItem = messageRoot.$(By.cssSelector(elements.getMessageReaction(emoji)))
                .shouldBe(visible, Duration.ofSeconds(10));

        reactionItem.$(elements.getMessageReactionCount())
                .shouldBe(visible, Duration.ofSeconds(10))
                .shouldHave(text(String.valueOf(expectedCount)));
        return this;
    }

    @Step("Проверить, что тултип реакции '{emoji}' в сообщении с ID '{messageId}' содержит имена пользователей '{userName1}' и '{userName2}'")
    public ChatVerify verifyReactionTooltipContainsUserNames(String messageId, String emoji, String userName1, String userName2) {
        try {
            Thread.sleep(800);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        SelenideElement popover = $(elements.getReactionTooltip())
                .shouldBe(visible, Duration.ofSeconds(5));
        String tooltipText = popover.getText();
        if (tooltipText == null || tooltipText.isEmpty()) {
            throw new AssertionError("Popover for reaction tooltip found, but text is empty");
        }
        if (!tooltipText.contains(userName1) || !tooltipText.contains(userName2)) {
            throw new AssertionError(String.format(
                    "Popover text '%s' does not contain both user names '%s' and '%s'",
                    tooltipText, userName1, userName2
            ));
        }
        return this;
    }

    @Step("Проверить, что сообщение с ID '{messageId}' содержит тег")
    public ChatVerify verifyMessageHasTagById(String messageId) {
        SelenideElement messageBubble = $(elements.getContainerWithMessages())
                .$(elements.getMessageBubbleById(messageId))
                .shouldBe(visible, Duration.ofSeconds(10));
        messageBubble.$(elements.getMessageTagLabel())
                .shouldBe(visible, Duration.ofSeconds(10));
        return this;
    }

    @Step("Проверить, что сообщение с ID '{messageId}' не содержит тегов")
    public ChatVerify verifyMessageHasNoTagById(String messageId) {
        SelenideElement messageBubble = $(elements.getContainerWithMessages())
                .$(elements.getMessageBubbleById(messageId))
                .shouldBe(visible, Duration.ofSeconds(10));
        messageBubble.$$(elements.getMessageTagLabel())
                .shouldHave(CollectionCondition.size(0), Duration.ofSeconds(10));
        return this;
    }

    @Step("Проверить, что сообщение с ID '{messageId}' не содержит тег '{tagText}'")
    public ChatVerify verifyMessageHasNoTagByIdAndText(String messageId, String tagText) {
        SelenideElement messageBubble = $(elements.getContainerWithMessages())
                .$(elements.getMessageBubbleById(messageId))
                .shouldBe(visible, Duration.ofSeconds(10));
        messageBubble.$$(elements.getMessageTagLabel())
                .findBy(text(tagText))
                .shouldNot(exist, Duration.ofSeconds(10));
        return this;
    }

    @Step("Проверить, что последнее отправленное сообщение содержит тег")
    public ChatVerify verifyLastMessageHasTag() {
        SelenideElement lastMessageBubble = $(elements.getContainerWithMessages())
                .$$(elements.getMessageBubble())
                .last()
                .shouldBe(visible, Duration.ofSeconds(10));
        lastMessageBubble.$(elements.getMessageTagLabel())
                .shouldBe(visible, Duration.ofSeconds(10));
        return this;
    }

    @Step("Проверить видимость системного сообщения 'Запланировано на {label}'")
    public void verifyScheduledForDate(String label, boolean visible) {
        if (visible) {
            $(elements.getScheduledLabelByText(label)).shouldBe(Condition.visible);
        } else {
            $(elements.getScheduledLabelByText(label)).shouldNotBe(Condition.visible);
        }
    }

    @Step("Проверить отсутствие кнопки отложенных сообщений")
    public void verifyScheduledMessagesButtonNotVisible() {
        $(elements.getScheduledMessagesButton()).shouldNotBe(visible);
    }

    @Step("Проверить, что чат пустой")
    public ChatVerify verifyChatIsEmpty() {
        $(elements.getContainerWithMessages())
                .$$(elements.getMessageBubble())
                .shouldBe(CollectionCondition.empty, timeout);
        return this;
    }

    @Step("Проверить порядок имен контактов: {expectedNames}")
    public ChatVerify verifyContactCardsOrder(String... expectedNames) {
        $(elements.getContainerWithMessages())
                .$$(elements.getContactNameInCard())
                .last(expectedNames.length)
                .shouldHave(texts(expectedNames));
        return this;
    }

    @Step("Проверить наличие карточки контакта с именем: {expectedName}")
    public ChatVerify verifyContactCardVisible(String expectedName) {
        $(elements.getContainerWithMessages())
                .$$(elements.getContactNameInCard())
                .find(text(expectedName))
                .shouldBe(visible);
        return this;
    }

    @Step("Проверить  системное сообщение о включении автоудаления")
    public ChatVerify verifyAutodeleteSystemMessages() {
        $$(systemMessagesElements.getAutoDeletedSystemMessage())
                .last()
                .shouldBe(visible)
                .shouldHave(text("Вы включили автоудаление сообщений со сроком"));
        return this;
    }

    @Step("Проверить  системное сообщение об отключении автоудаления")
    public ChatVerify verifyAutodeleteDisabledSystemMessage() {
        $$(systemMessagesElements.getAutoDeletedSystemMessage())
                .last()
                .shouldBe(visible)
                .shouldHave(text("Вы отключили настройку автоудаления сообщений"));
        return this;
    }

    @Step("Проверить, что сообщение с текстом '{messageText}' прочитано")
    public ChatVerify verifyMessageRead(String messageText) {
        $$(elements.getContainerWithMessages())
                .findBy(text(messageText))
                .find(elements.getMessageStatusSeen())
                .shouldBe(visible, timeout);
        return this;
    }
}
