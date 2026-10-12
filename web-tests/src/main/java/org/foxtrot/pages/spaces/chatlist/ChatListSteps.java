package org.foxtrot.pages.spaces.chatlist;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import io.qameta.allure.Step;
import org.foxtrot.config.RunChannelResolver;
import org.foxtrot.enums.UsersEnum;
import org.foxtrot.pages.chat.chat.elements.Chat;
import org.foxtrot.pages.chat.input_bar.elements.InputBar;
import org.foxtrot.pages.modals.WebOnboardingOverlay;
import org.foxtrot.pages.spaces.chatlist.elements.ChatList;

import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class ChatListSteps {

    private static final String BRIGHT_UNREAD_COUNTER_COLOR = "rgba(229, 80, 90, 1)";

    private final ChatList elements = new ChatList();
    private final InputBar inputBar = new InputBar();
    private final Chat chat = new Chat();

    public ChatListSteps() {
    }

    private SelenideElement findChatItemByName(String chatName) {
        WebOnboardingOverlay.dismissIfPresent();
        int maxAttempts = RunChannelResolver.isDesktop() ? 3 : 1;
        Duration perAttempt = Duration.ofSeconds(RunChannelResolver.isDesktop() ? 10 : 20);
        var needles = UsersEnum.uiNameNeedles(chatName);
        if (needles.isEmpty()) {
            needles = java.util.List.of(chatName);
        }
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            Throwable last = null;
            for (int i = 0; i < needles.size(); i++) {
                Duration wait = i == needles.size() - 1 ? perAttempt : Duration.ofSeconds(5);
                try {
                    return $(elements.chatCardByName(needles.get(i)))
                            .shouldBe(visible, wait);
                } catch (Throwable e) {
                    last = e;
                }
            }
            if (attempt == maxAttempts) {
                if (last instanceof RuntimeException runtime) {
                    throw runtime;
                }
                throw new IllegalStateException("Chat '" + chatName + "' not found", last);
            }
            refreshChatList();
        }
        throw new IllegalStateException("Chat '" + chatName + "' not found");
    }

    private void refreshChatList() {
        try {
            WebDriverRunner.getWebDriver().navigate().refresh();
        } catch (Throwable ignored) {
            Selenide.refresh();
        }
    }

    @Step("Проверить наличие заглушки с инициалами у пользователя {userName} в превью чатов")
    public void verifyUserHasInitialsAvatarPlaceholderInChatsPreview(String userName, String phone) {
        findChatItemByName(userName).shouldBe(visible, Duration.ofSeconds(20));
        $(elements.userWithNameAndAvatar(userName)).shouldNotBe(visible);
        $(elements.userWithAvatarPlaceholder(phone)).shouldNotBe(visible);
    }

    @Step("Проверить наличие серой заглушки у пользователя {userName} в списке пользователей для форварда")
    public void verifyUserHasAvatarPlaceholderInChatsPreview(String userName, String phone, boolean phoneVisible) {
        $(elements.userWithNameAndAvatar(userName)).shouldNotBe(visible);
        if (phoneVisible) {
            $(elements.userWithAvatarPlaceholder(phone)).shouldBe(visible, Duration.ofSeconds(10));
        } else {
            $(elements.userWithAvatarPlaceholder(userName)).shouldBe(visible, Duration.ofSeconds(10));
        }
    }

    @Step("Проверить наличие аватарки у пользователя {userName} в превью чатов")
    public void verifyUserHasAvatarInChatsPreview(String userName, String phone) {
        findChatItemByName(userName).shouldBe(visible, Duration.ofSeconds(20));
        $(elements.userWithNameAndAvatar(userName)).shouldBe(visible);
        $(elements.userWithAvatarPlaceholder(phone)).shouldNotBe(visible);
    }

    @Step("Проверить, что превью чата '{chatName}' содержит текст '{sentMessageText}'")
    public ChatListSteps verifyMessagePreview(String chatName, String sentMessageText) {
        findChatItemByName(chatName)
                .scrollIntoView(true)
                .$(elements.getPreviewTextMessage())
                .shouldBe(visible, Duration.ofSeconds(10))
                .shouldHave(text(sentMessageText));
        return this;
    }

    @Step("Проверить, что в превью чата '{chatName}' отображается карточка контакта '{contactFullName}'")
    public ChatListSteps verifyContactPreview(String chatName, String contactFullName) {
        String expectedText = "Контакт: " + contactFullName;

        return verifyMessagePreview(chatName, expectedText);
    }

    @Step("Проверить, что превью чата '{chatName}' в чатлисте содержит текст системного сообщения '{systemMessageText}'")
    public ChatListSteps verifySystemMessagePreview(String chatName, String systemMessageText) {

        findChatItemByName(chatName)
                .scrollIntoView(true)
                .$(elements.getPreviewSystemMessage())
                .shouldBe(visible, Duration.ofSeconds(10))
                .shouldHave(text(systemMessageText));

        return this;
    }

    @Step("Проверить, что каунтер в чате '{chatName}' отображает значение '{expectedCount}'")
    public ChatListSteps verifyChatCounter(String chatName, String expectedCount) {
        findChatItemByName(chatName)
                .scrollIntoView(true)
                .$(elements.getChatCounter())
                .shouldBe(visible, Duration.ofSeconds(10))
                .shouldHave(text(expectedCount));
        return this;
    }

    @Step("Проверить наличие каунтера в чате '{chatName}'")
    public ChatListSteps verifyChatCounterVisible(String chatName) {
        findChatItemByName(chatName)
                .scrollIntoView(true)
                .$(elements.getChatCounter())
                .shouldBe(visible, Duration.ofSeconds(10));
        return this;
    }

    @Step("Проверить, что каунтер в чате '{chatName}' отображается без числа и ярко-красным цветом")
    public ChatListSteps verifyChatCounterWithoutCountAndRedColor(String chatName) {
        findChatItemByName(chatName)
                .$(elements.getChatCounter())
                .shouldBe(visible, Duration.ofSeconds(10))
                .shouldHave(exactText(""))
                .shouldHave(cssValue("background-color", BRIGHT_UNREAD_COUNTER_COLOR));
        return this;
    }

    @Step("Проверить отсутствие каунтера в чатах {chatNames}")
    public ChatListSteps verifyChatCountersAbsent(String... chatNames) {
        for (String name : chatNames) {
            findChatItemByName(name)
                    .scrollIntoView(true)
                    .$(elements.getChatCounter())
                    .shouldBe(hidden, Duration.ofSeconds(5));
        }
        return this;
    }

    @Step("Открыть чат '{chatName}'")
    public ChatListSteps clickChatByName(String chatName) {
        if (chatName == null) {
            throw new IllegalArgumentException("chatName не может быть null");
        }
        WebOnboardingOverlay.dismissIfPresent();
        SelenideElement chatButton = findChatItemByName(chatName).scrollIntoView(true);
        chatButton.shouldBe(clickable, Duration.ofSeconds(10)).click();
        waitForChatOpened(chatName);
        return this;
    }

    @Step("Получить позицию чата '{chatName}' в списке чатов")
    public int getChatPosition(String chatName) {
        if (chatName == null) {
            throw new IllegalArgumentException("chatName не может быть null");
        }

        String expectedPrefix = "openChat";
        ElementsCollection chatCards = $$(elements.getOpenChatCardCss())
                .filterBy(visible);

        var needles = UsersEnum.uiNameNeedles(chatName);
        if (needles.isEmpty()) {
            needles = java.util.List.of(chatName);
        }
        for (int i = 0; i < chatCards.size(); i++) {
            String dataCy = chatCards.get(i).getAttribute("data-cy");
            for (String needle : needles) {
                if ((expectedPrefix + needle).equals(dataCy)) {
                    return i;
                }
            }
        }

        throw new AssertionError("Чат '" + chatName + "' не найден в списке чатов");
    }

    /**
     * Desktop: клик по превью иногда не открывает ленту (остаёмся в chat list).
     * Ждём поле ввода или контейнер сообщений; при необходимости — повторный клик.
     */
    private void waitForChatOpened(String chatName) {
        Duration timeout = RunChannelResolver.isDesktop() ? Duration.ofSeconds(20) : Duration.ofSeconds(10);
        long deadline = System.currentTimeMillis() + timeout.toMillis();

        while (System.currentTimeMillis() < deadline) {
            if (isChatOpened()) {
                return;
            }
            Selenide.sleep(200);
        }

        if (RunChannelResolver.isDesktop()) {
            $(elements.chatCardByName(chatName))
                    .shouldBe(clickable, Duration.ofSeconds(5))
                    .click();
            $(inputBar.getMessageField()).shouldBe(visible, Duration.ofSeconds(10));
            return;
        }

        $(chat.getContainerWithMessages()).shouldBe(visible, Duration.ofSeconds(5));
    }

    private boolean isChatOpened() {
        if ($(inputBar.getMessageField()).is(interactable)) {
            return true;
        }
        return $(chat.getContainerWithMessages()).is(visible);
    }

    @Step("Открыть контекстное меню чата '{chatName}'")
    public ChatListSteps openContextMenuByChatName(String chatName) {
        findChatItemByName(chatName).scrollIntoView(true)
                .shouldBe(clickable, Duration.ofSeconds(10))
                .contextClick();
        return this;
    }

    @Step("Проверить наличие чата '{chatName}'")
    public ChatListSteps verifyChatWithNameIsDisplayed(String chatName, int timeOut) {
        SelenideElement chatItem = findChatItemByName(chatName).scrollIntoView(true);
        chatItem.shouldBe(visible, Duration.ofSeconds(timeOut));
        return this;
    }

    @Step("Проверить отсутствие чата '{chatName}'")
    public ChatListSteps verifyChatWithNameIsNotDisplayed(String chatName) {
        $(elements.chatCardByName(chatName)).shouldNotBe(visible);
        return this;
    }

    @Step("Открыть чат '{user}'")
    public ChatListSteps clickChatByName(UsersEnum user) {
        try {
            return clickChatByName(user.getName());
        } catch (AssertionError e) {
            return clickChatByName(user.getFormattedPhone());
        }
    }

    @Step("Открыть чат [Сохраненные], кликом в превью чатов")
    public ChatListSteps clickSavedChat() {
        SelenideElement savedChat = $(elements.getSavedChatButton()).should(appear, Duration.ofSeconds(30));
        if (!savedChat.isDisplayed()) {
            savedChat.scrollIntoView(true);
        }
        savedChat.shouldBe(clickable).click();
        return this;
    }

    @Step("Открыть чат [Tango], кликом в превью чатов")
    public ChatListSteps clickTangoChat() {
        SelenideElement savedChat = $(elements.getTdmChatButton()).should(appear, Duration.ofSeconds(30));
        if (!savedChat.isDisplayed()) {
            savedChat.scrollIntoView(true);
        }
        savedChat.shouldBe(clickable).click();
        return this;
    }

    @Step("Найти и открыть чат [Сохраненные], прокрутив список до видимости элемента")
    public ChatListSteps clickChatSaved() {
        SelenideElement savedChat = $(elements.getSavedChatButton());
        if (!savedChat.isDisplayed()) {
            savedChat.scrollIntoView(true);
        }
        savedChat
                .shouldBe(visible, Duration.ofSeconds(20))
                .shouldBe(clickable, Duration.ofSeconds(10))
                .click();
        return this;
    }

    @Step("Открыть чат 'Главный'")
    public ChatListSteps clickMainChat() {
        return clickChatByName("Главный");
    }
}
