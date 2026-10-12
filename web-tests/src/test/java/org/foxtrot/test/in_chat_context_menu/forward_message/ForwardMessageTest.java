package org.foxtrot.test.in_chat_context_menu.forward_message;

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

@ExtendWith(AllureJunit5.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ForwardMessageTest extends BaseTest {

    private final LoginHelper loginHelper = new LoginHelper();
    private final GroupFactory groupFactory = new GroupFactory();

    @AfterEach
    void tearDown() {
        groupFactory.cleanUp();
    }

    @AllureId("58384") //also: 56724 кнопка пересылки - открытие окна
    @Test
    @DisplayName("Переслать собственное сообщение в группу хоумспейса, проверка основных элементов меню форварда")
    public void forwardOwnMessage() {
        GroupContext context = groupFactory.privateGroup();
        String messageId = String.valueOf(
                context.getOwnerBuilder().sendTextMessage(context.getGroupId(), "Owner text via API"));

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.findMessageById(messageId);
        pages.chatActions.openMessageContextMenuById(messageId);
        pages.messageBubbleContextMenu.clickForwardButton();
        pages.forwardMenuSteps.verifyForwardMenuAvailable();
        pages.forwardMenuSteps.saveForwardMessageText();
        pages.forwardMenuSteps.forwardMessageIntoChat(context.getGroupName());
        pages.forwardMenuSteps.verifyForwardedMessageInChat();
    }
}
