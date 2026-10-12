package org.foxtrot.test.in_chat_context_menu.pin_message;

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
public class UnpinMessageTest extends BaseTest {

    private final LoginHelper loginHelper = new LoginHelper();
    private final GroupFactory groupFactory = new GroupFactory();

    @AfterEach
    void tearDown() {
        groupFactory.cleanUp();
    }

    @Test
    @DisplayName("Открепить собственное сообщение OWNER")
    public void unpinOwnerMessage() {
        GroupContext context = groupFactory.privateGroup();
        String messageId = String.valueOf(
                context.getOwnerBuilder().sendTextMessage(context.getGroupId(), "Owner text via API"));
        context.getOwnerBuilder().pinMessage(context.getGroupId(), messageId);

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.topbarSteps.verifyPinButtonAvailable();
        pages.chatActions.findMessageById(messageId);
        pages.chatVerify.verifyPinnedMessagePictogramPresentById(messageId);
        pages.chatActions.openMessageContextMenuById(messageId);
        pages.messageBubbleContextMenu.clickPinButton();
        pages.unpinMessageModalSteps.clickUnpinButton();
        pages.chatVerify.verifyPinnedMessagePictogramAbsentById(messageId);
        pages.chatVerify.verifySystemMessage(SystemMessageType.UNPINNED, "Owner text via API");
    }

    @Test
    @DisplayName("Открепить чужое сообщение от ADMIN")
    public void unpinAdminMessage() {
        GroupContext context = groupFactory.privateGroupWithAdmin();
        String messageId = String.valueOf(
                context.getAdminBuilder().sendTextMessage(context.getGroupId(), "Admin text via API"));
        context.getOwnerBuilder().pinMessage(context.getGroupId(), messageId);

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.findMessageById(messageId);
        pages.chatVerify.verifyPinnedMessagePictogramPresentById(messageId);
        pages.chatActions.openMessageContextMenuById(messageId);
        pages.messageBubbleContextMenu.clickPinButton();
        pages.unpinMessageModalSteps.clickUnpinButton();
        pages.chatVerify.verifyPinnedMessagePictogramAbsentById(messageId);
        pages.chatVerify.verifySystemMessage(SystemMessageType.UNPINNED, "Admin text via API");
    }

    @Test
    @DisplayName("Открепить чужое сообщение от PARTICIPANT")
    public void unpinParticipantMessage() {
        GroupContext context = groupFactory.privateGroupWithParticipant();
        String messageId = String.valueOf(
                context.getParticipantBuilder().sendTextMessage(context.getGroupId(), "Participant text via API"));
        context.getOwnerBuilder().pinMessage(context.getGroupId(), messageId);

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.topbarSteps.verifyPinButtonAvailable();
        pages.chatActions.findMessageById(messageId);
        pages.chatVerify.verifyPinnedMessagePictogramPresentById(messageId);
        pages.chatActions.openMessageContextMenuById(messageId);
        pages.messageBubbleContextMenu.clickPinButton();
        pages.unpinMessageModalSteps.clickUnpinButton();
        pages.topbarSteps.verifyPinButtonNotAvailable();
        pages.chatVerify.verifyPinnedMessagePictogramAbsentById(messageId);
        pages.chatVerify.verifySystemMessage(SystemMessageType.UNPINNED, "Participant text via API");
        pages.topbarSteps.verifyPinButtonNotAvailable();
    }
}
