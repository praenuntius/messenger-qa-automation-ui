package org.foxtrot.pages.chat.chat.multiselect_messages_menu;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.foxtrot.pages.chat.chat.elements.Chat;
import org.foxtrot.pages.chat.chat.multiselect_messages_menu.elements.MultiselectMessages;
import org.foxtrot.pages.chat.comments_threads.elements.Comments;

import java.time.Duration;

import static com.codeborne.selenide.ClickOptions.usingJavaScript;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

public class MultiselectMessagesSteps {

    private final MultiselectMessages elements = new MultiselectMessages();
    private final Chat chatElements = new Chat();
    private final Comments commentsElements = new Comments();

    @Step("Проверить наличие и активность всех элементов меню мультиселекта сообщений")
    public MultiselectMessagesSteps FullMultiselectMenuAvailable() {
        $(elements.getSelectedMessagesCounter()).shouldBe(visible, Duration.ofSeconds(10));
        $(elements.getCancelButton()).shouldBe(visible, Duration.ofSeconds(10)).shouldBe(enabled);
        $(elements.getDeleteButton()).shouldBe(visible, Duration.ofSeconds(10)).shouldBe(enabled);
        $(elements.getForwardButton()).shouldBe(visible, Duration.ofSeconds(10)).shouldBe(enabled);
        $(elements.getSaveButton()).shouldBe(visible, Duration.ofSeconds(10)).shouldBe(enabled);
        $(elements.getPinButton()).shouldBe(visible, Duration.ofSeconds(10)).shouldBe(enabled);
        return this;
    }

    @Step("Проверить наличие всех элементов меню мультиселекта, где кнопка Pin неактивна, но отображается")
    public MultiselectMessagesSteps FullMultiselectMenuAvailableWithDisabledPin() {
        $(elements.getSelectedMessagesCounter()).shouldBe(visible, Duration.ofSeconds(10));
        $(elements.getCancelButton()).shouldBe(visible, Duration.ofSeconds(10)).shouldBe(enabled);
        $(elements.getDeleteButton()).shouldBe(visible, Duration.ofSeconds(10)).shouldBe(enabled);
        $(elements.getForwardButton()).shouldBe(visible, Duration.ofSeconds(10)).shouldBe(enabled);
        $(elements.getSaveButton()).shouldBe(visible, Duration.ofSeconds(10)).shouldBe(enabled);
        verifyPinButtonIsDisabled();
        return this;
    }

    @Step("Проверить наличие всех элементов меню мультиселекта, где кнопка Delete неактивна, но отображается")
    public MultiselectMessagesSteps FullMultiselectMenuAvailableWithDisabledDelete() {
        $(elements.getSelectedMessagesCounter()).shouldBe(visible, Duration.ofSeconds(10));
        $(elements.getCancelButton()).shouldBe(visible, Duration.ofSeconds(10)).shouldBe(enabled);
        verifyDeleteButtonIsDisabled();
        $(elements.getForwardButton()).shouldBe(visible, Duration.ofSeconds(10)).shouldBe(enabled);
        $(elements.getSaveButton()).shouldBe(visible, Duration.ofSeconds(10)).shouldBe(enabled);
        $(elements.getPinButton()).shouldBe(visible, Duration.ofSeconds(10)).shouldBe(enabled);
        return this;
    }

    @Step("Проверить наличие всех элементов меню мультиселекта, где кнопки Pin и Delete неактивны, но отображаются")
    public MultiselectMessagesSteps FullMultiselectMenuAvailableWithDisabledPinAndDelete() {
        $(elements.getSelectedMessagesCounter()).shouldBe(visible, Duration.ofSeconds(10));
        $(elements.getCancelButton()).shouldBe(visible, Duration.ofSeconds(10)).shouldBe(enabled);
        verifyDeleteButtonIsDisabled();
        $(elements.getForwardButton()).shouldBe(visible, Duration.ofSeconds(10)).shouldBe(enabled);
        $(elements.getSaveButton()).shouldBe(visible, Duration.ofSeconds(10)).shouldBe(enabled);
        verifyPinButtonIsDisabled();
        return this;
    }

    @Step("Проверить, что кнопка Delete имеет атрибут disabled")
    public MultiselectMessagesSteps verifyDeleteButtonIsDisabled() {
        $(elements.getDeleteButton())
                .shouldBe(visible, Duration.ofSeconds(10));

        String disabledAttribute = $(elements.getDeleteButton()).getAttribute("disabled");
        if (disabledAttribute == null) {
            throw new AssertionError("Кнопка Delete должна иметь атрибут 'disabled', но он отсутствует");
        }
        return this;
    }

    @Step("Проверить, что кнопка Pin неактивна (имеет атрибут disabled)")
    public MultiselectMessagesSteps verifyPinButtonIsDisabled() {
        $(elements.getPinButton())
                .shouldBe(visible, Duration.ofSeconds(10));

        String disabledAttribute = $(elements.getPinButton()).getAttribute("disabled");
        if (disabledAttribute == null) {
            throw new AssertionError("Кнопка Pin должна иметь атрибут 'disabled', но он отсутствует");
        }
        return this;
    }

    @Step("Проверить, что кнопка Cancel активна")
    public MultiselectMessagesSteps verifyCancelButtonIsEnabled() {
        $(elements.getCancelButton())
                .shouldBe(visible, Duration.ofSeconds(10))
                .shouldBe(enabled);

        String disabledAttribute = $(elements.getCancelButton()).getAttribute("disabled");
        if (disabledAttribute != null) {
            throw new AssertionError("Кнопка Cancel должна быть активна, но имеет атрибут 'disabled'");
        }
        return this;
    }

    @Step("Проверить, что кнопка Delete активна")
    public MultiselectMessagesSteps verifyDeleteButtonIsEnabled() {
        $(elements.getDeleteButton())
                .shouldBe(visible, Duration.ofSeconds(10))
                .shouldBe(enabled);

        String disabledAttribute = $(elements.getDeleteButton()).getAttribute("disabled");
        if (disabledAttribute != null) {
            throw new AssertionError("Кнопка Delete должна быть активна, но имеет атрибут 'disabled'");
        }
        return this;
    }

    @Step("Проверить, что кнопка Forward активна")
    public MultiselectMessagesSteps verifyForwardButtonIsEnabled() {
        $(elements.getForwardButton())
                .shouldBe(visible, Duration.ofSeconds(10))
                .shouldBe(enabled);

        String disabledAttribute = $(elements.getForwardButton()).getAttribute("disabled");
        if (disabledAttribute != null) {
            throw new AssertionError("Кнопка Forward должна быть активна, но имеет атрибут 'disabled'");
        }
        return this;
    }

    @Step("Кликнуть по кнопке Cancel в меню мультиселекта")
    public MultiselectMessagesSteps clickCancelButton() {
        $(elements.getCancelButton())
                .shouldBe(visible, Duration.ofSeconds(10))
                .shouldBe(enabled)
                .click();
        return this;
    }

    @Step("Кликнуть по кнопке Delete в меню мультиселекта")
    public MultiselectMessagesSteps clickDeleteButton() {
        $(elements.getDeleteButton())
                .shouldBe(visible, Duration.ofSeconds(10))
                .shouldBe(enabled)
                .click();
        return this;
    }


    @Step("Кликнуть по кнопке Forward в меню мультиселекта")
    public MultiselectMessagesSteps clickForwardButton() {
        $(elements.getForwardButton())
                .shouldBe(visible, Duration.ofSeconds(10))
                .shouldBe(enabled)
                .click();
        return this;
    }

    @Step("Кликнуть по кнопке Save в меню мультиселекта")
    public MultiselectMessagesSteps clickSaveButton() {
        $(elements.getSaveButton())
                .shouldBe(visible, Duration.ofSeconds(10))
                .shouldBe(enabled)
                .click();
        return this;
    }

    @Step("Кликнуть по кнопке Pin в меню мультиселекта")
    public MultiselectMessagesSteps clickPinButton() {
        $(elements.getPinButton())
                .shouldBe(visible, Duration.ofSeconds(10))
                .shouldBe(enabled)
                .click();
        return this;
    }

    @Step("Кликнуть по сообщениям с текстом '{texts}' в режиме мультиселекта")
    public MultiselectMessagesSteps clickMessagesByTextInMultiselectMode(String... texts) {
        for (String text : texts) {
            $$(chatElements.getMessageBubble()).findBy(text(text))
                    .shouldBe(visible, Duration.ofSeconds(10))
                    .scrollIntoView(true)
                    .click(usingJavaScript());
        }
        return this;
    }

    @Step("Проверить, что сообщения с текстом '{texts}' выбраны в режиме мультиселекта")
    public MultiselectMessagesSteps verifyMessagesSelectedByTextInMultiselectMode(String... texts) {
        for (String text : texts) {
            $$(elements.getSelectedMessageRow()).findBy(text(text))
                    .shouldBe(visible, Duration.ofSeconds(10));
        }
        return this;
    }

    @Step("Кликнуть по комментарию с текстом '{commentText}' в режиме мультиселекта")
    public MultiselectMessagesSteps clickCommentByTextInMultiselectMode(String commentText) {
        SelenideElement commentBubble;
        if ($(commentsElements.getCommentsChatArea()).exists()) {
            commentBubble = $(commentsElements.getCommentsChatArea())
                    .$$(chatElements.getMessageBubble())
                    .findBy(text(commentText))
                    .shouldBe(visible, Duration.ofSeconds(10));
        } else {
            commentBubble = $(chatElements.getContainerWithMessages())
                    .$$(chatElements.getMessageBubble())
                    .findBy(text(commentText))
                    .shouldBe(visible, Duration.ofSeconds(10));
        }
        commentBubble.scrollIntoView(true);
        executeJavaScript("arguments[0].click();", commentBubble.toWebElement());
        return this;
    }

    @Step("Кликнуть по комментарию с ID '{commentId}' в режиме мультиселекта")
    public MultiselectMessagesSteps clickCommentByIdInMultiselectMode(String commentId) {
        SelenideElement commentBubble;
        if ($(commentsElements.getCommentsChatArea()).exists()) {
            commentBubble = $(commentsElements.getCommentsChatArea())
                    .$(chatElements.getMessageBubbleById(commentId))
                    .shouldBe(visible, Duration.ofSeconds(10));
        } else {
            commentBubble = $(chatElements.getContainerWithMessages())
                    .$(chatElements.getMessageBubbleById(commentId))
                    .shouldBe(visible, Duration.ofSeconds(10));
        }
        commentBubble.scrollIntoView(true);
        executeJavaScript("arguments[0].click();", commentBubble.toWebElement());
        return this;
    }

    @Step("Кликнуть по комментариям {commentTexts} в режиме мультиселекта")
    public MultiselectMessagesSteps clickCommentsByTextInMultiselectMode(String... commentTexts) {
        for (String commentText : commentTexts) {
            clickCommentByTextInMultiselectMode(commentText);
        }
        return this;
    }
}
