package org.foxtrot.pages.chat.forward;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.foxtrot.pages.chat.chat.ChatVerify;
import org.foxtrot.pages.chat.forward.elements.ForwardMenu;
import org.foxtrot.pages.chat.input_bar.elements.InputBar;
import org.openqa.selenium.By;

import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static org.openqa.selenium.By.xpath;

public class ForwardMenuSteps {

    private final ForwardMenu elements = new ForwardMenu();
    private final InputBar inputBar = new InputBar();
    private String forwardedMessageText;

    @Step("Проверить наличие и доступность элементов меню пересылки сообщения")
    public ForwardMenuSteps verifyForwardMenuAvailable() {
        $(elements.getForwardMessageHeader()).shouldBe(visible, Duration.ofSeconds(10));
        $(elements.getCancelButton()).shouldBe(visible, Duration.ofSeconds(10));
        $(elements.getSelectRecipientsMenu()).shouldBe(visible, Duration.ofSeconds(10));
        $(elements.getSearchForRecipientsInputField()).shouldBe(visible, Duration.ofSeconds(10));
        return this;
    }

    @Step("Открыть список доступных для пересылки пространств")
    public ForwardMenuSteps openForwardSpacesList() {
        $(elements.getOpenForwardSpacesListButton()).click();
        return this;
    }

    @Step("Выбрать пространство {spaceName} из списка")
    public ForwardMenuSteps clickSpaceByName(String spaceName) {
        $(elements.getDefinedSpace(spaceName)).shouldBe(interactable).click();
        return this;
    }

    @Step("Проверить наличие пространства '{spaseName}' в списке доступных для пересылки")
    public ForwardMenuSteps verifySpaceInForwardList(String spaseName) {
        $(elements.getDefinedSpace(spaseName)).shouldBe(visible);
        return this;
    }

    @Step("Проверить отсутствие пространства '{spaseName}' в списке доступных для пересылки")
    public ForwardMenuSteps verifySpaceNotInForwardList(String spaseName) {
        $(elements.getDefinedSpace(spaseName)).shouldNotBe(visible);
        return this;
    }

    @Step("Проверить наличие заглушки с инициалами у пользователя {userName} в списке пользователей для форварда")
    public void verifyForwardRecipientsHasInitialsAvatarPlaceholder(String userName, String phone) {
        String photo = elements.getDefinedUserWithNameAndAvatar().replace("{username}", userName);
        String placeholder = elements.getDefinedUserWithAvatarPlaceholder().replace("{username}", phone);
        checkUserPresenceInForwardMenu(userName);
        $(xpath(photo)).shouldNotBe(visible);
        $(xpath(placeholder)).shouldNotBe(visible);
    }

    @Step("Проверить наличие заглушки с инициалами у пользователя {userName} в списке пользователей для форварда")
    public void verifyForwardRecipientsHasAvatarPlaceholder(String userName, String phone, boolean phoneVisible) {
        $(xpath(elements.getDefinedUserWithNameAndAvatar().replace("{username}", userName))).shouldNotBe(visible);

        String selector = phoneVisible
                ? elements.getDefinedUserWithAvatarPlaceholder().replace("{username}", phone)
                : elements.getDefinedUserWithAvatarPlaceholder().replace("{username}", userName);

        $(xpath(selector)).shouldBe(visible, Duration.ofSeconds(10));
    }

    @Step("Проверить наличие аватарки у пользователя {userName} в списке пользователей для форварда")
    public void verifyForwardRecipientsHasAvatar(String userName, String phone) {
        String photo = elements.getDefinedUserWithNameAndAvatar().replace("{username}", userName);
        String placeholder = elements.getDefinedUserWithAvatarPlaceholder().replace("{username}", phone);
        checkUserPresenceInForwardMenu(userName);
        $(xpath(photo)).shouldBe(visible);
        $(xpath(placeholder)).shouldNotBe(visible);
    }

    @Step("Проверить, что чат 'Сохраненные' отображается первым в списке получателей для пересылки")
    public ForwardMenuSteps verifySavedChatIsFirstInForwardList() {
        verifyForwardMenuAvailable();
        $(elements.getSelectRecipientsMenu())
                .shouldBe(visible, Duration.ofSeconds(10))
                .$$(elements.getForwardRecipient())
                .first()
                .$(elements.getDefinedChat("Сохраненные"))
                .shouldBe(visible, Duration.ofSeconds(10));
        return this;
    }

    @Step("Проверить наличие пользователя {userName} в списке пользователей для форварда")
    public void checkUserPresenceInForwardMenu(String userName) {
        $(By.xpath(elements.getDefinedUserButton().replace("{username}", userName)))
                .shouldBe(visible);
    }

    @Step("Переслать сообщение в чат '{chatName}'")
    public ForwardMenuSteps forwardMessageIntoChat(String chatName) {
        verifyForwardMenuAvailable();
        $(elements.getSearchForRecipientsInputField())
                .shouldBe(visible, Duration.ofSeconds(10))
                .setValue(chatName);

        SelenideElement forwardMenu = $(elements.getForwardMenuContainer())
                .shouldBe(visible, Duration.ofSeconds(10));

        SelenideElement chatElement = forwardMenu
                .$$(elements.getForwardRecipient())
                .findBy(text(chatName))
                .shouldBe(visible, Duration.ofSeconds(30))
                .scrollIntoView(true);

        chatElement.click();

        // Отправляем сообщение через кнопку отправки в инпут-баре
        $(inputBar.getMessageSendButton())
                .shouldBe(visible, Duration.ofSeconds(10))
                .click();
        return this;
    }

    @Step("Выбрать чат '{chatName}' для форварда")
    public ForwardMenuSteps selectChatForForward(String chatName) {
        $(elements.getDefinedChat(chatName)).shouldBe(interactable).click();
        return this;
    }

    @Step("Сохранить текст пересылаемого сообщения из меню форварда")
    public ForwardMenuSteps saveForwardMessageText() {
        forwardedMessageText = $(elements.getForwardMessageText())
                .shouldBe(visible, Duration.ofSeconds(30))
                .getText();
        return this;
    }

    @Step("Проверить, что текст пересылаемого сообщения '{expectedText}' отображается в меню форварда")
    public ForwardMenuSteps verifyForwardMessageText(String expectedText) {
        $(elements.getForwardMessageText())
                .shouldBe(visible, Duration.ofSeconds(10))
                .shouldHave(text(expectedText));
        return this;
    }

    @Step("Проверить, что текст пересылаемого сообщения с сохранённым текстом отображается в меню форварда")
    public ForwardMenuSteps verifyForwardMessageText() {
        if (forwardedMessageText == null || forwardedMessageText.isEmpty()) {
            throw new IllegalStateException("Текст пересылаемого сообщения не был сохранён. Вызовите saveForwardMessageText() перед проверкой.");
        }
        return verifyForwardMessageText(forwardedMessageText);
    }

    @Step("Проверить, что пересланное сообщение с сохранённым текстом отображается в чате")
    //TODO: сюда добавить проверку элемента форварднутого сообщения
    public ForwardMenuSteps verifyForwardedMessageInChat() {
        if (forwardedMessageText == null || forwardedMessageText.isEmpty()) {
            throw new IllegalStateException("Текст пересылаемого сообщения не был сохранён. Вызовите saveForwardMessageText() перед проверкой.");
        }
        new ChatVerify().verifyMessageText(forwardedMessageText);
        return this;
    }

    //TODO: добавить проверки пересылки в несколько чатов
    //TODO: добавить проверки атрибутов : галочка на карточке выбранного чата в листе получателей
    //TODO: добавить проверки атрибутов : Бабл выбранного чата в Search recipients поле
}
