package org.foxtrot.pages.chat.chat.poll_message;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.foxtrot.pages.chat.chat.elements.Chat;
import org.foxtrot.pages.chat.chat.poll_message.elements.PollMessage;

import java.time.Duration;
import java.util.List;

import static com.codeborne.selenide.Condition.disabled;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Selenide.$;

public class PollMessageSteps {

    private final Chat chatElements = new Chat();
    private final PollMessage elements = new PollMessage();

    @Step("Прокрутить до сообщения-опроса (messageId: {messageId})")
    public PollMessageSteps scrollToPollMessageById(String messageId) {
        pollBubbleById(messageId).scrollIntoView(true);
        return this;
    }

    @Step("Проверить, что опрос '{pollTitle}' создан в текущем чате")
    public PollMessageSteps verifyPollCreatedInChatByTitle(String pollTitle) {
        SelenideElement bubble = $(chatElements.getContainerWithMessages())
                .$(elements.pollBubbleByTitle(pollTitle))
                .shouldBe(visible, Duration.ofSeconds(30));

        bubble.scrollIntoView(true);

        bubble.$(elements.getVoteButton())
                .shouldBe(visible, Duration.ofSeconds(30));

        bubble.$(elements.getPollEndedLabel())
                .shouldNot(exist);

        return this;
    }

    @Step("Проверить, что опрос '{pollTitle}' (messageId: {messageId}) отображается в чате")
    public PollMessageSteps verifyPollCreatedInMessageBubbleById(String messageId, String pollTitle) {
        SelenideElement bubble = pollBubbleById(messageId);
        bubble.scrollIntoView(true);

        bubble.$(elements.pollTitleByText(pollTitle))
                .shouldBe(visible, Duration.ofSeconds(30));
        bubble.$(elements.getVoteButton())
                .shouldBe(visible, Duration.ofSeconds(30));
        bubble.$(elements.getPollEndedLabel())
                .shouldNot(exist);

        return this;
    }

    @Step("Дождаться автоматического завершения опроса (messageId: {messageId})")
    public PollMessageSteps waitForPollToEndByMessageId(String messageId) {
        pollBubbleById(messageId).$(elements.getPollEndedLabel())
                .shouldBe(visible, Duration.ofSeconds(15));
        return this;
    }

    @Step("Проверить, что опрос '{pollTitle}' (messageId: {messageId}) завершён")
    public PollMessageSteps verifyPollEndedInMessageBubbleById(String messageId, String pollTitle) {
        SelenideElement bubble = pollBubbleById(messageId);
        bubble.scrollIntoView(true);

        bubble.$(elements.pollTitleByText(pollTitle))
                .shouldBe(visible, Duration.ofSeconds(30));
        bubble.$(elements.getPollEndedLabel())
                .shouldBe(visible, Duration.ofSeconds(30));
        bubble.$(elements.getPollEndTimeLabel())
                .shouldNot(exist);

        SelenideElement voteButton = bubble.$(elements.getVoteButton());
        if (voteButton.exists()) {
            voteButton.shouldBe(disabled, Duration.ofSeconds(10));
        } else {
            bubble.$(elements.getResultsButton())
                    .shouldBe(visible, Duration.ofSeconds(30));
        }

        return this;
    }

    @Step("Проверить, что в опросе (messageId: {messageId}) учтен голос пользователя")
    public PollMessageSteps verifyUserVoteIsPresent(String messageId) {
        SelenideElement bubble = pollBubbleById(messageId);
        bubble.scrollIntoView(true);

        bubble.$(elements.getYourVoteAcceptedLabel())
                .shouldBe(visible, Duration.ofSeconds(30));

        bubble.$(elements.getChooseOptionLabel())
                .shouldNot(exist);

        bubble.$(elements.getVoteButton())
                .shouldNot(exist);

        return this;
    }

    @Step("Проверить, что в опросе (messageId: {messageId}) нет проставленного голоса")
    public PollMessageSteps verifyNoUserVotes(String messageId) {
        SelenideElement bubble = pollBubbleById(messageId);
        bubble.scrollIntoView(true);

        bubble.$(elements.getYourVoteAcceptedLabel())
                .shouldNot(exist);

        bubble.$(elements.getVoteButton())
                .shouldBe(visible, Duration.ofSeconds(30));

        return this;
    }

    @Step("Выбрать вариант ответа #{optionIndex} в опросе (messageId: {messageId})")
    public PollMessageSteps clickVoteOptionByIndex(String messageId, int optionIndex) {
        SelenideElement bubble = pollBubbleById(messageId);
        bubble.scrollIntoView(true);
        bubble.$(elements.pollVoteOptionByIndex(optionIndex))
                .shouldBe(visible, Duration.ofSeconds(10))
                .click();
        return this;
    }

    @Step("Выбрать варианты ответа 1..{optionCount} в опросе (messageId: {messageId})")
    public PollMessageSteps clickVoteOptionsByIndexRange(String messageId, int optionCount) {
        for (int index = 1; index <= optionCount; index++) {
            clickVoteOptionByIndex(messageId, index);
        }
        return this;
    }

    @Step("Выбрать варианты ответа по тексту в опросе (messageId: {messageId})")
    public PollMessageSteps clickVoteOptionsByText(String messageId, List<String> optionTexts) {
        for (String optionText : optionTexts) {
            clickVoteOptionByText(messageId, optionText);
        }
        return this;
    }

    @Step("Выбрать вариант ответа '{optionText}' в опросе (messageId: {messageId})")
    public PollMessageSteps clickVoteOptionByText(String messageId, String optionText) {
        SelenideElement bubble = pollBubbleById(messageId);
        bubble.scrollIntoView(true);
        bubble.$(elements.pollVoteOptionByText(optionText))
                .shouldBe(visible, Duration.ofSeconds(10))
                .click();
        return this;
    }

    @Step("Нажать кнопку 'Проголосовать' в опросе (messageId: {messageId})")
    public PollMessageSteps clickSubmitVoteButton(String messageId) {
        SelenideElement bubble = pollBubbleById(messageId);
        bubble.scrollIntoView(true);
        bubble.$(elements.getVoteButton())
                .shouldBe(visible, Duration.ofSeconds(10))
                .click();
        return this;
    }

    @Step("Проверить, что опрос (messageId: {messageId}) завершён")
    public PollMessageSteps verifyPollEndedById(String messageId) {
        SelenideElement bubble = pollBubbleById(messageId);
        bubble.scrollIntoView(true);

        bubble.$(elements.getPollEndedLabel())
                .shouldBe(visible, Duration.ofSeconds(30));

        SelenideElement voteButton = bubble.$(elements.getVoteButton());
        if (voteButton.exists()) {
            voteButton.shouldBe(disabled, Duration.ofSeconds(10));
        }

        return this;
    }

    private SelenideElement pollBubbleById(String messageId) {
        return $(chatElements.getContainerWithMessages())
                .$(chatElements.getMessageBubbleById(messageId))
                .shouldBe(visible, Duration.ofSeconds(30));
    }
}

