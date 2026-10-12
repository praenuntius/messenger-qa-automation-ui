package org.foxtrot.pages.chat.right_sidebar_menu.tabs.pinned_Message_Menu;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.foxtrot.pages.chat.chat.elements.Chat;
import org.foxtrot.pages.chat.right_sidebar_menu.tabs.pinned_Message_Menu.elements.PinnedMessageMenu;

import java.time.Duration;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static org.openqa.selenium.By.xpath;

public class PinnedMessageMenuSteps {

    private final PinnedMessageMenu elements = new PinnedMessageMenu();
    private final Chat chatElements = new Chat();

    @Step("Проверить, что отображается сайдбар с закрепленными сообщениями")
    public PinnedMessageMenuSteps verifyPinnedMessagesSidebarIsVisible() {
        $(elements.getPinnedMessagesSidebar()).shouldBe(visible, Duration.ofSeconds(10));
        return this;
    }

    @Step("Проверить, что в сайдбаре закрепленных сообщений отображается сообщение {messageId}")
    public PinnedMessageMenuSteps verifyPinnedMessageIsVisible(String messageId) {
        $(elements.getPinnedMessagesSidebar()).shouldBe(visible, Duration.ofSeconds(10));
        $(elements.getPinnedMessageById(messageId)).shouldBe(visible, Duration.ofSeconds(10));
        return this;
    }

    @Step("Проверить, что к закрепленному сообщению {messageId} применены все стили кроме подчеркивания")
    public PinnedMessageMenuSteps verifyAllStylesExceptUnderlineInPinnedMessage(String messageId, String expectedText) {
        SelenideElement pinnedMessage = $(elements.getPinnedMessageById(messageId))
                .shouldBe(visible, Duration.ofSeconds(10))
                .shouldHave(text(expectedText));

        pinnedMessage.$$(chatElements.getBoldText())
                .findBy(text(expectedText))
                .shouldBe(visible, Duration.ofSeconds(10));
        pinnedMessage.$$(chatElements.getItalicText())
                .findBy(text(expectedText))
                .shouldBe(visible, Duration.ofSeconds(10));
        pinnedMessage.$$(chatElements.getStrikethroughText())
                .findBy(text(expectedText))
                .shouldBe(visible, Duration.ofSeconds(10));
        pinnedMessage.$$(chatElements.getMonospaceText())
                .findBy(text(expectedText))
                .shouldBe(visible, Duration.ofSeconds(10));
        pinnedMessage.$$(chatElements.getUnderlinedText())
                .filterBy(text(expectedText))
                .shouldHave(size(0), Duration.ofSeconds(10));

        return this;
    }

    @Step("Перейти к закреплённому сообщению с id {messageId}")
    public PinnedMessageMenuSteps clickGoToMessageById(String messageId) {
        $(elements.getGoToMessageButtonByPinnedMessageId(messageId))
                .shouldBe(visible, Duration.ofSeconds(10))
                .click();
        return this;
    }

    @Step("Проверить наличие заглушки с инициалами у пользователя {userName} меню закрепленных сообщений группы")
    public void verifyUserHasInitialsAvatarPlaceholderInPinnedMessagesMenu(String userName, String phone) {
        String photo = elements.getDefinedUserWithNameAndAvatar().replace("{username}", userName);
        String placeholder = elements.getDefinedUserWithAvatarPlaceholder().replace("{username}", phone);
        String user = elements.getPinnedMessageFromUser().replace("{username}", userName);
        $(xpath(user)).shouldBe(visible);
        $(xpath(photo)).shouldNotBe(visible);
        $(xpath(placeholder)).shouldNotBe(visible);
    }

    @Step("Проверить наличие серой заглушки у пользователя {userName} меню закрепленных сообщений группы")
    public void verifyUserHasAvatarPlaceholderInPinnedMessagesMenu(String userName, String phone, boolean phoneVisible) {
        $(xpath(elements.getDefinedUserWithNameAndAvatar().replace("{username}", userName))).shouldNotBe(visible);

        String selector = phoneVisible
                ? elements.getDefinedUserWithAvatarPlaceholder().replace("{username}", phone)
                : elements.getDefinedUserWithAvatarPlaceholder().replace("{username}", userName);

        $(xpath(selector)).shouldBe(visible, Duration.ofSeconds(10));
    }

    @Step("Проверить наличие аватарки у пользователя {userName} меню закрепленных сообщений группы")
    public void verifyUserHasAvatarInPinnedMessagesMenu(String userName, String phone) {
        String photo = elements.getDefinedUserWithNameAndAvatar().replace("{username}", userName);
        String placeholder = elements.getDefinedUserWithAvatarPlaceholder().replace("{username}", phone);
        String user = elements.getPinnedMessageFromUser().replace("{username}", userName);
        $(xpath(user)).shouldBe(visible);
        $(xpath(photo)).shouldBe(visible);
        $(xpath(placeholder)).shouldNotBe(visible);
    }
}