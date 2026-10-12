package org.foxtrot.pages.chat.topbar;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.foxtrot.pages.chat.right_sidebar_menu.primary_bar.elements.ChannelMenu;
import org.foxtrot.pages.chat.right_sidebar_menu.primary_bar.elements.GroupMenu;
import org.foxtrot.pages.chat.right_sidebar_menu.primary_bar.elements.P2pChatMenu;
import org.foxtrot.pages.chat.topbar.elements.Topbar;
import org.foxtrot.pages.modals.WebOnboardingOverlay;
import org.openqa.selenium.By;

import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;

public class TopbarSteps {

    private final Topbar elements = new Topbar();
    private final P2pChatMenu p2pChatMenu = new P2pChatMenu();
    private final GroupMenu groupMenu = new GroupMenu();
    private final ChannelMenu channelMenu = new ChannelMenu();

    @Step("Открыть меню закрепленных сообщений")
    public TopbarSteps clickPinnedMessageButton() {
        WebOnboardingOverlay.dismissIfPresent();
        SelenideElement headerPin = $(elements.getPinnedMessagesMenuButton());
        if (headerPin.exists() && headerPin.isDisplayed()) {
            headerPin.shouldBe(clickable, Duration.ofSeconds(10)).click();
            return this;
        }
        openChatInfoIfNeeded();
        sidebarPinnedMessagesButton()
                .shouldBe(clickable, Duration.ofSeconds(10))
                .click();
        return this;
    }

    @Step("Проверить наличие заглушки с инициалами у пользователя в хедере p2p чата")
    public TopbarSteps verifyP2pHeaderHasInitialsAvatarPlaceholder() {
        $(elements.getUserPhoto()).shouldNotBe(visible);
        $(elements.getUserAvatarPlaceholder()).shouldNotBe(visible);
        return this;
    }

    @Step("Проверить наличие серой заглушки у пользователя в хедере p2p чата")
    public TopbarSteps verifyP2pHeaderHasAvatarPlaceholder() {
        $(elements.getUserPhoto()).shouldNotBe(visible);
        $(elements.getUserAvatarPlaceholder()).shouldBe(visible);
        return this;
    }

    @Step("Проверить наличие аватарки у пользователя в хедере p2p чата")
    public TopbarSteps verifyP2pHeaderHasAvatar() {
        $(elements.getUserPhoto()).shouldBe(visible);
        $(elements.getUserAvatarPlaceholder()).shouldNotBe(visible);
        return this;
    }

    @Step("Проверить, что отображается индикатор набора текста от {userName}")
    public TopbarSteps verifyTypingIndicatorIsVisible(String userName) {
        $(By.xpath(elements.getDefinedTypingIndicator().replace("{username}", userName)))
                .shouldBe(visible);
        return this;
    }

    @Step("Открыть меню P2p-чата")
    public TopbarSteps clickP2pHeader() {
        WebOnboardingOverlay.dismissIfPresent();
        $(elements.getP2pChatHeader()).shouldBe(clickable, Duration.ofSeconds(10)).click();
        return this;
    }

    @Step("Открыть меню группы")
    public TopbarSteps clickGroupHeader() {
        $(elements.getGroupHeader()).click();
        return this;
    }

    @Step("Открыть меню канала")
    public TopbarSteps clickChannelHeader() {
        $(elements.getChannelHeader()).click();
        return this;
    }

    @Step("Нажать кнопку поиска в топбаре")
    public TopbarSteps clickSearchButton() {
        $(elements.getSearchButton()).click();
        return this;
    }

    @Step("Открыть меню настроек чата в топбаре")
    public TopbarSteps clickPreferencesMenuButton() {
        WebOnboardingOverlay.dismissIfPresent();
        $(elements.getPreferencesMenuButton())
                .shouldBe(clickable, Duration.ofSeconds(15))
                .click();
        return this;
    }

    @Step("Проверить, что название чата равно {chatName}")
    public void verifyChatName(String chatName) {
        $(elements.getChatTitle())
                .shouldBe(visible, Duration.ofSeconds(30))
                .shouldHave(text(chatName));
    }

    @Step("Проверить, что кнопка закрепленных сообщений доступна в топбаре")
    public TopbarSteps verifyPinButtonAvailable() {
        WebOnboardingOverlay.dismissIfPresent();
        SelenideElement headerPin = $(elements.getPinnedMessagesMenuButton());
        if (headerPin.exists() && headerPin.isDisplayed()) {
            headerPin.shouldBe(visible, Duration.ofSeconds(10));
            return this;
        }
        openChatInfoIfNeeded();
        sidebarPinnedMessagesButton()
                .shouldBe(visible, Duration.ofSeconds(10));
        return this;
    }

    @Step("Проверить, что кнопка закрепленных сообщений недоступна в топбаре")
    public TopbarSteps verifyPinButtonNotAvailable() {
        $(elements.getPinnedMessagesMenuButton())
                .shouldBe(not(visible), Duration.ofSeconds(10));
        return this;
    }

    @Step("Проверить, что на иконке закрепленных сообщений отображается индикатор непрочитанности")
    public TopbarSteps verifyPinnedMessagesUnreadIndicatorIsVisible() {
        $(elements.getPinnedMessagesUnreadBadge())
                .shouldBe(visible, Duration.ofSeconds(10));
        return this;
    }

    @Step("Проверить чат 'Сохраненные' открыт")
    public TopbarSteps verifySavedChatOpened() {
        try {
            $(elements.getSavedChatTitle())
                    .shouldBe(visible, Duration.ofSeconds(10))
                    .shouldHave(text("Сохраненные"));
        } catch (Exception e) {
            $(elements.getSavedChatTitleFallback())
                    .shouldBe(visible, Duration.ofSeconds(10))
                    .shouldHave(text("Сохраненные"));
        }
        return this;
    }

    private void openChatInfoIfNeeded() {
        if (isDisplayed(p2pChatMenu.getPinnedMessagesButton())
                || isDisplayed(groupMenu.getPinnedMessagesButton())
                || isDisplayed(channelMenu.getPinnedMessagesButton())) {
            return;
        }
        if ($(elements.getGroupHeader()).is(visible)) {
            $(elements.getGroupHeader()).click();
        } else if ($(elements.getChannelHeader()).is(visible)) {
            $(elements.getChannelHeader()).click();
        } else if ($(elements.getP2pChatHeader()).is(visible)) {
            $(elements.getP2pChatHeader()).click();
        }
    }

    private SelenideElement sidebarPinnedMessagesButton() {
        if (isDisplayed(p2pChatMenu.getPinnedMessagesButton())) {
            return $(p2pChatMenu.getPinnedMessagesButton());
        }
        if (isDisplayed(groupMenu.getPinnedMessagesButton())) {
            return $(groupMenu.getPinnedMessagesButton());
        }
        return $(channelMenu.getPinnedMessagesButton());
    }

    private static boolean isDisplayed(By locator) {
        SelenideElement element = $(locator);
        return element.exists() && element.isDisplayed();
    }
}
