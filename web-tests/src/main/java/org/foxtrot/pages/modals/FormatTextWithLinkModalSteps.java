package org.foxtrot.pages.modals;

import io.qameta.allure.Step;
import org.foxtrot.pages.modals.elements.FormatTextWithLinkModal;

import java.time.Duration;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class FormatTextWithLinkModalSteps {

    private final FormatTextWithLinkModal elements = new FormatTextWithLinkModal();

    @Step("Нажать на кнопку 'Done'")
    public FormatTextWithLinkModalSteps clickDoneButton() {
        $(elements.getDoneButton()).shouldBe(visible, Duration.ofSeconds(15)).click();
        return this;
    }

    @Step("Нажать на кнопку 'Cancel'")
    public FormatTextWithLinkModalSteps clickCancelButton() {
        $(elements.getCancelButton()).shouldBe(visible, Duration.ofSeconds(15)).click();
        return this;
    }

    @Step("Кликнуть в поле ввода URL")
    public FormatTextWithLinkModalSteps clickUrlInputField() {
        $(elements.getUrlInputField()).shouldBe(visible, Duration.ofSeconds(15)).click();
        return this;
    }

    @Step("Проверить наличие всех элементов модального окна")
    public FormatTextWithLinkModalSteps verifyAllElementsVisible() {
        $(elements.getModalContainer()).shouldBe(visible, Duration.ofSeconds(15));
        $(elements.getDoneButton()).shouldBe(visible, Duration.ofSeconds(15));
        $(elements.getCancelButton()).shouldBe(visible, Duration.ofSeconds(15));
        $(elements.getUrlInputField()).shouldBe(visible, Duration.ofSeconds(15));
        $(elements.getMessageTextToBeFormatted()).shouldBe(visible, Duration.ofSeconds(15));
        return this;
    }

    @Step("Ввести URL в поле ввода: {url}")
    public FormatTextWithLinkModalSteps enterUrl(String url) {
        $(elements.getUrlInputField()).shouldBe(visible, Duration.ofSeconds(15)).click();
        $(elements.getUrlInputField()).setValue(url);
        $(elements.getDoneButton()).click();
        return this;
    }

    @Step("Проверить, что текст сообщения '{messageText}' отображается в модальном окне для форматирования")
    public FormatTextWithLinkModalSteps verifyMessageTextToBeFormatted(String messageText) {
        $(elements.getMessageTextToBeFormatted())
                .shouldBe(visible, Duration.ofSeconds(15))
                .shouldHave(text(messageText));
        return this;
    }

    @Step("Выполнить полный процесс форматирования текста ссылкой: проверить элементы, текст сообщения '{messageText}', ввести URL '{url}' и подтвердить")
    public FormatTextWithLinkModalSteps formatTextWithLink(String messageText, String url) {
        verifyAllElementsVisible();
        verifyMessageTextToBeFormatted(messageText);
        clickUrlInputField();
        enterUrl(url);
        return this;
    }
}
