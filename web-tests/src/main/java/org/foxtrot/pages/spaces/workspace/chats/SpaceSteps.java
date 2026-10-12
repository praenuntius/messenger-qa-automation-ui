package org.foxtrot.pages.spaces.workspace.chats;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.foxtrot.pages.spaces.workspace.chats.elements.Space;
import org.openqa.selenium.By;

import java.time.Duration;

import static com.codeborne.selenide.Condition.clickable;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static org.openqa.selenium.By.cssSelector;

public class SpaceSteps {

    private final Space elements = new Space();

    @Step("Проверить, что название пространства равно {spaceName}")
    public void verifySpaceName(String spaceName) {
        $(elements.getSpaceName())
                .shouldBe(visible, Duration.ofSeconds(10))
                .shouldHave(text(spaceName));
    }

    @Step("Проверить, наличие чата {chatName}")
    public void verifyChatIsDisplayed(String chatName) {
        $(elements.getChatName())
                .shouldBe(visible, Duration.ofSeconds(10))
                .shouldHave(text(chatName));
    }

    @Step("Открыть меню участников")
    public void clickParticipantsButton() {
        $(elements.getParticipantsButton()).click();
    }

    @Step("Открыть настройки в меню пространства")
    public void openSpaceMenu() {
        org.foxtrot.pages.modals.WebOnboardingOverlay.dismissIfPresent();
        $(elements.getOpenSpaceMenuButton())
                .shouldBe(visible, Duration.ofSeconds(15))
                .shouldBe(clickable)
                .click();
    }

    @Step("Выбрать чат типа '{chatType}' с названием '{chatName}'")
    public SpaceSteps clickChat(String chatType, String chatName) {
        By selectorPrefix;
        switch (chatType.toLowerCase()) {
            case "publicchannel":
                selectorPrefix = elements.getPublicChannelPrefix();
                break;
            case "privatechannel":
                selectorPrefix = elements.getPrivateChannelPrefix();
                break;
            case "publicgroup":
                selectorPrefix = elements.getPublicGroupPrefix();
                break;
            case "privategroup":
                selectorPrefix = elements.getPrivateGroupPrefix();
                break;
            default:
                throw new IllegalArgumentException("Unknown chat type: " + chatType);
        }
        String fullSelector = selectorPrefix + chatName + "']";
        SelenideElement chat = $(cssSelector(fullSelector))
                .shouldBe(visible, Duration.ofSeconds(10));
        chat.click();
        return this;
    }

    @Step("Открыть чат 'Главный'")
    public SpaceSteps openMainChat() {
        $(elements.getOpenWorkspaceMainChat()).click();
        return null;
    }

    @Step("Открыть публичный канал '{chatName}'")
    public SpaceSteps openPublicChannel(String chatName) {
        return clickChat("publicChannel", chatName);
    }

    @Step("Открыть приватный канал '{chatName}'")
    public SpaceSteps openPrivateChannel(String chatName) {
        return clickChat("privateChannel", chatName);
    }

    @Step("Открыть публичную группу '{chatName}'")
    public SpaceSteps openPublicGroup(String chatName) {
        return clickChat("publicGroup", chatName);
    }

    @Step("Открыть приватную группу '{chatName}'")
    public SpaceSteps openPrivateGroup(String chatName) {
        return clickChat("privateGroup", chatName);
    }
}
