package org.foxtrot.test.send_messages;

import io.qameta.allure.AllureId;
import io.qameta.allure.junit5.AllureJunit5;
import org.foxtrot.test.BaseTest;
import org.foxtrot.utils.LoginHelper;
import org.foxtrot.utils.factories.contexts.GroupContext;
import org.foxtrot.utils.factories.core.GroupFactory;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(AllureJunit5.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TextFormattingTest extends BaseTest {

    private final LoginHelper loginHelper = new LoginHelper();
    private final GroupFactory groupFactory = new GroupFactory();
    private final String formattedText = "FormattedText";
    private final String link = "https://www.google.com/";

    @AfterEach
    void tearDown() {
        groupFactory.cleanUp();
    }

    @Tag("FormatingTest")
    @Test
    @AllureId("64244")
    @DisplayName("Проверка вставки символов в середину форматированного текста")
    public void insertSymbolsIntoMiddleOfFormattedText() {
        GroupContext context = groupFactory.privateGroup();
        String insertedText = String.valueOf(System.currentTimeMillis());
        String expectedMessageText = formattedText.substring(0, formattedText.length() / 2)
                + insertedText
                + formattedText.substring(formattedText.length() / 2);

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());

        pages.inputBarSteps.clickMessageInputField()
                .enterMessage(formattedText)
                .selectAllTextByTripleClick()
                .pressCmdS()
                .clickMessageInputField()
                .putCursorInMiddleOfText(formattedText)
                .pasteMessage(insertedText)
                .verifyStrikethroughInInput(expectedMessageText);

        pages.chatActions.sendMessageWithEnterKey();

        pages.chatVerify.verifyStrikethroughInMessage(expectedMessageText);
    }

    @Tag("FormatingTest")
    @Test
    @AllureId("64242")
    @DisplayName("Проверка вставки символов перед форматированным текстом")
    public void insertSymbolsBeforeFormattedText() {
        GroupContext context = groupFactory.privateGroup();
        String insertedText = String.valueOf(System.currentTimeMillis());
        String expectedMessageText = insertedText + formattedText;

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());

        pages.inputBarSteps.clickMessageInputField()
                .enterMessage(formattedText)
                .selectAllTextByTripleClick()
                .pressCmdS()
                .clickMessageInputField()
                .moveToLineStart()
                .pasteMessage(insertedText)
                .checkMessageFieldContainsText(expectedMessageText)
                .verifyPlainAndStrikethroughTextInInput(expectedMessageText, insertedText, formattedText)
                .clickSendMessageButton();

        pages.chatVerify.verifyMessageText(expectedMessageText)
                .verifyPartlyStrikethroughTextInLastMessage(expectedMessageText, insertedText, formattedText);
    }

    @Tag("FormatingTest")
    @Test
    @AllureId("64245")
    @DisplayName("Проверка вставки символов после форматированного текста")
    public void insertSymbolsAfterFormattedText() {
        GroupContext context = groupFactory.privateGroup();
        String insertedText = String.valueOf(System.currentTimeMillis());
        String expectedMessageText = formattedText + insertedText;

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());

        pages.inputBarSteps.clickMessageInputField()
                .enterMessage(formattedText)
                .selectAllTextByTripleClick()
                .pressCmdS()
                .clickMessageInputField()
                .pasteMessage(insertedText)
                .checkMessageFieldContainsText(expectedMessageText)
                .verifyPlainAndStrikethroughTextInInput(expectedMessageText, insertedText, formattedText)
                .clickSendMessageButton();

        pages.chatVerify.verifyMessageText(expectedMessageText)
                .verifyPartlyStrikethroughTextInLastMessage(expectedMessageText, insertedText, formattedText);
    }

    @Tag("FormatingTest")
    @Test
    @AllureId("59800")
    @DisplayName("Проверка добавление стилей на текст. Редактирование")
    public void addStylesToTextWhileEditingMessage() {
        GroupContext context = groupFactory.privateGroup();
        String messageId = String.valueOf(
                context.getOwnerBuilder().sendTextMessage(context.getGroupId(), formattedText));

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());

        pages.chatActions.openMessageContextMenuById(messageId);
        pages.messageBubbleContextMenu.clickEditButton();

        pages.inputBarSteps.checkMessageFieldContainsText(formattedText)
                .selectAllTextByTripleClick()
                .pressCmdB()
                .pressCmdI()
                .pressCmdU()
                .pressCmdS()
                .pressCmdShiftM()
                .verifyAllStylesAppliedToTextInInput(formattedText);

        pages.chatActions.sendMessageWithEnterKey();

        pages.chatVerify.verifyAllStylesInMessage(formattedText);
    }

    @Tag("FormatingTest")
    @Test
    @AllureId("59803")
    @DisplayName("Проверка изменения сообщения в закрепленных сообщениях")
    public void editPinnedMessage() {
        GroupContext context = groupFactory.privateGroup();
        Long messageId = context.getOwnerBuilder().sendTextMessage(context.getGroupId(), formattedText);
        String messageIdText = String.valueOf(messageId);
        context.getOwnerBuilder().formatTextMessage(context.getGroupId(), messageId, formattedText);
        context.getOwnerBuilder().pinMessage(context.getGroupId(), messageIdText);

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());

        pages.chatVerify.verifyPinnedMessagePictogramPresentById(messageIdText)
                .verifyAllStylesInMessage(formattedText);

        pages.chatActions.openMessageContextMenuById(messageIdText);
        pages.messageBubbleContextMenu.clickEditButton();

        pages.inputBarSteps.checkMessageFieldContainsText(formattedText)
                .verifyAllStylesAppliedToTextInInput(formattedText)
                .selectAllTextByTripleClick()
                .pressCmdU();

        pages.chatActions.sendMessageWithEnterKey();

        pages.chatVerify.verifyPinnedMessagePictogramPresentById(messageIdText)
                .verifyBoldInMessage(formattedText)
                .verifyItalicInMessage(formattedText)
                .verifyStrikethroughInMessage(formattedText)
                .verifyMonospaceInMessage(formattedText)
                .verifyUnderlinedAbsentInMessage(formattedText);

        pages.topbarSteps.clickPinnedMessageButton();
        pages.pinnedMessageMenuSteps.verifyPinnedMessagesSidebarIsVisible()
                .verifyAllStylesExceptUnderlineInPinnedMessage(messageIdText, formattedText);
    }

    @Tag("FormatingTest")
    @Test
    @AllureId("64249")
    @DisplayName("Проверка наложения всех стилей горячими клавишами")
    public void applyAllStylesWithHotkeysToText() {
        GroupContext context = groupFactory.privateGroup();

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());

        pages.inputBarSteps.clickMessageInputField()
                .enterMessage(formattedText)
                .selectAllTextByTripleClick()
                .pressCmdB()
                .pressCmdU()
                .pressCmdI()
                .pressCmdS()
                .pressCmdShiftM()
                .pressCmdК();

        pages.formatTextWithLinkModalSteps.formatTextWithLink(formattedText, link);

        pages.inputBarSteps
                .verifyAllStylesAppliedToTextInInput(formattedText)
                .verifyTextIsLinkInInput(formattedText)
                .clickMessageInputField();

        pages.chatActions.sendMessageWithEnterKey();

        pages.chatVerify.verifyAllStylesInMessage(formattedText)
                .verifyMessageLinkText(formattedText);
    }

    @Tag("FormatingTest")
    @Test
    @AllureId("59790")
    @DisplayName("Проверка наложения всех стилей на текст через контекстное меню")
    public void applyAllStylesWithContextMenuToText() {
        GroupContext context = groupFactory.privateGroup();

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());

        pages.inputBarSteps.clickMessageInputField()
                .enterMessage(formattedText)
                .selectAllTextByTripleClick();

        pages.contextMenuSteps.applyAllStylesToSelectedText();
        pages.inputBarSteps.rightClickMessageInputField();
        pages.contextMenuSteps.clickFormattingButton()
                .clickAddLinkButton();
        pages.formatTextWithLinkModalSteps.formatTextWithLink(formattedText, link);

        pages.inputBarSteps.rightClickMessageInputField();
        pages.contextMenuSteps.clickFormattingButton()
                .verifyAllSelectedStylesHaveChecks();

        pages.inputBarSteps.verifyAllStylesAppliedToTextInInput(formattedText)
                .verifyTextIsLinkInInput(formattedText);

        pages.inputBarSteps.clickMessageInputField();
        pages.chatActions.sendMessageWithEnterKey();

        pages.chatVerify.verifyAllStylesInMessage(formattedText)
                .verifyMessageLinkText(formattedText);

        pages.chatActions.findAndClickLinkMessageByText(formattedText);
        pages.openTheLinkModalSteps.clickOpenButton();
        pages.chatVerify.verifyLinkOpensInNewTab(link);
    }

    @Tag("FormatingTest")
    @Test
    @AllureId("59797")
    @DisplayName("Проверка удаления одного стиля при миксе стилей. горячие клавиши")
    public void removeOneStyleFromMixedStylesWithHotkeys() {
        GroupContext context = groupFactory.privateGroup();

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());

        pages.inputBarSteps.clickMessageInputField()
                .enterMessage(formattedText)
                .selectAllTextByTripleClick()
                .pressCmdB()
                .pressCmdI()
                .pressCmdU()
                .pressCmdS()
                .pressCmdShiftM()
                .verifyAllStylesAppliedToTextInInput(formattedText)
                .pressCmdS()
                .verifyStrikethroughAbsentInInput(formattedText)
                .verifyBoldInInput(formattedText)
                .verifyItalicInInput(formattedText)
                .verifyUnderlinedInInput(formattedText)
                .verifyMonospaceInInput(formattedText);

        pages.chatActions.sendMessageWithEnterKey();

        pages.chatVerify.verifyBoldInMessage(formattedText)
                .verifyItalicInMessage(formattedText)
                .verifyUnderlinedInMessage(formattedText)
                .verifyMonospaceInMessage(formattedText)
                .verifyStrikethroughAbsentInMessage(formattedText);
    }

    @Tag("FormatingTest")
    @Test
    @AllureId("59796")
    @DisplayName("Проверка удаления стилей с помощью горячих клавиш")
    public void removeStylesWithHotkeys() {
        GroupContext context = groupFactory.privateGroup();

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());

        pages.inputBarSteps.clickMessageInputField()
                .enterMessage(formattedText)
                .selectAllTextByTripleClick()
                .pressCmdB()
                .verifyBoldInInput(formattedText)
                .pressCmdB()
                .verifyBoldAbsentInInput(formattedText)
                .pressCmdI()
                .verifyItalicInInput(formattedText)
                .pressCmdI()
                .verifyItalicAbsentInInput(formattedText)
                .pressCmdU()
                .verifyUnderlinedInInput(formattedText)
                .pressCmdU()
                .verifyUnderlinedAbsentInInput(formattedText)
                .pressCmdS()
                .verifyStrikethroughInInput(formattedText)
                .pressCmdS()
                .verifyStrikethroughAbsentInInput(formattedText)
                .pressCmdShiftM()
                .verifyMonospaceInInput(formattedText)
                .pressCmdShiftM()
                .verifyMonospaceAbsentInInput(formattedText);
    }

    @Tag("FormatingTest")
    @Test
    @AllureId("64250")
    @DisplayName("Проверка удаления стилей через контекстное меню")
    public void removeStylesWithContextMenu() {
        GroupContext context = groupFactory.privateGroup();

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());

        pages.inputBarSteps.clickMessageInputField()
                .enterMessage(formattedText)
                .selectAllTextByTripleClick()
                .rightClickMessageInputField();

        pages.contextMenuSteps.clickFormattingButton()
                .verifyFormattingSubMenuVisible()
                .clickBoldButton()
                .verifyFormattingSubMenuClosed();

        pages.inputBarSteps.verifyBoldInInput(formattedText)
                .rightClickMessageInputField();

        pages.contextMenuSteps.clickFormattingButton()
                .verifyFormattingSubMenuVisible()
                .verifyBoldStyleHasCheck()
                .clickBoldButton()
                .verifyFormattingSubMenuClosed();

        pages.inputBarSteps.verifyBoldAbsentInInput(formattedText);
    }

    @Tag("FormatingTest")
    @Test
    @AllureId("59798")
    @DisplayName("Удаления части форматированного текста")
    public void deletePartOfFormattedText() {
        GroupContext context = groupFactory.privateGroup();
        String textToDelete = "Пока зачем почему кто";

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());

        pages.inputBarSteps.clickMessageInputField()
                .enterMessage(textToDelete)
                .selectAllTextByTripleClick()
                .pressCmdB()
                .pressCmdI()
                .pressCmdU()
                .pressCmdS()
                .pressCmdShiftM()
                .verifyAllStylesAppliedToTextInInput(textToDelete)
                .clickMessageInputField()
                .deleteCharactersBeforeCursor(textToDelete.length() - 1)
                .verifyAllStylesAppliedToTextInInput(textToDelete.substring(0, 1));
    }
}
