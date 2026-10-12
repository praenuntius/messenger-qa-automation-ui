package org.foxtrot.test.in_chat_context_menu.pin_message;

import io.qameta.allure.AllureId;
import io.qameta.allure.junit5.AllureJunit5;
import org.foxtrot.enums.SystemMessageType;
import org.foxtrot.test.BaseTest;
import org.foxtrot.utils.LoginHelper;
import org.foxtrot.utils.factories.contexts.GroupContext;
import org.foxtrot.utils.factories.core.GroupFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(AllureJunit5.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PinMessageTest extends BaseTest {

    private final LoginHelper loginHelper = new LoginHelper();
    private final GroupFactory groupFactory = new GroupFactory();

    @AfterEach
    void tearDown() {
        groupFactory.cleanUp();
    }

    @AllureId("52676")
    @Test
    @DisplayName("Закрепить сообщение от OWNER") // все визуальные атрибуты тестируются здесь
    public void pinOwnerMessage() {
        GroupContext context = groupFactory.privateGroup();
        String messageId = String.valueOf(
                context.getOwnerBuilder().sendTextMessage(context.getGroupId(), "Owner text via API"));

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.topbarSteps.verifyPinButtonNotAvailable();
        pages.chatActions.findMessageById(messageId);
        pages.chatVerify.verifyPinnedMessagePictogramAbsentById(messageId);
        pages.chatActions.openMessageContextMenuById(messageId);
        pages.messageBubbleContextMenu.clickPinButton();
        pages.topbarSteps.verifyPinButtonAvailable();
        pages.chatVerify.verifyPinnedMessagePictogramPresentById(messageId);
        pages.chatVerify.verifySystemMessage(SystemMessageType.PINNED, "Owner text via API");
    }

    @AllureId("52684")
    @Test
    @DisplayName("Закрепить сообщение от ADMIN")
    public void pinAdminMessage() {
        GroupContext context = groupFactory.privateGroupWithAdmin();
        String messageId = String.valueOf(
                context.getAdminBuilder().sendTextMessage(context.getGroupId(), "Admin text via API"));

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.findMessageById(messageId);
        pages.chatActions.openMessageContextMenuById(messageId);
        pages.messageBubbleContextMenu.clickPinButton();
        pages.chatVerify.verifyPinnedMessagePictogramPresentById(messageId);
        pages.chatVerify.verifySystemMessage(SystemMessageType.PINNED, "Admin text via API");
    }

    @Test
    @DisplayName("Закрепить сообщение от PARTICIPANT")
    public void pinParticipantMessage() {
        GroupContext context = groupFactory.privateGroupWithParticipant();
        String messageId = String.valueOf(
                context.getParticipantBuilder().sendTextMessage(context.getGroupId(), "Participant text via API"));

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.findMessageById(messageId);
        pages.chatActions.openMessageContextMenuById(messageId);
        pages.messageBubbleContextMenu.clickPinButton();
        pages.chatVerify.verifyPinnedMessagePictogramPresentById(messageId);
        pages.chatVerify.verifySystemMessage(SystemMessageType.PINNED, "Participant text via API");
    }
}
