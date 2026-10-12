package org.foxtrot.test.global_search.search_sort_navigation;

import io.qameta.allure.AllureId;
import io.qameta.allure.junit5.AllureJunit5;
import org.foxtrot.test.BaseTest;
import org.foxtrot.utils.LoginHelper;
import org.foxtrot.utils.UrlConstants;
import org.foxtrot.utils.factories.contexts.GroupContext;
import org.foxtrot.utils.factories.core.GroupFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(AllureJunit5.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LinkTest extends BaseTest {

    private final LoginHelper loginHelper = new LoginHelper();
    private final GroupFactory groupFactory = new GroupFactory();

    private GroupContext context;

    @AfterEach
    public void tearDown() {
        groupFactory.cleanUp();
    }

    @Test
    @AllureId("56470")
    @DisplayName("Контекстное меню ссылки: 'Копировать'")
    public void linkContextMenuCopyOptionTest() {
        context = groupFactory.privateGroup();
        String groupUrl = UrlConstants.getHomespaceGroupUrl(String.valueOf(context.getGroupId()));
        context.getOwnerBuilder().sendLinkMassage(groupUrl, context.getGroupId());

        loginHelper.performUILogin(context.getOwner());

        pages.groupAndChannelMenuSteps.clickSearchButton();
        pages.searchUnitSteps.enterSearchText(String.valueOf(context.getGroupId()));
        pages.searchUnitSteps.clickLinksTab();
        pages.searchUnitSteps.waitForSearchCompletion();

        pages.searchResultsListSteps.clickLinkContextMenuButton();
        pages.linksContextMenuSteps.checkAllButtonsVisible();
        pages.linksContextMenuSteps.clickCopy();

        pages.snackbarsSteps.verifyCopiedLinkSnackbarVisible();

        pages.leftSidebarSteps.clickChatsButton();
        pages.chatListSteps.clickSavedChat();
        pages.inputBarSteps.clickMessageInputField();
        pages.inputBarSteps.pasteTextIntoInput();
        pages.inputBarSteps.clickSendMessageButton();

        pages.chatVerify.verifyMessageText(groupUrl);
    }

    @Test
    @AllureId("56408")
    @DisplayName("Контекстное меню ссылки: 'Открыть в браузере'")
    void linkContextMenuOpenInBrowserOptionTest() {
        context = groupFactory.privateGroup();
        String url = "https://www.google.com/";
        context.getOwnerBuilder().sendLinkMassage(url, context.getGroupId());

        loginHelper.performUILogin(context.getOwner());

        pages.groupAndChannelMenuSteps.clickSearchButton();
        pages.searchUnitSteps.enterSearchText(url);
        pages.searchUnitSteps.clickLinksTab();
        pages.searchUnitSteps.waitForSearchCompletion();

        pages.searchResultsListSteps.clickLinkContextMenuButton();
        pages.linksContextMenuSteps.checkAllButtonsVisible();
        pages.linksContextMenuSteps.clickOpenInBrowser();

        pages.chatVerify.verifyLinkOpensInNewTab(url);
    }

    @Test
    @AllureId("56459")
    @DisplayName("Контекстное меню ссылки: 'Перейти к сообщению'")
    void linkContextMenuGoToMessageOptionTest() {
        context = groupFactory.privateGroup();
        String groupUrl = UrlConstants.getHomespaceGroupUrl(String.valueOf(context.getGroupId()));
        context.getOwnerBuilder().sendLinkMassage(groupUrl, context.getGroupId());

        loginHelper.performUILogin(context.getOwner());

        pages.groupAndChannelMenuSteps.clickSearchButton();
        pages.searchUnitSteps.enterSearchText(String.valueOf(context.getGroupId()));
        pages.searchUnitSteps.clickLinksTab();
        pages.searchUnitSteps.waitForSearchCompletion();

        pages.searchResultsListSteps.clickLinkContextMenuButton();
        pages.linksContextMenuSteps.checkAllButtonsVisible();
        pages.linksContextMenuSteps.clickGoToMessage();

        pages.topbarSteps.verifyChatName(context.getGroupName());
        pages.chatVerify.verifyMessageText(groupUrl);
        pages.leftSidebarSteps.verifySpaceIsActive("Чаты");
        pages.searchUnitSteps.verifyGlobalSearchElementsVisible();
    }

    @Test
    @AllureId("56340")
    @DisplayName("Поиск ссылки от 1 символа содержащегося в адресе и открытие в браузере")
    void linkSearchByOneSymbolAndOpenInBrowserTest() {
        context = groupFactory.privateGroup();
        String url = "https://www.google.com/";
        context.getOwnerBuilder().sendLinkMassage(url, context.getGroupId());

        loginHelper.performUILogin(context.getOwner());

        pages.groupAndChannelMenuSteps.clickSearchButton();
        pages.searchUnitSteps.enterSearchText("om/");
        pages.searchUnitSteps.clickLinksTab();
        pages.searchUnitSteps.waitForSearchCompletion();

        pages.searchResultsListSteps.clickLinkContextMenuButton();
        pages.linksContextMenuSteps.checkAllButtonsVisible();
        pages.linksContextMenuSteps.clickOpenInBrowser();

        pages.chatVerify.verifyLinkOpensInNewTab(url);
    }

    @Test
    @AllureId("56288")
    @DisplayName("Поиск ссылки от 1 символа и переход к сообщению в чате")
    void linkSearchByOneSymbolAndGoToMessageTest() {
        context = groupFactory.privateGroup();
        String url = "!@#$%^&";
        context.getOwnerBuilder().sendLinkMassage(url, context.getGroupId());

        loginHelper.performUILogin(context.getOwner());

        pages.groupAndChannelMenuSteps.clickSearchButton();
        pages.searchUnitSteps.enterSearchText("%^&");
        pages.searchUnitSteps.clickLinksTab();
        pages.searchUnitSteps.waitForSearchCompletion();

        pages.searchResultsListSteps.clickLinkContextMenuButton();
        pages.linksContextMenuSteps.checkAllButtonsVisible();
        pages.linksContextMenuSteps.clickGoToMessage();

        pages.topbarSteps.verifyChatName(context.getGroupName());
        pages.chatVerify.verifyMessageText(url);
        pages.leftSidebarSteps.verifySpaceIsActive("Чаты");
        pages.searchUnitSteps.verifyGlobalSearchElementsVisible();
    }

    @Test
    @AllureId("56471")
    @DisplayName("Контекстное меню ссылки: 'Переслать'")
    void linkContextMenuForwardOptionTest() {
        context = groupFactory.privateGroup();
        String url = "https://www.google.com/";
        context.getOwnerBuilder().sendLinkMassage(url, context.getGroupId());

        loginHelper.performUILogin(context.getOwner());

        pages.chatListSteps.clickChatByName(context.getGroupName());

        pages.groupAndChannelMenuSteps.clickSearchButton();
        pages.searchUnitSteps.enterSearchText(url);
        pages.searchUnitSteps.waitForSearchCompletion();

        pages.searchUnitSteps.clickLinksTab();
        pages.searchUnitSteps.waitForSearchCompletion();

        pages.searchResultsListSteps.clickLinkContextMenuButton();
        pages.linksContextMenuSteps.checkAllButtonsVisible();
        pages.linksContextMenuSteps.clickForward();

        pages.forwardMenuSteps.forwardMessageIntoChat("Сохраненные");

        pages.topbarSteps.verifyChatName("Сохраненные");
        pages.searchUnitSteps.verifyGlobalSearchElementsVisible();
        pages.chatVerify.verifyMessageText(url);
    }
}