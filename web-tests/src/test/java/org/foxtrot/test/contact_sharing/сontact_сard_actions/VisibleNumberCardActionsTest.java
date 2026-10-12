package org.foxtrot.test.contact_sharing.сontact_сard_actions;

import org.foxtrot.allure.ChannelAllureIds;
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
public class VisibleNumberCardActionsTest extends BaseTest {

    private final LoginHelper loginHelper = new LoginHelper();
    private final GroupFactory groupFactory = new GroupFactory();

    private GroupContext context;

    @AfterEach
    public void tearDown() {
        groupFactory.cleanUp();
    }

    @Test
    @ChannelAllureIds(web = "57855", desktop = "61863")
    @DisplayName("Действие с карточкой контакта пользователя Tango при клике на номер")
    public void copyPhoneNumberFromContactCardTest() {
        context = groupFactory.privateGroupWithParticipant();
        context.getOwnerBuilder().addContacts(context.getParticipant());

        Long messageId = context.getOwnerBuilder().sendContact(
                context.getGroupId(),
                context.getParticipant().getId()
        );

        loginHelper.performUILogin(context.getOwner());

        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.hoverMessageById(messageId).clickCopyContactButton();
        pages.inputBarSteps.pasteTextIntoInput().clickSendMessageButton();

        pages.chatVerify.verifyMessageText(context.getParticipant().getFormattedPhone());
    }


    @Test
    @ChannelAllureIds(web = "57856", desktop = "61867")
    @DisplayName("Меню действий с карточкой неконтакта пользователя Tango при клике на номер")
    public void copyPhoneNumberFromNonContactCardTest() {
        context = groupFactory.privateGroupWithParticipant();

        Long messageId = context.getOwnerBuilder().sendContact(
                context.getGroupId(),
                context.getParticipant().getId()
        );

        loginHelper.performUILogin(context.getOwner());

        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.hoverMessageById(messageId).clickCopyContactButton();
        pages.inputBarSteps.pasteTextIntoInput().clickSendMessageButton();

        pages.chatVerify.verifyMessageText(context.getParticipant().getFormattedPhone());
    }
}
