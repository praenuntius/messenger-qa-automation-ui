package org.foxtrot.test.in_chat_context_menu;

import io.qameta.allure.AllureId;
import io.qameta.allure.junit5.AllureJunit5;
import org.foxtrot.test.BaseTest;
import org.foxtrot.utils.LoginHelper;
import org.foxtrot.utils.factories.contexts.GroupContext;
import org.foxtrot.utils.factories.core.GroupFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;

import static com.codeborne.selenide.Selenide.sleep;

@ExtendWith(AllureJunit5.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MultiselectMessagesTest extends BaseTest {

    private final LoginHelper loginHelper = new LoginHelper();
    private final GroupFactory groupFactory = new GroupFactory();

    @AfterEach
    void tearDown() {
        groupFactory.cleanUp();
    }

    @AllureId("56734")
    @Test
    @DisplayName("Меню мультиселекта владельца чата - доступны все действия для сообщений: свое, админа, участника ")
    public void multiselectMessageMenuForOwner() {
        GroupContext context = groupFactory.privateGroupWithAdminAndParticipant();
        String ownerMessageId = String.valueOf(
                context.getOwnerBuilder().sendTextMessage(context.getGroupId(), "Owner text via API"));
        String adminMessageId = String.valueOf(
                context.getAdminBuilder().sendTextMessage(context.getGroupId(), "Admin text via API"));
        String participantMessageId = String.valueOf(
                context.getParticipantBuilder().sendTextMessage(context.getGroupId(), "Participant text via API"));

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.findMessageById(ownerMessageId);
        sleep(300);
        pages.chatActions.openMessageContextMenuById(ownerMessageId);
        pages.messageBubbleContextMenu.clickSelectButton();
        pages.multiselectMessagesSteps.FullMultiselectMenuAvailable();
        pages.multiselectMessagesSteps.clickCancelButton();
        sleep(300);
        pages.chatActions.openMessageContextMenuById(adminMessageId);
        pages.messageBubbleContextMenu.clickSelectButton();
        pages.multiselectMessagesSteps.FullMultiselectMenuAvailable();
        pages.multiselectMessagesSteps.clickCancelButton();
        sleep(300);
        pages.chatActions.openMessageContextMenuById(participantMessageId);
        pages.messageBubbleContextMenu.clickSelectButton();
        pages.multiselectMessagesSteps.FullMultiselectMenuAvailable();
    }

    @Test
    @DisplayName("Меню мультиселекта админа чата - доступны все действия для сообщений: свое, админа, участника ")
    public void multiselectMessageMenuForAdmin() {
        GroupContext context = groupFactory.privateGroupWithAdminAndParticipant();
        String ownerMessageId = String.valueOf(
                context.getOwnerBuilder().sendTextMessage(context.getGroupId(), "Owner text via API"));
        String adminMessageId = String.valueOf(
                context.getAdminBuilder().sendTextMessage(context.getGroupId(), "Admin text via API"));
        String participantMessageId = String.valueOf(
                context.getParticipantBuilder().sendTextMessage(context.getGroupId(), "Participant text via API"));

        loginHelper.performUILogin(context.getAdmin());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.findMessageById(ownerMessageId);
        sleep(300);
        pages.chatActions.openMessageContextMenuById(ownerMessageId);
        pages.messageBubbleContextMenu.clickSelectButton();
        pages.multiselectMessagesSteps.FullMultiselectMenuAvailable();
        pages.multiselectMessagesSteps.clickCancelButton();
        sleep(300);
        pages.chatActions.openMessageContextMenuById(adminMessageId);
        pages.messageBubbleContextMenu.clickSelectButton();
        pages.multiselectMessagesSteps.FullMultiselectMenuAvailable();
        pages.multiselectMessagesSteps.clickCancelButton();
        sleep(300);
        pages.chatActions.openMessageContextMenuById(participantMessageId);
        pages.messageBubbleContextMenu.clickSelectButton();
        pages.multiselectMessagesSteps.FullMultiselectMenuAvailable();
    }

    @Test
    @DisplayName("Меню мультиселекта участника чата - доступны все действия для собственных сообщений, удаление недоступно для сообщений владельца и админа")
    public void multiselectMessageMenuForParticipant() {
        GroupContext context = groupFactory.privateGroupWithAdminAndParticipant();
        String ownerMessageId = String.valueOf(
                context.getOwnerBuilder().sendTextMessage(context.getGroupId(), "Owner text via API"));
        String adminMessageId = String.valueOf(
                context.getAdminBuilder().sendTextMessage(context.getGroupId(), "Admin text via API"));
        String participantMessageId = String.valueOf(
                context.getParticipantBuilder().sendTextMessage(context.getGroupId(), "Participant text via API"));

        loginHelper.performUILogin(context.getParticipant());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.findMessageById(ownerMessageId);
        sleep(300);
        pages.chatActions.openMessageContextMenuById(ownerMessageId);
        pages.messageBubbleContextMenu.clickSelectButton();
        pages.multiselectMessagesSteps.FullMultiselectMenuAvailableWithDisabledDelete();
        pages.multiselectMessagesSteps.clickCancelButton();
        sleep(300);
        pages.chatActions.openMessageContextMenuById(adminMessageId);
        pages.messageBubbleContextMenu.clickSelectButton();
        pages.multiselectMessagesSteps.FullMultiselectMenuAvailableWithDisabledDelete();
        pages.multiselectMessagesSteps.clickCancelButton();
        sleep(300);
        pages.chatActions.openMessageContextMenuById(participantMessageId);
        pages.messageBubbleContextMenu.clickSelectButton();
        pages.multiselectMessagesSteps.FullMultiselectMenuAvailable();
    }
}
