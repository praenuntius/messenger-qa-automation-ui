package org.foxtrot.test.in_chat_context_menu.reactions;

import io.qameta.allure.junit5.AllureJunit5;
import org.foxtrot.test.BaseTest;
import org.foxtrot.utils.LoginHelper;
import org.foxtrot.utils.factories.contexts.GroupContext;
import org.foxtrot.utils.factories.core.GroupFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;

@Disabled
@ExtendWith(AllureJunit5.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ReactionsAndViewsTest extends BaseTest {

    private final LoginHelper loginHelper = new LoginHelper();
    private final GroupFactory groupFactory = new GroupFactory();

    @AfterEach
    void tearDown() {
        groupFactory.cleanUp();
    }

    @Test
    @DisplayName("Просмотрено и Реакции кнопки отсутствуют в контекстном меню")
    public void viewedAndReactionsAreAbsentOnOwnMessage() {
        GroupContext context = groupFactory.privateGroupWithAdmin();
        context.getOwnerBuilder().sendTextMessage(context.getGroupId(), "Owner text via API");

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.findMessageByText("Owner text via API");
        pages.chatActions.openMessageContextMenu("Owner text via API");
        pages.chatVerify.verifyContextMenuButtonAbsent(pages.messageBubbleContextMenu.getMessageViewedButton());
        pages.chatVerify.verifyContextMenuButtonAbsent(pages.messageBubbleContextMenu.getMessageReactionsButton());
    }

    @Test
    @DisplayName("Просмотрено кнопка представлена в контекстном меню")
    public void viewedButtonPresentOnOwnMessage() {
        GroupContext context = groupFactory.privateGroupWithAdmin();
        String ownerMessageId = String.valueOf(
                context.getOwnerBuilder().sendTextMessage(context.getGroupId(), "Owner text via API"));

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        context.getAdminBuilder().confirmMessage(Long.valueOf(ownerMessageId), context.getGroupId());
        pages.chatActions.findMessageByText("Owner text via API");
        pages.chatActions.openMessageContextMenu("Owner text via API");
        pages.chatVerify.verifyContextMenuButtonVisible(pages.messageBubbleContextMenu.getMessageViewedButton());
    }

    @Test
    @DisplayName("Просмотреть реакции кнопка представлена в контекстном меню")
    public void reactionsButtonPresentOnOwnMessage() {
        GroupContext context = groupFactory.privateGroupWithAdmin();
        String ownerMessageId = String.valueOf(
                context.getOwnerBuilder().sendTextMessage(context.getGroupId(), "Owner text via API"));

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        context.getAdminBuilder().addReaction(Long.parseLong(ownerMessageId), context.getGroupId(), "smile");
        pages.chatActions.findMessageByText("Owner text via API");
        pages.chatActions.openMessageContextMenu("Owner text via API");
        pages.chatVerify.verifyContextMenuButtonVisible(pages.messageBubbleContextMenu.getMessageReactionsButton());
    }
}
