package org.foxtrot.pages.chat.comments_threads;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.foxtrot.pages.chat.comments_threads.elements.Comments;
import org.foxtrot.pages.chat.chat.elements.Chat;
import org.foxtrot.pages.chat.chat.message_bubble_context_menu.elements.MessageBubbleContextMenu;
import org.foxtrot.pages.key_interactions.PressKeys;

import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.CollectionCondition.empty;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.sleep;

public class CommentsSteps {

    private final Chat elements = new Chat();
    private String sentCommentText;
    private final Comments commentsElements = new Comments();
    private final MessageBubbleContextMenu contextMenu = new MessageBubbleContextMenu();

    @Step("Проверить отсутствие счетчика комментариев и кнопки 'Комментировать' под сообщением '{messageText}'")
    public CommentsSteps verifyCommentElementsAbsentUnderBubble(String messageText) {
        SelenideElement messageBubble = $(elements.getContainerWithMessages())
                .$$(elements.getMessageBubble())
                .findBy(text(messageText))
                .shouldBe(visible, Duration.ofSeconds(10));

        messageBubble.$(elements.getCommentCounter())
                .shouldBe(not(visible), Duration.ofSeconds(5));
        messageBubble.$(elements.getCommentButtonUnderBubble())
                .shouldBe(not(visible), Duration.ofSeconds(5));
        return this;
    }

    @Step("Проверить наличие кнопки 'Комментировать' под сообщением '{messageText}'")
    public CommentsSteps verifyCommentButtonPresentUnderBubble(String messageText) {
        SelenideElement messageBubble = $(elements.getContainerWithMessages())
                .$$(elements.getMessageBubble())
                .findBy(text(messageText))
                .shouldBe(visible, Duration.ofSeconds(10));

        messageBubble.$(elements.getCommentButtonUnderBubble())
                .shouldBe(visible, Duration.ofSeconds(10));
        return this;
    }

    @Step("Проверить наличие кнопки 'Прокомментировать' под сообщением с ID '{messageId}'")
    public CommentsSteps verifyCommentButtonPresentUnderBubbleById(String messageId) {
        $(elements.getCommentsButtonByPostId(Long.parseLong(messageId)))
                .shouldBe(visible, Duration.ofSeconds(15));
        return this;
    }

    @Step("Проверить отсутствие кнопки 'Прокомментировать' под сообщением с ID '{messageId}'")
    public CommentsSteps verifyCommentButtonAbsentUnderBubbleById(String messageId) {
        $$(elements.getCommentsButtonByPostId(Long.parseLong(messageId)))
                .shouldBe(empty, Duration.ofSeconds(10));
        return this;
    }

    @Step("Кликнуть по кнопке 'Прокомментировать' в контекстном меню сообщения")
    public CommentsSteps clickCommentButtonInContextMenu() {
        $(contextMenu.getMessageCommentButton())
                .shouldBe(visible, Duration.ofSeconds(10))
                .click();
        return this;
    }

    @Step("Кликнуть по кнопке 'Комментировать' под сообщением '{messageText}'")
    public CommentsSteps clickCommentButtonUnderBubble(String messageText) {
        SelenideElement messageBubble = $(elements.getContainerWithMessages())
                .$$(elements.getMessageBubble())
                .findBy(text(messageText))
                .shouldBe(visible, Duration.ofSeconds(10));
        messageBubble.$(elements.getCommentButtonUnderBubble())
                .shouldBe(visible, Duration.ofSeconds(10))
                .click();
        return this;
    }

    @Step("Кликнуть по кнопке 'Прокомментировать' под сообщением с ID '{messageId}'")
    public CommentsSteps clickCommentButtonUnderBubbleById(String messageId) {
        $(elements.getCommentsButtonByPostId(Long.parseLong(messageId)))
                .shouldBe(visible, Duration.ofSeconds(10))
                .click();
        return this;
    }

    @Step("Проверить отображение меню комментариев")
    public CommentsSteps verifyCommentsMenuVisible() {
        $(elements.getCommentsMenu())
                .shouldBe(visible, Duration.ofSeconds(10));
        $(commentsElements.getBackFromCommentsButton())
                .shouldBe(visible, Duration.ofSeconds(10));
        $(commentsElements.getCommentsHeader())
                .shouldBe(visible, Duration.ofSeconds(10));
        $(commentsElements.getCommentsChatArea())
                .shouldBe(visible, Duration.ofSeconds(10));
        return this;
    }

    @Step("Проверить наличие системного сообщения об отсутствии комментариев")
    public CommentsSteps verifyNoCommentsYetSystemMessage() {
        $(commentsElements.getNoCommentsYetSystemMessage())
                .shouldBe(visible, Duration.ofSeconds(10));
        return this;
    }

    @Step("Проверить отсутствие системного сообщения об отсутствии комментариев")
    public CommentsSteps verifyNoCommentsYetSystemMessageNotVisible() {
        $(commentsElements.getNoCommentsYetSystemMessage())
                .shouldNotBe(visible, Duration.ofSeconds(10));
        return this;
    }

    @Step("Проверить наличие системного сообщения 'Начало обсуждения'")
    public CommentsSteps verifyThreadStartSystemMessageVisible() {
        $(commentsElements.getThreadStartSystemMessage())
                .shouldBe(visible, Duration.ofSeconds(10));
        return this;
    }

    @Step("Проверить наличие системного сообщения даты 'Сегодня'")
    public CommentsSteps verifyTodayDateSystemMessageVisible() {
        $(commentsElements.getTodayDateSystemMessage())
                .shouldBe(visible, Duration.ofSeconds(10));
        return this;
    }

    @Step("Проверить тред без комментариев")
    public CommentsSteps verifyThreadWithoutComments() {
        verifyCommentsMenuVisible();
        verifyNoCommentsYetSystemMessage();
        return this;
    }

    @Step("Кликнуть по кнопке 'Назад' из меню комментариев")
    public CommentsSteps clickBackFromCommentsButton() {
        $(commentsElements.getBackFromCommentsButton())
                .shouldBe(visible, Duration.ofSeconds(10))
                .click();
        return this;
    }

    @Step("Проверить, что счетчик комментариев для сообщения с ID '{messageId}' отображает значение '{expectedCount}'")
    public CommentsSteps verifyCommentCounterValue(String messageId, int expectedCount) {
        long postId = Long.parseLong(messageId);
        try {
            //обновил логику под новый селектор со значением 
            $(elements.getCommentsButtonByPostIdAndCount(postId, expectedCount))
                    .shouldBe(visible, Duration.ofSeconds(10));
        } catch (AssertionError | Exception e) {
            // пока что оставим для стабильности, потом удалим если селектор будет надежным
            SelenideElement commentButton = $(elements.getCommentsButtonByPostId(postId))
                    .shouldBe(visible, Duration.ofSeconds(10));
            String counterText = commentButton.$(elements.getCommentButtonText())
                    .shouldBe(visible, Duration.ofSeconds(5))
                    .getText();
            String numberStr = counterText.replaceAll("[^0-9]", "");
            int actualCount = numberStr.isEmpty() ? 0 : Integer.parseInt(numberStr);

            if (actualCount != expectedCount) {
                throw new AssertionError(String.format(
                        "Счетчик комментариев не соответствует ожидаемому значению. Ожидалось: %d, фактически: %d (текст: '%s')",
                        expectedCount, actualCount, counterText
                ));
            }
        }
        return this;
    }

    @Step("Отправить коммент кнопкой [Enter]")
    public CommentsSteps sendCommentWithEnterKey() {
        saveCommentInputText();
        PressKeys.pressEnter();
        waitCommentRenderedAfterSend();
        return this;
    }

    /**
     * Дожидаемся, что отправленный комментарий отрендерился в ленте, прежде чем возвращать
     * управление. Без этого быстрые enterMessage + Enter подряд гонятся с асинхронной отправкой:
     * следующий текст дописывается в ещё не очищенное поле ввода (склейка сообщений), а последний
     * коммент может не успеть отрендериться/сохраниться до ухода со страницы (clickBack).
     */
    private void waitCommentRenderedAfterSend() {
        if (sentCommentText == null || sentCommentText.isBlank()) {
            return;
        }
        $(commentsElements.getCommentsChatArea())
                .shouldBe(visible, Duration.ofSeconds(10))
                .$$(elements.getMessageBubble())
                .findBy(text(sentCommentText))
                .shouldBe(visible, Duration.ofSeconds(10));
    }

    @Step("Сохранить текст из поля ввода комментария")
    public CommentsSteps saveCommentInputText() {
        sentCommentText = $(elements.getSavedMessageText()).getText();
        return this;
    }

    private SelenideElement findSentCommentBubble() {
        sleep(1500);
        try {
            return $(commentsElements.getCommentsChatArea())
                    .$$(elements.getMessageBubble())
                    .findBy(text(sentCommentText))
                    .shouldBe(visible, Duration.ofSeconds(5));
        } catch (Exception e) {
            return $(commentsElements.getCommentsChatArea())
                    .$$(commentsElements.getMessageView())
                    .findBy(text(sentCommentText))
                    .shouldBe(visible, Duration.ofSeconds(10));
        }
    }

    private SelenideElement findSentCommentContainer() {
        // аватар искать внутри дива BaseMessageView.
        SelenideElement bubble = findSentCommentBubble();
        try {
            return bubble.$(commentsElements.getMessageViewSelfOrAncestor());
        } catch (Exception e) {
            return bubble.parent();
        }
    }

    //метод для группированных сообщений 
    private SelenideElement findNearestAuthorHeaderContainer() {
        SelenideElement current = findSentCommentContainer();

        for (int i = 0; i < 6; i++) {
            boolean hasName = current.$(commentsElements.getCommentAuthorName()).exists();
            boolean hasAvatar = current.$(elements.getMessageBubbleUserAvatar()).exists();
            if (hasName || hasAvatar) {
                return current;
            }

            //откат к предыдущему сообщению при группировке
            SelenideElement prev = current.$(commentsElements.getPreviousMessageView());
            if (!prev.exists()) {
                prev = current.$(commentsElements.getPrecedingMessageView());
            }
            if (!prev.exists()) {
                break;
            }
            current = prev;
        }
        //откат в случае если нет хедера
        return findSentCommentContainer();
    }

    @Step("Проверить, что комментарий с сохранённым текстом отображается в ленте комментариев")
    public CommentsSteps verifyCommentSent() {
        findSentCommentBubble();
        return this;
    }

    @Step("Проверить, что комментарий с текстом '{messageText}' отображается в ленте комментариев")
    public CommentsSteps verifyCommentSent(String messageText) {
        if (messageText == null || messageText.isEmpty()) {
            throw new IllegalArgumentException("messageText must not be null or empty. Use verifyCommentSent() with saveCommentInputText() for dynamic text.");
        }
        $(commentsElements.getCommentsChatArea())
                .shouldBe(visible, Duration.ofSeconds(10))
                .$$(elements.getMessageBubble())
                .findBy(text(messageText))
                .shouldBe(visible, Duration.ofSeconds(10));
        return this;
    }

    @Step("Проверить имя автора в баббле отправленного комментария: '{expectedAuthorName}'")
    public CommentsSteps verifyCommentAuthorName(String expectedAuthorName) {
        SelenideElement authorHeaderContainer = findNearestAuthorHeaderContainer();
        authorHeaderContainer.$(commentsElements.getCommentAuthorName())
                .shouldBe(visible, Duration.ofSeconds(10))
                .shouldHave(text(expectedAuthorName));
        return this;
    }

    @Step("Проверить, что аватар автора отображается в баббле отправленного комментария")
    public CommentsSteps verifyCommentAuthorAvatarVisible() {
        SelenideElement authorHeaderContainer = findNearestAuthorHeaderContainer();
        authorHeaderContainer.$(elements.getMessageBubbleUserAvatar())
                .shouldBe(visible, Duration.ofSeconds(10));
        return this;
    }

    @Step("Проверить, что комментарий отправлен и отображается (автор: '{expectedAuthorName}', аватар виден)")
    public CommentsSteps verifyCommentSentWithAuthorAndAvatar(String expectedAuthorName) {
        verifyCommentSent();
        verifyCommentAuthorName(expectedAuthorName);
        verifyCommentAuthorAvatarVisible();
        return this;
    }

    @Step("Кликнуть по кнопке 'Удалить' в контекстном меню комментария")
    public CommentsSteps clickDeleteButtonInCommentContextMenu() {
        $(contextMenu.getMessageDeleteButton())
                .shouldBe(visible, Duration.ofSeconds(10))
                .click();
        return this;
    }

    @Step("Кликнуть по кнопке 'Выбрать' в контекстном меню комментария")
    public CommentsSteps clickSelectButtonInCommentContextMenu() {
        $(contextMenu.getMessageSelectButton())
                .shouldBe(visible, Duration.ofSeconds(10))
                .click();
        return this;
    }

    @Step("Проверить системное сообщение с текстом '{messageText}' в области комментариев")
    public CommentsSteps verifySystemMessage(String messageText) {
        $(commentsElements.getCommentsChatArea())
                .shouldBe(visible, Duration.ofSeconds(10))
                .$$(commentsElements.getDeletedSystemMessage())
                .findBy(text(messageText))
                .shouldBe(visible, Duration.ofSeconds(10))
                .shouldHave(text(messageText));
        return this;
    }
}
