package org.foxtrot.pages.chat.chat.message_bubble_context_menu;

import com.codeborne.selenide.WebDriverRunner;
import io.qameta.allure.Step;
import org.foxtrot.pages.chat.chat.message_bubble_context_menu.elements.MessageBubbleContextMenu;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import static com.codeborne.selenide.Condition.hidden;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class MessageBubbleContextMenuSteps {

    private final MessageBubbleContextMenu contextMenu = new MessageBubbleContextMenu();

    public By getMessageEditButton() {
        return contextMenu.getMessageEditButton();
    }

    public By getMessageViewedButton() {
        return contextMenu.getMessageViewedButton();
    }

    public By getMessageReactionsButton() {
        return contextMenu.getMessageReactionsButton();
    }

    public By getMessagePinButton() {
        return contextMenu.getMessagePinButton();
    }

    @Step("Проверить, что отображается уведомление об ожидании прочтения сообщения")
    public MessageBubbleContextMenuSteps verifyAutoDeleteTimerWaitingForRead() {
        $(contextMenu.getMessageAutodeleteTimerHasNotStarted())
                .shouldBe(visible, Duration.ofSeconds(10));
        return this;
    }

    @Step("Проверить, что таймер автоудаления отображаетcя")
    public MessageBubbleContextMenuSteps verifyAutoDeleteTimerVisible() {
        $(contextMenu.getMessageAutodeleteUnit()).shouldBe(visible, Duration.ofSeconds(10));
        return this;
    }

    @Step("Проверить, что сообщение не подлежит автоудалению")
    public MessageBubbleContextMenuSteps verifyAutoDeleteNotDisplayed() {
        $(contextMenu.getMessageAutodeleteTimerHasNotStarted())
                .shouldNotBe(visible);
        $(contextMenu.getMessageAutodeleteUnit())
                .shouldNotBe(visible);
        return this;
    }

    @Step("Получить текущее время на таймере автоудаления")
    public String getAutoDeleteTimerText() {
        return $(contextMenu.getMessageAutodeleteUnit())
                .shouldBe(visible, Duration.ofSeconds(10))
                .text();
    }

    @Step("Дождаться значения таймера '{expectedTime}'")
    public void waitForTimerValueQuietly(String expectedTime) {
        WebDriver driver = WebDriverRunner.getWebDriver();
        // Находим селектор, который используется внутри getAutoDeleteTimerText()

        new WebDriverWait(driver, Duration.ofSeconds(20))
                .pollingEvery(Duration.ofMillis(300))
                .until(d -> {
                    // Используем нативный Selenium (он не пишет логи)
                    String text = d.findElement(contextMenu.getMessageAutodeleteUnit()).getText();
                    return text.contains(expectedTime);
                });
    }


    @Step("Проверить, что таймер автоудаления стартовал в диапазоне 05:00")
    public MessageBubbleContextMenuSteps verifyTimerStartsFromFiveMinutes() {
        String timerText = getAutoDeleteTimerText();

        boolean isCorrectTime = timerText.matches(".*(05:00|04:5\\d)$");

        if (!isCorrectTime) {
            throw new org.opentest4j.AssertionFailedError(
                    "Таймер стартовал вне допустимого диапазона (10 сек)",
                    "05:00 или 04:5x",
                    timerText
            );
        }
        return this;
    }

    @Step("Кликнуть по кнопке Reply в контекстном меню сообщения")
    public MessageBubbleContextMenuSteps clickReplyButton() {
        $(contextMenu.getMessageReplyButton())
                .shouldBe(visible, Duration.ofSeconds(10))
                .click();
        return this;
    }

    @Step("Кликнуть по кнопке Edit в контекстном меню сообщения")
    public MessageBubbleContextMenuSteps clickEditButton() {
        $(contextMenu.getMessageEditButton())
                .shouldBe(visible, Duration.ofSeconds(10))
                .click();
        return this;
    }

    @Step("Кликнуть по кнопке Copy Text в контекстном меню сообщения")
    public MessageBubbleContextMenuSteps clickCopyTextButton() {
        $(contextMenu.getMessageCopyTextButton())
                .shouldBe(visible, Duration.ofSeconds(10))
                .click();
        return this;
    }

    @Step("Кликнуть по кнопке Forward в контекстном меню сообщения")
    public MessageBubbleContextMenuSteps clickForwardButton() {
      $(contextMenu.getMessageForwardButton()).shouldBe(visible).click();
      return this;
    }

    @Step("Кликнуть по кнопке Pin в контекстном меню сообщения")
    public MessageBubbleContextMenuSteps clickPinButton() {
        $(contextMenu.getMessagePinButton())
                .shouldBe(visible, Duration.ofSeconds(10))
                .click();
        return this;
    }

    @Step("Кликнуть по кнопке In Saved в контекстном меню сообщения")
    public MessageBubbleContextMenuSteps clickIntoSavedButton() {
        $(contextMenu.getMessageIntoSavedButton())
                .shouldBe(visible, Duration.ofSeconds(10))
                .click();
        return this;
    }

    @Step("Кликнуть по кнопке Add Tag в контекстном меню сообщения")
    public MessageBubbleContextMenuSteps clickAddTagButton() {
        $(contextMenu.getMessageAddTagButton())
                .shouldBe(visible, Duration.ofSeconds(10))
                .click();
        return this;
    }

    @Step("Кликнуть по кнопке Delete в контекстном меню сообщения")
    public MessageBubbleContextMenuSteps clickDeleteButton() {
        $(contextMenu.getMessageDeleteButton())
                .shouldBe(visible, Duration.ofSeconds(10))
                .click();
        return this;
    }

    @Step("Кликнуть по кнопке Comment в контекстном меню сообщения")
    public MessageBubbleContextMenuSteps clickCommentButton() {
        $(contextMenu.getMessageCommentButton())
                .shouldBe(visible, Duration.ofSeconds(10))
                .click();
        return this;
    }

    @Step("Контекстное меню: проверить наличие кнопки Comment")
    public MessageBubbleContextMenuSteps verifyCommentButtonPresent() {
        $(contextMenu.getMessageCommentButton())
                .shouldBe(visible, Duration.ofSeconds(10));
        return this;
    }

    @Step("Кликнуть по кнопке Viewed в контекстном меню сообщения")
    public MessageBubbleContextMenuSteps clickViewedButton() {
        $(contextMenu.getMessageViewedButton())
                .shouldBe(visible, Duration.ofSeconds(10))
                .click();
        return this;
    }

    @Step("Кликнуть по кнопке Reactions в контекстном меню сообщения")
    public MessageBubbleContextMenuSteps clickReactionsButton() {
        $(contextMenu.getMessageReactionsButton())
                .shouldBe(visible, Duration.ofSeconds(10))
                .click();
        return this;
    }

    @Step("Кликнуть по кнопке Select в контекстном меню сообщения")
    public MessageBubbleContextMenuSteps clickSelectButton() {
        $(contextMenu.getMessageSelectButton())
                .shouldBe(visible, Duration.ofSeconds(10))
                .click();
        return this;
    }

    @Step("Кликнуть по кнопке Cancel Vote в контекстном меню опроса")
    public MessageBubbleContextMenuSteps clickCancelVoteButton() {
        $(contextMenu.getPollCancelVoteButton())
                .shouldBe(visible, Duration.ofSeconds(10))
                .click();
        return this;
    }

    @Step("Кликнуть по кнопке Finish Poll в контекстном меню опроса")
    public MessageBubbleContextMenuSteps clickFinishPoll() {
        $(contextMenu.getFinishPollButton())
                .shouldBe(visible, Duration.ofSeconds(10))
                .click();
        return this;
    }

    @Step("Контекстное меню: своё сообщение — проверить доступные опции")
    public MessageBubbleContextMenuSteps verifyOwnMessageContextMenu() {
        verifyMenuItemsVisible(Arrays.asList(
                contextMenu.getMessageReplyButton(),
                contextMenu.getMessageAddTagButton(),
                contextMenu.getMessageForwardButton(),
                contextMenu.getMessageSelectButton(),
                contextMenu.getMessageCopyTextButton(),
                contextMenu.getMessageIntoSavedButton(),
                contextMenu.getMessagePinButton(),
                contextMenu.getMessageEditButton(),
                contextMenu.getMessageDeleteButton()
        ));
        return this;
    }

    @Step("Контекстное меню: чужое сообщение — проверить доступные опции")
    public MessageBubbleContextMenuSteps verifyPeerMessageContextMenu() {
        verifyMenuItemsVisible(Arrays.asList(
                contextMenu.getMessageReplyButton(),
                contextMenu.getMessageAddTagButton(),
                contextMenu.getMessageForwardButton(),
                contextMenu.getMessageSelectButton(),
                contextMenu.getMessageCopyTextButton(),
                contextMenu.getMessageIntoSavedButton(),
                contextMenu.getMessagePinButton()
        ));
        verifyMenuItemsHidden(Arrays.asList(
                contextMenu.getMessageEditButton(),
                contextMenu.getMessageDeleteButton()
        ));
        return this;
    }

    @Step("Контекстное меню: свои комментарии (владелец/админ) — проверить доступные опции")
    public MessageBubbleContextMenuSteps verifyOwnCommentContextMenuByOwnerOrAdmin() {
        verifyMenuItemsVisible(Arrays.asList(
                contextMenu.getMessageReplyButton(),
                contextMenu.getMessageEditButton(),
                contextMenu.getMessageCopyTextButton(),
                contextMenu.getMessageForwardButton(),
                contextMenu.getMessageIntoSavedButton(),
                contextMenu.getMessageSelectButton(),
                contextMenu.getMessageDeleteButton()
        ));
        return this;
    }

    @Step("Контекстное меню: свои комментарии (подписчик) — проверить доступные опции")
    public MessageBubbleContextMenuSteps verifyOwnCommentContextMenuBySubscriber() {
        return verifyOwnCommentContextMenuByOwnerOrAdmin();
    }

    @Step("Контекстное меню: чужие комментарии (владелец/админ) — проверить доступные опции")
    public MessageBubbleContextMenuSteps verifyPeerCommentContextMenuByOwnerOrAdmin() {
        verifyMenuItemsVisible(Arrays.asList(
                contextMenu.getMessageReplyButton(),
                contextMenu.getMessageCopyTextButton(),
                contextMenu.getMessageForwardButton(),
                contextMenu.getMessageIntoSavedButton(),
                contextMenu.getMessageSelectButton(),
                contextMenu.getMessageDeleteButton()
        ));
        verifyMenuItemsHidden(List.of(contextMenu.getMessageEditButton()));
        return this;
    }

    @Step("Контекстное меню: чужие комментарии (подписчик) — проверить доступные опции")
    public MessageBubbleContextMenuSteps verifyPeerCommentContextMenuBySubscriber() {
        verifyMenuItemsVisible(Arrays.asList(
                contextMenu.getMessageReplyButton(),
                contextMenu.getMessageCopyTextButton(),
                contextMenu.getMessageForwardButton(),
                contextMenu.getMessageIntoSavedButton(),
                contextMenu.getMessageSelectButton()
        ));
        verifyMenuItemsHidden(Arrays.asList(
                contextMenu.getMessageEditButton(),
                contextMenu.getMessageDeleteButton()
        ));
        return this;
    }

    private void verifyMenuItemsVisible(List<By> items) {
        for (By item : items) {
            $(item)
                    .shouldBe(visible, Duration.ofSeconds(10));
        }
    }

    private void verifyMenuItemsHidden(List<By> items) {
        for (By item : items) {
            $(item)
                    .shouldBe(hidden, Duration.ofSeconds(5));
        }
    }
}
