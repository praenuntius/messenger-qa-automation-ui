package org.foxtrot.pages.chat.chat;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import io.qameta.allure.Step;
import org.foxtrot.pages.chat.chat.elements.Chat;
import org.foxtrot.pages.chat.input_bar.InputBarSteps;
import org.foxtrot.pages.chat.input_bar.elements.AttachContextMenu;
import org.foxtrot.pages.chat.input_bar.elements.InputBar;
import org.foxtrot.utils.FileUploadHelper;
import org.foxtrot.utils.MessageUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.interactions.Actions;

import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

public class ChatActions {

    private final Chat chatElements = new Chat();
    private final AttachContextMenu attachContextMenu = new AttachContextMenu();
    private final InputBar inputBar = new InputBar();
    private final InputBarSteps inputBarSteps = new InputBarSteps();
    private final FileUploadHelper fileUploadHelper = new FileUploadHelper();
    private String sentMessageText;

    @Step("Отправить сообщение в чат кнопкой [Enter]")
    public ChatActions sendMessageWithEnterKey() {
        actions().sendKeys(org.openqa.selenium.Keys.ENTER).perform();
        return this;
    }

    @Step("Отправить текстовое сообщение: '{messageText}'")
    public ChatActions sendTextMessage(String messageText) {
        SelenideElement inputField = $(inputBar.getMessageField())
                .shouldBe(visible, Duration.ofSeconds(10));
        new Actions(WebDriverRunner.getWebDriver())
                .moveToElement(inputField)
                .click()
                .sendKeys(messageText)
                .perform();
        $(inputBar.getMessageSendButton())
                .shouldBe(visible, Duration.ofSeconds(10))
                .click();
        return this;
    }

    @Step("Отправлять сообщения до появления хедера комментируемого поста (max={maxMessages}, randomText={useRandomText})")
    public ChatActions sendTextMessagesUntilCommentedPostHeaderAppears(int maxMessages, boolean useRandomText) {
        return sendTextMessagesUntilCommentedPostHeaderAppears(maxMessages, useRandomText, null);
    }

    @Step("Отправлять сообщения до появления хедера комментируемого поста (max={maxMessages}, postText='{expectedPostMessageText}')")
    public ChatActions sendTextMessagesUntilCommentedPostHeaderAppears(
            int maxMessages, boolean useRandomText, String expectedPostMessageText) {
        for (int i = 0; i < maxMessages; i++) {
            if (isCommentedPostHeaderVisibleStable(expectedPostMessageText)) {
                return this;
            }

            String messageText = useRandomText
                    ? MessageUtil.getRandomMessage(1, 20)
                    : "1";

            sendTextMessage(messageText);

            // small UI settle time
            sleep(300);
            if (waitForCommentedPostHeaderVisibleStable(expectedPostMessageText, Duration.ofSeconds(2))) {
                return this;
            }
        }

        if (waitForCommentedPostHeaderVisibleStable(expectedPostMessageText, Duration.ofSeconds(5))) {
            return this;
        }
        throw new AssertionError("Header of commented post did not appear after sending " + maxMessages + " messages");
    }

    @Step("Отправлять сообщения до появления хедера комментируемого поста (max={maxMessages})")
    public ChatActions sendTextMessagesUntilCommentedPostHeaderAppears(int maxMessages) {
        return sendTextMessagesUntilCommentedPostHeaderAppears(maxMessages, true);
    }

    @Step("Отправлять сообщения до появления хедера комментируемого поста (max={maxMessages}, postText='{expectedPostMessageText}')")
    public ChatActions sendTextMessagesUntilCommentedPostHeaderAppears(int maxMessages, String expectedPostMessageText) {
        return sendTextMessagesUntilCommentedPostHeaderAppears(maxMessages, true, expectedPostMessageText);
    }

    @Step("Отправлять изображения до появления хедера комментируемого поста (max={maxCount}, postText='{expectedPostMessageText}')")
    public ChatActions sendImagesUntilCommentedPostHeaderAppears(int maxCount, String expectedPostMessageText) {
        return sendImagesUntilCommentedPostHeaderAppears(maxCount, expectedPostMessageText, "images/tinyJPG.jpg");
    }

    @Step("Отправлять изображения '{imagePath}' до появления хедера комментируемого поста (max={maxCount}, postText='{expectedPostMessageText}')")
    public ChatActions sendImagesUntilCommentedPostHeaderAppears(
            int maxCount, String expectedPostMessageText, String imagePath) {
        for (int i = 0; i < maxCount; i++) {
            if (isCommentedPostHeaderVisibleStable(expectedPostMessageText)) {
                return this;
            }

            sendImageMessage(imagePath);

            sleep(300);
            if (waitForCommentedPostHeaderVisibleStable(expectedPostMessageText, Duration.ofSeconds(2))) {
                return this;
            }
        }

        if (waitForCommentedPostHeaderVisibleStable(expectedPostMessageText, Duration.ofSeconds(5))) {
            return this;
        }
        throw new AssertionError("Header of commented post did not appear after sending " + maxCount + " images");
    }

    @Step("Отправить изображение '{imagePath}'")
    public ChatActions sendImageMessage(String imagePath) {
        inputBarSteps.uploadMediaFileViaAttachMenu(imagePath);
        inputBarSteps.clickSendMessageButton();
        return this;
    }

    @Step("Отправить {count} случайных текстовых сообщений (minLen={minLength}, maxLen={maxLength})")
    public ChatActions textMessageSpam(int count, int minLength, int maxLength) {
        if (count <= 0) {
            return this;
        }
        for (int i = 0; i < count; i++) {
            inputBarSteps.clickMessageInputField();
            String messageText = MessageUtil.getRandomMessage(minLength, maxLength);
            inputBarSteps.enterMessage(messageText);
            inputBarSteps.clickSendMessageButton();
        }
        return this;
    }

    @Step("Отправить {count} случайных текстовых сообщений")
    public ChatActions textMessageSpam(int count) {
        return textMessageSpam(count, 1, 20);
    }

    @Step("Проскроллить чат вверх на {pixels}px")
    public ChatActions scrollChatUp(int pixels) {
        if (pixels <= 0) {
            return this;
        }
        SelenideElement scrollContainer = getChatScrollContainer();
        long before = getScrollTop(scrollContainer);

        executeJavaScript(
                "arguments[0].scrollTop = Math.max(0, arguments[0].scrollTop - arguments[1]);",
                scrollContainer,
                pixels
        );

        sleep(200);
        long after = getScrollTop(scrollContainer);
        if (after >= before) {
            throw new AssertionError("Chat did not scroll up. before=" + before + ", after=" + after);
        }
        return this;
    }

    @Step("Проскроллить вверх до появления хедера комментируемого поста (postText='{expectedPostMessageText}')")
    public ChatActions scrollUntilCommentedPostHeaderAppears(String expectedPostMessageText) {
        int scrollStepPx = 1000;
        int maxAttempts = 30;
        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            if (isCommentedPostHeaderVisibleStable(expectedPostMessageText)) {
                return this;
            }
            SelenideElement scrollContainer = getChatScrollContainer();
            long before = getScrollTop(scrollContainer);
            executeJavaScript(
                    "arguments[0].scrollTop = Math.max(0, arguments[0].scrollTop - arguments[1]);",
                    scrollContainer,
                    scrollStepPx
            );
            sleep(300);
            if (getScrollTop(scrollContainer) >= before) {
                break;
            }
        }
        if (!waitForCommentedPostHeaderVisible(expectedPostMessageText, Duration.ofSeconds(5))) {
            throw new AssertionError(
                    "Commented post header with text '" + expectedPostMessageText + "' did not appear after scrolling up"
            );
        }
        return this;
    }

    @Step("Кликнуть по кнопке скролла вниз")
    public ChatActions clickScrollDownButton() {
        SelenideElement btn;
        try {
            btn = $(chatElements.getScrollDownButton())
                    .shouldBe(visible, Duration.ofSeconds(4));
        } catch (Throwable firstAttemptFailed) {
            sleep(400);
            btn = $(chatElements.getScrollDownButton())
                    .shouldBe(visible, Duration.ofSeconds(8));
        }
        try {
            btn.click();
        } catch (Throwable e) {
            executeJavaScript("arguments[0].click();", btn.toWebElement());
        }
        return this;
    }

    @Step("Кликнуть по кнопке скролла вниз и проверить, что лента проскроллилась вниз")
    public ChatActions clickScrollDownButtonAndVerifyScrolledDown() {
        SelenideElement scrollContainer = getChatScrollContainer();
        long before = getScrollTop(scrollContainer);

        clickScrollDownButton();

        boolean scrolledDown = false;
        for (int i = 0; i < 25; i++) {
            sleep(120);
            long after = getScrollTop(scrollContainer);
            if (after > before) {
                scrolledDown = true;
                break;
            }
        }

        if (!scrolledDown) {
            long after = getScrollTop(scrollContainer);
            throw new AssertionError(
                    "Chat did not scroll down after clicking scroll-down button. before=" + before + ", after=" + after
            );
        }
        return this;
    }

    private boolean isCommentedPostHeaderVisible(String expectedPostMessageText) {
        return $$(chatElements.getCommentedPostHeader()).stream()
                .filter(SelenideElement::isDisplayed)
                .anyMatch(header -> commentedPostHeaderMatches(header, expectedPostMessageText));
    }

    private boolean commentedPostHeaderMatches(SelenideElement header, String expectedPostMessageText) {
        if (expectedPostMessageText == null || expectedPostMessageText.isBlank()) {
            return true;
        }
        SelenideElement messageSpan = header.$(chatElements.getCommentedPostHeaderMessageText());
        if (!messageSpan.isDisplayed()) {
            return false;
        }
        return expectedPostMessageText.equals(messageSpan.getText().trim());
    }

    private boolean isCommentedPostHeaderVisibleStable(String expectedPostMessageText) {
        return isCommentedPostHeaderVisibleStable(expectedPostMessageText, Duration.ofMillis(800));
    }

    /**
     * Sticky-плашка комментируемого поста рендерится в DOM только пока оригинальный пост проскроллен
     * за верх ленты. При авто-скролле после отправки сообщения она может мелькнуть на пару сотен
     * миллисекунд и снова исчезнуть (когда короткая лента устаканивается и пост возвращается в вид).
     * Поэтому считаем плашку появившейся, только если она видима НЕПРЕРЫВНО в течение окна stableFor —
     * иначе цикл остановится на транзиентном срабатывании, а к моменту клика плашки уже не будет.
     */
    private boolean isCommentedPostHeaderVisibleStable(String expectedPostMessageText, Duration stableFor) {
        if (!isCommentedPostHeaderVisible(expectedPostMessageText)) {
            return false;
        }
        long until = System.currentTimeMillis() + stableFor.toMillis();
        while (System.currentTimeMillis() < until) {
            sleep(100);
            if (!isCommentedPostHeaderVisible(expectedPostMessageText)) {
                return false;
            }
        }
        return true;
    }

    private boolean waitForCommentedPostHeaderVisible(String expectedPostMessageText, Duration timeout) {
        long deadline = System.currentTimeMillis() + timeout.toMillis();
        while (System.currentTimeMillis() < deadline) {
            if (isCommentedPostHeaderVisible(expectedPostMessageText)) {
                return true;
            }
            sleep(100);
        }
        return false;
    }

    private boolean waitForCommentedPostHeaderVisibleStable(String expectedPostMessageText, Duration timeout) {
        long deadline = System.currentTimeMillis() + timeout.toMillis();
        while (System.currentTimeMillis() < deadline) {
            if (isCommentedPostHeaderVisibleStable(expectedPostMessageText)) {
                return true;
            }
            sleep(100);
        }
        return false;
    }

    private boolean isAnyVisible(By locator) {
        return $$(locator).stream().anyMatch(SelenideElement::isDisplayed);
    }

    private SelenideElement getChatScrollContainer() {
        SelenideElement container = $(chatElements.getContainerWithChat())
                .shouldBe(visible, Duration.ofSeconds(10));
        if (isElementVerticallyScrollable(container)) {
            return container;
        }

        SelenideElement messages = $(chatElements.getContainerWithMessages())
                .shouldBe(visible, Duration.ofSeconds(10));
        return messages;
    }

    private boolean isElementVerticallyScrollable(SelenideElement element) {
        Object value = executeJavaScript("return arguments[0].scrollHeight > arguments[0].clientHeight;", element);
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        return Boolean.parseBoolean(String.valueOf(value));
    }

    private long getScrollTop(SelenideElement scrollContainer) {
        Object value = executeJavaScript("return arguments[0].scrollTop;", scrollContainer);
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return Long.parseLong(String.valueOf(value));
    }

    @Step("Проверить, что хедер комментируемого поста отображает автора '{expectedAuthorName}' и текст '{expectedPostText}'")
    public ChatActions verifyCommentedPostHeaderDisplays(String expectedAuthorName, String expectedPostText) {
        SelenideElement header = findVisibleCommentedPostHeader(Duration.ofSeconds(10), expectedPostText);
        header.$(chatElements.getCommentedPostHeaderAuthorTitle())
                .shouldHave(text(expectedAuthorName));
        header.$(chatElements.getCommentedPostHeaderMessageText())
                .shouldHave(text(expectedPostText));
        return this;
    }

    @Step("Кликнуть по хедеру комментируемого поста (полоска вверху)")
    public ChatActions clickCommentedPostHeader() {
        return clickCommentedPostHeader(null);
    }

    @Step("Кликнуть по хедеру комментируемого поста (полоска вверху, postText='{expectedPostMessageText}')")
    public ChatActions clickCommentedPostHeader(String expectedPostMessageText) {
        SelenideElement header = findVisibleCommentedPostHeader(Duration.ofSeconds(10), expectedPostMessageText);
        header.scrollIntoView(true);
        try {
            header.click();
        } catch (Throwable e) {
            executeJavaScript("arguments[0].click();", header.toWebElement());
        }

        if ($(chatElements.getCommentChatHeader()).isDisplayed()) {
            SelenideElement backButton = $(chatElements.getChannelCommentsBackButton())
                    .shouldBe(visible, Duration.ofSeconds(4));
            try {
                backButton.click();
            } catch (Throwable e) {
                executeJavaScript("arguments[0].click();", backButton.toWebElement());
            }
        }
        $(chatElements.getCommentChatHeader()).shouldBe(hidden, Duration.ofSeconds(10));
        return this;
    }

    private SelenideElement findVisibleCommentedPostHeader(Duration timeout) {
        return findVisibleCommentedPostHeader(timeout, null);
    }

    private SelenideElement findVisibleCommentedPostHeader(Duration timeout, String expectedPostMessageText) {
        long deadline = System.currentTimeMillis() + timeout.toMillis();
        while (System.currentTimeMillis() < deadline) {
            var visibleHeaders = $$(chatElements.getCommentedPostHeader()).stream()
                    .filter(SelenideElement::isDisplayed)
                    .toList();
            if (expectedPostMessageText != null && !expectedPostMessageText.isBlank()) {
                for (SelenideElement header : visibleHeaders) {
                    if (commentedPostHeaderMatches(header, expectedPostMessageText)) {
                        return header;
                    }
                }
            } else if (!visibleHeaders.isEmpty()) {
                return visibleHeaders.get(0);
            }
            sleep(100);
        }
        return $$(chatElements.getCommentedPostHeader())
                .findBy(visible)
                .shouldBe(visible, timeout);
    }

    @Step("Кликнуть по хедеру комментируемого поста и проверить навигацию к оригинальному сообщению '{originalPostMessageId}'")
    public ChatActions clickCommentedPostHeaderAndVerifyNavigatedToOriginalPost(String originalPostMessageId) {
        return clickCommentedPostHeaderAndVerifyNavigatedToOriginalPost(originalPostMessageId, null);
    }

    @Step("Кликнуть по хедеру комментируемого поста и проверить навигацию к оригинальному сообщению '{originalPostMessageId}' (postText='{expectedPostMessageText}')")
    public ChatActions clickCommentedPostHeaderAndVerifyNavigatedToOriginalPost(
            String originalPostMessageId,
            String expectedPostMessageText
    ) {
        clickCommentedPostHeader(expectedPostMessageText);
        sleep(500);
        $(chatElements.getCommentChatHeader()).shouldBe(hidden, Duration.ofSeconds(10));
        findMessageById(originalPostMessageId);
        return this;
    }

    @Step("Нажать кнопку '@ отметить'")
    public ChatActions clickMentionButton() {
        $(chatElements.getMentionButton()).shouldBe(visible).click();
        return this;
    }

    @Step("Открыть меню отложенных сообщений")
    public ChatActions clickScheduledMessagesMenu() {
        $(chatElements.getScheduledMessagesButton()).shouldBe(visible).click();
        return this;
    }

    @Step("Прокрутить ленту чата к сообщению '{messageText}'")
    public ChatActions findMessageByText(String messageText) {
        SelenideElement messageBubble = $(chatElements.getContainerWithMessages())
                .$$(chatElements.getMessageBubble())
                .findBy(text(messageText))
                .shouldBe(visible, Duration.ofSeconds(10));
        messageBubble.scrollIntoView(true);
        return this;
    }

    @Step("Открыть контекстное меню сообщения с текстом '{messageText}'")
    public ChatActions openMessageContextMenu(String messageText) {
        SelenideElement messageBubble = $(chatElements.getContainerWithMessages())
                .$$(chatElements.getMessageBubble())
                .findBy(text(messageText))
                .shouldBe(visible, Duration.ofSeconds(10));
        new Actions(WebDriverRunner.getWebDriver())
                .moveToElement(messageBubble)
                .contextClick()
                .perform();
        return this;
    }

    @Step("Прокрутить ленту чата к сообщению с ID '{messageId}'")
    public ChatActions findMessageById(String messageId) {
        SelenideElement messageBubble = $(chatElements.getContainerWithMessages())
                .$(chatElements.getMessageBubbleById(messageId))
                .shouldBe(visible, Duration.ofSeconds(10));
        messageBubble.scrollIntoView(true);
        return this;
    }

    @Step("Открыть контекстное меню сообщения с ID '{messageId}'")
    public ChatActions openMessageContextMenuById(String messageId) {
        SelenideElement messageBubble = $(chatElements.getContainerWithMessages())
                .$(chatElements.getMessageBubbleById(messageId))
                .shouldBe(visible, Duration.ofSeconds(10));
        messageBubble.scrollIntoView(true);
        new Actions(WebDriverRunner.getWebDriver())
                .moveToElement(messageBubble)
                .contextClick()
                .perform();
        return this;
    }

    @Step("Открыть комментарии сообщения с ID '{messageId}'")
    public ChatActions openMessageCommentById(long messageId) {
        SelenideElement messageBubble = $(chatElements.getContainerWithMessages())
                .$(chatElements.getCommentsButtonByPostId(messageId))
                .shouldBe(visible);
        messageBubble.click();
        return this;
    }

    @Step("Кликнуть по сообщению с ID '{messageId}'")
    public ChatActions clickMessageById(Long messageId) {
        $(chatElements.getMessageBubbleByIdSecond(String.valueOf(messageId))).shouldBe(visible).click();
        return this;
    }

    @Step("Кликнуть по ссылке в сообщении с ID '{messageId}'")
    public ChatActions clickOnLinkInMessageById(Long messageId) {
        $(chatElements.getMessageBubbleByIdSecond(String.valueOf(messageId)))
                .shouldBe(visible)
                .$(chatElements.getMessageLink())
                .shouldBe(visible)
                .click();
        return this;
    }

    @Step("Кликнуть по сообщению c ссылкой '{text}'")
    public ChatActions findAndClickLinkMessageByText(String text) {
        $(chatElements.getMessageLinkByText(text))
                .shouldBe(visible)
                .click();
        return this;
    }

    @Step("Кликнуть по Медиа сообщению  с ID '{messageId}'")
    public ChatActions clickMediaMessageById(String messageId) {
        SelenideElement messageBubble = $(chatElements.getContainerWithMessages())
                .$(chatElements.getMediaMessageBubbleById(messageId))
                .shouldBe(visible);
        messageBubble.scrollIntoView(true);
        try {
            messageBubble.click();
        } catch (Throwable e) {
            executeJavaScript("arguments[0].click();", messageBubble.toWebElement());
        }
        return this;
    }

    @Step("Перейти по реплаю с ID '{messageId}' к оригиналу сообщения")
    public ChatActions clickReplyInsideMessageById(String messageId) {
        $(chatElements.getReplayMessageBubbleById(messageId)).shouldBe(visible).click();
        return this;
    }

    @Step("Перейти по меншену в реплае с ID '{messageId}' к оригиналу сообщения")
    public ChatActions clickMentionInReplyById(String messageId) {
        $(chatElements.getReplayMessageBubbleById(messageId)).$(chatElements.getMessageLink()).shouldBe(visible).click();
        return this;
    }

    @Step("Перейти по реплаю с текстом '{message}' к оригиналу сообщения")
    public ChatActions clickReplyInsideMessageByText(String message) {
        $(chatElements.getReplayMessageBubbleByText())
                .shouldHave(text(message))
                .click();
        return this;
    }

    @Step("Кликнуть по тексту отправленного сообщения в баббле (для отформатированного текста со ссылкой)")
    public ChatActions clickLinkFormattedMessageText() {
        SelenideElement lastMessageBubble = $(chatElements.getContainerWithMessages())
                .$$(chatElements.getMessageBubble())
                .last()
                .shouldBe(visible, Duration.ofSeconds(10));

        lastMessageBubble
                .$$(chatElements.getFormattedTextNodes())
                .findBy(text(sentMessageText))
                .shouldBe(visible, Duration.ofSeconds(10))
                .click();
        return this;
    }

    @Step("Кликнуть по архиву экспорта истории чата '{chatName}'")
    public ChatActions clickChatHistoryExportArchive(String chatName) {
        String expectedMessagePart = String.format("Экспорт «%s» завершен", chatName);
        SelenideElement messageRow = $$(chatElements.getMessageBubble())
                .findBy(text(expectedMessagePart))
                .shouldBe(visible, Duration.ofSeconds(30));

        messageRow.$(chatElements.getFileName())
                .shouldBe(visible, Duration.ofSeconds(30))
                .click();
        return this;
    }

    @Step("Выполнить Drag and Drop медиафайла '{filePath}' в поле ввода сообщения")
    public ChatActions dragAndDropMediaFile(String filePath) {
        SelenideElement messageField = $(inputBar.getMessageField())
                .shouldBe(visible, Duration.ofSeconds(10));
        fileUploadHelper.dragAndDropFile(messageField, filePath);
        return this;
    }

    @Step("Загрузить медиафайл '{filePath}' через скрытый input элемент")
    public ChatActions uploadMediaFileViaInput(String filePath) {
        SelenideElement fileInput = $(attachContextMenu.getFileInput())
                .shouldBe(visible, Duration.ofSeconds(10));
        fileUploadHelper.uploadFileViaSelenideElement(fileInput, filePath);
        return this;
    }

    @Step("Открыть контекстное меню тега в сообщении с ID '{messageId}'")
    public ChatActions openTagContextMenuById(String messageId) {
        SelenideElement messageBubble = $(chatElements.getContainerWithMessages())
                .$(chatElements.getMessageBubbleById(messageId))
                .shouldBe(visible, Duration.ofSeconds(10));
        messageBubble.scrollIntoView(true);
        SelenideElement tagElement = messageBubble.$(chatElements.getMessageTagLabel())
                .shouldBe(visible, Duration.ofSeconds(10));
        try {
            new Actions(WebDriverRunner.getWebDriver())
                    .moveToElement(tagElement)
                    .contextClick()
                    .perform();
        } catch (Throwable e) {
            executeJavaScript(
                    "arguments[0].dispatchEvent(new MouseEvent('contextmenu', {bubbles: true, cancelable: true, button: 2}));",
                    tagElement.toWebElement()
            );
        }
        return this;
    }

    @Step("Открыть контекстное меню тега '{tagText}' в сообщении с ID '{messageId}'")
    public ChatActions openTagContextMenuByIdAndText(String messageId, String tagText) {
        SelenideElement messageBubble = $(chatElements.getContainerWithMessages())
                .$(chatElements.getMessageBubbleById(messageId))
                .shouldBe(visible, Duration.ofSeconds(10));
        messageBubble.scrollIntoView(true);
        SelenideElement tagElement = messageBubble.$$(chatElements.getMessageTagLabel())
                .findBy(text(tagText))
                .shouldBe(visible, Duration.ofSeconds(10));
        try {
            new Actions(WebDriverRunner.getWebDriver())
                    .moveToElement(tagElement)
                    .contextClick()
                    .perform();
        } catch (Throwable e) {
            executeJavaScript(
                    "arguments[0].dispatchEvent(new MouseEvent('contextmenu', {bubbles: true, cancelable: true, button: 2}));",
                    tagElement.toWebElement()
            );
        }
        return this;
    }

    @Step("Кликнуть по реакции '{emoji}' в сообщении с ID '{messageId}'")
    public ChatActions clickReactionInMessageBubbleById(String messageId, String emoji) {
        SelenideElement messageBubble = $(chatElements.getContainerWithMessages())
                .$(chatElements.getMessageBubbleById(messageId))
                .shouldBe(visible, Duration.ofSeconds(10));
        messageBubble.scrollIntoView(true);
        SelenideElement messageRoot = messageBubble.$(chatElements.getMessageRoot())
                .shouldBe(visible, Duration.ofSeconds(10));
        SelenideElement reactionItem = messageRoot.$(By.cssSelector(chatElements.getMessageReaction(emoji)))
                .shouldBe(visible, Duration.ofSeconds(10));
        reactionItem.scrollIntoView(true);
        executeJavaScript("arguments[0].click();", reactionItem.toWebElement());
        return this;
    }

    @Step("Навести курсор на реакцию '{emoji}' в сообщении с ID '{messageId}'")
    public ChatActions hoverReactionInMessageBubbleById(String messageId, String emoji) {
        SelenideElement messageBubble = $(chatElements.getContainerWithMessages())
                .$(chatElements.getMessageBubbleById(messageId))
                .shouldBe(visible, Duration.ofSeconds(10));
        messageBubble.scrollIntoView(true);
        SelenideElement messageRoot = messageBubble.$(chatElements.getMessageRoot())
                .shouldBe(visible, Duration.ofSeconds(10));
        SelenideElement reactionItem = messageRoot.$(By.cssSelector(chatElements.getMessageReaction(emoji)))
                .shouldBe(visible, Duration.ofSeconds(10));
        reactionItem.scrollIntoView(true);
        Actions actions = new Actions(WebDriverRunner.getWebDriver());
        actions.moveToElement(reactionItem.toWebElement()).perform();
        return this;
    }

    @Step("Навести курсор на сообщение с ID '{messageId}'")
    public ChatActions hoverMessageById(Long messageId) {
        new Actions(WebDriverRunner.getWebDriver())
                .moveToElement($(chatElements.getMessageBubbleByIdSecond(String.valueOf(messageId))))
                .perform();
        return this;
    }

    @Step("Кликнуть кнопку 'Копировать' на карточке контакта")
    public ChatActions clickCopyContactButton() {
        $(chatElements.getCopyContactButton()).shouldBe(interactable).click();
        return this;
    }

}
