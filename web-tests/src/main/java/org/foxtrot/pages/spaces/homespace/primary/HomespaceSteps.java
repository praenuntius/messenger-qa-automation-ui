package org.foxtrot.pages.spaces.homespace.primary;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Attachment;
import io.qameta.allure.Step;
import org.foxtrot.enums.UsersEnum;
import org.foxtrot.pages.modals.WebOnboardingOverlay;
import org.foxtrot.pages.spaces.homespace.primary.elements.Homespace;
import org.openqa.selenium.OutputType;

import java.time.Duration;

import static com.codeborne.selenide.Condition.appear;
import static com.codeborne.selenide.Condition.clickable;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class HomespaceSteps {

    private final Homespace elements = new Homespace();

    @Step("Открыть окно создания нового чата")
    public HomespaceSteps clickCreateNewChatButton() {
        $(elements.getCreateNewChatButton())
                .shouldBe(visible, Duration.ofSeconds(20))
                .shouldBe(clickable)
                .click();
        return this;
    }

    @Step("Проверить, что страница хоумспейса загружена")
    public HomespaceSteps checkPageIsLoaded() {
        $(elements.getHomespaceButton()).shouldBe(visible, Duration.ofSeconds(10));
        return this;
    }

    @Step("Открыть контекстное меню чата '{chatName}'")
    public HomespaceSteps clickContextMenuChatByName(String chatName) {
        SelenideElement chatButton = findChatCard(chatName)
                .shouldBe(visible, Duration.ofSeconds(20));
        chatButton.contextClick();
        return this;
    }

    @Step("Открыть чат '{chatName}'")
    public HomespaceSteps clickChatByName(String chatName) {
        if (chatName == null) {
            throw new IllegalArgumentException("chatName не может быть null");
        }

        findChatCard(chatName)
                .shouldBe(visible, Duration.ofSeconds(20))
                .click();
        return this;
    }

    @Step("Проверить, что чат '{chatName}' отсутствует")
    public HomespaceSteps verifyChatAbsent(String chatName) {
        if (chatName == null) {
            throw new IllegalArgumentException("chatName не может быть null");
        }

        for (String needle : UsersEnum.uiNameNeedles(chatName)) {
            $(elements.chatCardByName(needle)).shouldNot(exist, Duration.ofSeconds(5));
        }
        return this;
    }

    @Step("Открыть чат [Сохраненные], кликом в превью чатов")
    public HomespaceSteps clickSavedChat() {
        SelenideElement savedChat = $(elements.getSavedChatButton()).should(appear, Duration.ofSeconds(30));
        if (!savedChat.isDisplayed()) {
            savedChat.scrollIntoView(true);
        }
        savedChat.shouldBe(clickable).click();
        return this;
    }

    private SelenideElement findChatCard(String chatName) {
        WebOnboardingOverlay.dismissIfPresent();
        var needles = UsersEnum.uiNameNeedles(chatName);
        if (needles.isEmpty()) {
            needles = java.util.List.of(chatName);
        }
        Throwable last = null;
        for (int i = 0; i < needles.size(); i++) {
            Duration wait = i == needles.size() - 1 ? Duration.ofSeconds(20) : Duration.ofSeconds(5);
            try {
                return $(elements.chatCardByName(needles.get(i))).shouldBe(visible, wait);
            } catch (Throwable e) {
                last = e;
            }
        }
        if (last instanceof RuntimeException runtime) {
            throw runtime;
        }
        throw new IllegalStateException("Chat '" + chatName + "' not found", last);
    }

    @Attachment(value = "Page source on fail", type = "text/plain")
    private byte[] attachPageSource(String name) {
        return Selenide.executeJavaScript("return document.documentElement.outerHTML;").toString().getBytes();
    }

    @Attachment(value = "{0} Screenshot", type = "image/png")
    private byte[] attachScreenshot(String name) {
        return Selenide.screenshot(OutputType.BYTES);
    }
}
