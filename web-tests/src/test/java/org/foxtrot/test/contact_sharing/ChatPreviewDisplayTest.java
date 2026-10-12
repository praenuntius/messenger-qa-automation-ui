package org.foxtrot.test.contact_sharing;

import io.qameta.allure.AllureId;
import io.qameta.allure.junit5.AllureJunit5;
import org.foxtrot.allure.ChannelAllureIds;
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
public class ChatPreviewDisplayTest extends BaseTest {

    private final LoginHelper loginHelper = new LoginHelper();
    private final GroupFactory groupFactory = new GroupFactory();

    private GroupContext context;

    @AfterEach
    public void tearDown() {
        groupFactory.cleanUp();
    }

    @Test
    @ChannelAllureIds(web = "58386", desktop = "61891")
    @DisplayName("Проверить отображение карточки контакта в списке чатов")
    public void contactCardDisplayedInChatPreview() {
        context = groupFactory.p2pChat();
        var owner = context.getOwnerBuilder();
        var participant = context.getParticipant();
        String contactName = participant.getName();

        owner.addContacts(participant);
        owner.sendContact(context.getGroupId(), participant.getId());

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.verifyContactPreview(contactName, contactName);
    }
}