package org.foxtrot.test.chats_and_spaces;

import org.foxtrot.allure.ChannelAllureIds;
import io.qameta.allure.junit5.AllureJunit5;
import org.foxtrot.enums.UsersEnum;
import org.foxtrot.test.BaseTest;
import org.foxtrot.utils.LoginHelper;
import org.foxtrot.utils.factories.contexts.GroupContext;
import org.foxtrot.utils.factories.core.GroupFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@ExtendWith(AllureJunit5.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ClearMessageHistoryTest extends BaseTest {

    private static final String TEST_MESSAGE = "Сообщение перед очисткой";

    private final LoginHelper loginHelper = new LoginHelper();
    private final GroupFactory groupFactory = new GroupFactory();

    @AfterEach
    void tearDown() {
        groupFactory.cleanUp();
    }

    @ChannelAllureIds(web = "96191", desktop = "95716")
    @ParameterizedTest
    @ValueSource(strings = {"admin", "owner", "participant"})
    @DisplayName("Кнопка 'Очистить у всех' видна только владельцу группы")
    public void clearHistoryButtonVisibleOnlyForOwnerInGroup(String role) {
        GroupContext context = groupFactory.privateGroupWithAdminAndParticipant();
        UsersEnum currentUser = switch (role) {
            case "owner" -> context.getOwner();
            case "admin" -> context.getAdmin();
            case "participant" -> context.getParticipant();
            default -> throw new IllegalArgumentException("Unknown role: " + role);
        };

        loginHelper.performUILogin(currentUser);
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.topbarSteps.clickGroupHeader();
        pages.groupMenuSteps.clickClearHistoryButton();

        pages.cleanMessageHistoryModalSteps.verifyClearHistoryForAllButtonVisibility("owner".equals(role));
    }

    @ChannelAllureIds(web = "96192", desktop = "95717")
    @ParameterizedTest
    @ValueSource(strings = {"admin", "owner", "participant"})
    @DisplayName("Кнопка 'Очистить у всех' видна только владельцу группы в рабочем пространстве")
    public void clearHistoryButtonVisibleOnlyForOwnerInWorkspaceGroup(String role) {
        GroupContext context = groupFactory.withParticipant(
                groupFactory.withChatAdmin(groupFactory.privateGroupInWs()));
        UsersEnum currentUser = switch (role) {
            case "owner" -> context.getOwner();
            case "admin" -> context.getAdmin();
            case "participant" -> context.getParticipant();
            default -> throw new IllegalArgumentException("Unknown role: " + role);
        };

        loginHelper.performUILogin(currentUser);
        pages.leftSidebarSteps.clickSpaceByName(context.getWorkspaceName());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.topbarSteps.clickGroupHeader();
        pages.groupMenuSteps.clickClearHistoryButton();

        pages.cleanMessageHistoryModalSteps.verifyClearHistoryForAllButtonVisibility("owner".equals(role));
    }

    @Test
    @ChannelAllureIds(web = "64265", desktop = "95670")
    @DisplayName("История сообщений очищается у всех участников группы в домашнем пространстве после действия владельца")
    public void clearHistoryPropagatesToAllParticipantsInHomespaceGroup() {
        GroupContext context = groupFactory.privateGroupWithAdminAndParticipant();
        context.getAdminBuilder().sendTextMessage(context.getGroupId(), TEST_MESSAGE);

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.topbarSteps.clickGroupHeader();
        pages.groupMenuSteps.clickClearHistoryButton();
        pages.cleanMessageHistoryModalSteps.clickClearForAllButton();
        pages.chatVerify.verifyClearMessageHistorySystemMessage();
        pages.chatVerify.verifyMessageWithTextIsNotDisplayed(TEST_MESSAGE, 10);

        loginHelper.performUILoginForTest(context.getAdmin());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatVerify.verifyClearMessageHistorySystemMessage();
        pages.chatVerify.verifyMessageWithTextIsNotDisplayed(TEST_MESSAGE, 10);

        loginHelper.performUILoginForTest(context.getParticipant());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatVerify.verifyClearMessageHistorySystemMessage();
        pages.chatVerify.verifyMessageWithTextIsNotDisplayed(TEST_MESSAGE, 10);
    }

    @Test
    @ChannelAllureIds(web = "64264", desktop = "95720")
    @DisplayName("Очистка истории сообщений 'у себя' в роли владельца группы в домашнем пространстве")
    public void clearHistoryForSelfAsOwnerInHomespaceGroup() {
        GroupContext context = groupFactory.privateGroupWithAdminAndParticipant();
        context.getAdminBuilder().sendTextMessage(context.getGroupId(), TEST_MESSAGE);

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.topbarSteps.clickGroupHeader();
        pages.groupMenuSteps.clickClearHistoryButton();
        pages.cleanMessageHistoryModalSteps.clickClearForSelfButton();
        pages.snackbarsSteps.verifyClearHistoryMessageIsVisible();
        pages.chatVerify.verifyMessageWithTextIsNotDisplayed(TEST_MESSAGE, 10);

        loginHelper.performUILoginForTest(context.getAdmin());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatVerify.verifyMessageSent(TEST_MESSAGE);

        loginHelper.performUILoginForTest(context.getParticipant());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatVerify.verifyMessageSent(TEST_MESSAGE);
    }

    @Test
    @ChannelAllureIds(web = "64268", desktop = "95721")
    @DisplayName("Очистка истории сообщений 'у себя' в роли владельца группы в рабочем пространстве")
    public void clearHistoryForSelfAsOwnerInWorkSpaceGroup() {
        GroupContext context = groupFactory.withParticipant(
                groupFactory.withChatAdmin(groupFactory.privateGroupInWs()));
        context.getAdminBuilder().sendTextMessage(
                context.getGroupId(), TEST_MESSAGE, String.valueOf(context.getWorkspaceId()));

        loginHelper.performUILogin(context.getOwner());
        pages.leftSidebarSteps.clickSpaceByName(context.getWorkspaceName());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.topbarSteps.clickGroupHeader();
        pages.groupMenuSteps.clickClearHistoryButton();
        pages.cleanMessageHistoryModalSteps.clickClearForSelfButton();
        pages.snackbarsSteps.verifyClearHistoryMessageIsVisible();
        pages.chatVerify.verifyMessageWithTextIsNotDisplayed(TEST_MESSAGE, 10);

        loginHelper.performUILoginForTest(context.getAdmin());
        pages.leftSidebarSteps.clickSpaceByName(context.getWorkspaceName());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatVerify.verifyMessageSent(TEST_MESSAGE);

        loginHelper.performUILoginForTest(context.getParticipant());
        pages.leftSidebarSteps.clickSpaceByName(context.getWorkspaceName());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatVerify.verifyMessageSent(TEST_MESSAGE);
    }

    @Test
    @ChannelAllureIds(web = "64269", desktop = "95715")
    @DisplayName("История сообщений очищается у всех участников группы в рабочем пространстве после действия владельца")
    public void clearHistoryPropagatesToAllParticipantsInWorkspaceGroup() {
        GroupContext context = groupFactory.withParticipant(
                groupFactory.withChatAdmin(groupFactory.privateGroupInWs()));
        context.getAdminBuilder().sendTextMessage(
                context.getGroupId(), TEST_MESSAGE, String.valueOf(context.getWorkspaceId()));

        loginHelper.performUILogin(context.getOwner());
        pages.leftSidebarSteps.clickSpaceByName(context.getWorkspaceName());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.topbarSteps.clickGroupHeader();
        pages.groupMenuSteps.clickClearHistoryButton();
        pages.cleanMessageHistoryModalSteps.clickClearForAllButton();
        pages.chatVerify.verifyClearMessageHistorySystemMessage();
        pages.chatVerify.verifyMessageWithTextIsNotDisplayed(TEST_MESSAGE, 10);

        loginHelper.performUILoginForTest(context.getAdmin());
        pages.leftSidebarSteps.clickSpaceByName(context.getWorkspaceName());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatVerify.verifyClearMessageHistorySystemMessage();
        pages.chatVerify.verifyMessageWithTextIsNotDisplayed(TEST_MESSAGE, 10);

        loginHelper.performUILoginForTest(context.getParticipant());
        pages.leftSidebarSteps.clickSpaceByName(context.getWorkspaceName());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatVerify.verifyClearMessageHistorySystemMessage();
        pages.chatVerify.verifyMessageWithTextIsNotDisplayed(TEST_MESSAGE, 10);
    }

    @Test
    @ChannelAllureIds(web = "64263", desktop = "95718")
    @DisplayName("Очистка истории сообщений у всех в p2p чате")
    public void clearP2pHistoryForAll() {
        GroupContext context = groupFactory.p2pChat();
        context.getOwnerBuilder().sendTextMessage(context.getGroupId(), TEST_MESSAGE);

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getParticipant());
        pages.topbarSteps.clickP2pHeader();
        pages.p2pChatMenuSteps.clearChatHistoryButton();
        pages.cleanMessageHistoryModalSteps.clickClearForAllButton();
        pages.chatVerify.verifyClearMessageHistorySystemMessage();
        pages.chatVerify.verifyMessageWithTextIsNotDisplayed(TEST_MESSAGE, 10);

        loginHelper.performUILoginForTest(context.getParticipant());
        pages.chatListSteps.clickChatByName(context.getOwner());
        pages.chatVerify.verifyClearMessageHistorySystemMessage();
        pages.chatVerify.verifyMessageWithTextIsNotDisplayed(TEST_MESSAGE, 10);
    }

    @Test
    @ChannelAllureIds(web = "64262", desktop = "95719")
    @DisplayName("Очистка истории сообщений у себя в p2p чате")
    public void clearP2pHistoryForSelf() {
        GroupContext context = groupFactory.p2pChat();
        context.getOwnerBuilder().sendTextMessage(context.getGroupId(), TEST_MESSAGE);

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getParticipant());
        pages.topbarSteps.clickP2pHeader();
        pages.p2pChatMenuSteps.clearChatHistoryButton();
        pages.cleanMessageHistoryModalSteps.clickClearForSelfButton();
        pages.snackbarsSteps.verifyClearHistoryMessageIsVisible();
        pages.chatVerify.verifyMessageWithTextIsNotDisplayed(TEST_MESSAGE, 10);

        loginHelper.performUILoginForTest(context.getParticipant());
        pages.chatListSteps.clickChatByName(context.getOwner());
        pages.chatVerify.verifyMessageSent(TEST_MESSAGE);
    }
}