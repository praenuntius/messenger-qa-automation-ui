package org.foxtrot.test.in_chat_context_menu.reactions;

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

import java.util.Objects;

import static com.codeborne.selenide.Selenide.open;

@ExtendWith(AllureJunit5.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class P2pTest extends BaseTest {

    private final LoginHelper loginHelper = new LoginHelper();
    private final GroupFactory groupFactory = new GroupFactory();

    @AfterEach
    void tearDown() {
        groupFactory.cleanUp();
    }

    @Test
    @DisplayName("Отправить реакцию на свое сообщение")
    public void sendRecentReactionOnOwnMessage() {
        GroupContext context = groupFactory.p2pChat();
        String ownMessageId = String.valueOf(
                context.getOwnerBuilder().sendTextMessage(
                        context.getGroupId(), "Owner P2P text via API"));

        loginHelper.performUILogin(context.getOwner());
        open(Objects.requireNonNull(UrlConstants.getP2pChatUrl(context.getParticipant().getId().toString())));
        pages.chatActions.findMessageById(ownMessageId);
        pages.chatActions.openMessageContextMenuById(ownMessageId);
        pages.quickReactionsUnitSteps.clickFirstAvailableReactionButton();
        pages.chatVerify.verifyReactionPresentInMessageBubbleById(
                ownMessageId,
                pages.quickReactionsUnitSteps.getLastClickedReactionEmoji()
        );
    }

    @Test
    @DisplayName("Отправить реакцию на сообщение оппонента")
    public void sendRecentReactionOnPeerMessage() {
        GroupContext context = groupFactory.p2pChat();
        String peerMessageId = String.valueOf(
                context.getParticipantBuilder().sendTextMessage(
                        context.getGroupId(), "Peer P2P text via API"));

        loginHelper.performUILogin(context.getOwner());
        open(Objects.requireNonNull(UrlConstants.getP2pChatUrl(context.getParticipant().getId().toString())));
        pages.chatActions.findMessageById(peerMessageId);
        pages.chatActions.openMessageContextMenuById(peerMessageId);
        pages.quickReactionsUnitSteps.clickFirstAvailableReactionButton();
        pages.chatVerify.verifyReactionPresentInMessageBubbleById(
                peerMessageId,
                pages.quickReactionsUnitSteps.getLastClickedReactionEmoji()
        );
    }
}
