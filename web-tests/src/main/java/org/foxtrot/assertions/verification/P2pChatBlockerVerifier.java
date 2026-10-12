package org.foxtrot.assertions.verification;

import io.qameta.allure.Step;
import org.foxtrot.pages.chat.chat.elements.Chat;
import org.foxtrot.pages.chat.input_bar.elements.InputBar;
import org.openqa.selenium.WebDriver;

import java.time.Duration;

import static com.codeborne.selenide.Condition.not;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class P2pChatBlockerVerifier {

    private final WebDriver driver;
    private final Chat chat;
    private final InputBar inputBar = new InputBar();

    public P2pChatBlockerVerifier(Chat chat, WebDriver driver) {
        this.chat = chat;
        this.driver = driver;
    }

    @Step("Проверить блокировку интерфейса чата при блокировке P2P")
    public P2pChatBlockerVerifier verifyChatInterfaceBlockedForBlockedP2P() {
        verifyChatInterfaceBlocked();
        return this;
    }

    @Step("Проверить блокировку интерфейса чата при блокировке пользователя")
    public P2pChatBlockerVerifier verifyChatInterfaceBlockedForBlockedByUser() {
        verifyChatInterfaceBlocked();
        return this;
    }

    @Step("Проверить блокировку интерфейса чата при взаимной блокировке")
    public P2pChatBlockerVerifier verifyChatInterfaceBlockedForMutualBlock() {
        verifyChatInterfaceBlocked();
        return this;
    }

    private void verifyChatInterfaceBlocked() {
        $(inputBar.getMessageField()).shouldBe(not(visible), Duration.ofSeconds(10));
        $(inputBar.getMessageSendButton()).shouldBe(not(visible), Duration.ofSeconds(10));
    }
}