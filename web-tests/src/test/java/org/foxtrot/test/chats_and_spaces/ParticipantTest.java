package org.foxtrot.test.chats_and_spaces;

import io.qameta.allure.AllureId;
import io.qameta.allure.junit5.AllureJunit5;
import org.foxtrot.enums.UsersEnum;
import org.foxtrot.test.BaseTest;
import org.foxtrot.utils.LoginHelper;
import org.foxtrot.utils.UrlConstants;
import org.foxtrot.utils.factories.contexts.GroupContext;
import org.foxtrot.utils.factories.core.GroupFactory;
import org.foxtrot.utils.factories.core.UserFactory;
import org.foxtrot.utils.factories.core.WorkspaceFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;

import static com.codeborne.selenide.Selenide.open;

@ExtendWith(AllureJunit5.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ParticipantTest extends BaseTest {

    private final LoginHelper loginHelper = new LoginHelper();
    private final GroupFactory groupFactory = new GroupFactory();
    private final WorkspaceFactory workspaceFactory = new WorkspaceFactory();
    private final UserFactory userFactory = new UserFactory();

    @AfterEach
    public void tearDown() {
        groupFactory.cleanUp();
        workspaceFactory.cleanUp();
    }

    @AllureId("48676")
    @Test
    @DisplayName("UI логин, Группа в домашнем пространстве, проверка меню группы для роли Участник")
    public void loginAndWaitForHomespaceGroup() {
        var context = groupFactory.privateGroupWithParticipant();

        loginHelper.performUILogin(context.getParticipant());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.topbarSteps.clickGroupHeader();
        pages.groupMenuVerifier.verifyParticipantMenu(context.getGroupName(), "Закрытая группа");
    }

    @Test
    @DisplayName("Отправить текстовое сообщение в группу домашнего пространства")
    public void sendMessageInHomespaceGroup() {
        var context = groupFactory.privateGroupWithParticipant();

        loginHelper.performUILogin(context.getParticipant());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.inputBarSteps.clickMessageInputField();
        pages.inputBarSteps.enterMessage("Текстовое сообщение от участника в группу домашнего пространства");
        pages.chatVerify.saveMessageInputText();
        pages.inputBarSteps.clickSendMessageButton();
        pages.chatVerify.verifySaveMessageSent();
    }

    @AllureId("48679")
    @Test
    @DisplayName("Канал в домашнем пространстве, проверка меню канала для роли Участник")
    public void waitForChannelInHomespace() {
        var context = groupFactory.privateChannelWithParticipant();

        loginHelper.performUILogin(context.getParticipant());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.topbarSteps.clickChannelHeader();
        pages.channelMenuVerifier.verifyParticipantMenu(context.getGroupName(), "Закрытый канал");
    }

    @Test
    @DisplayName("Проверка отсутствия интерфейса ввода сообщения в канале для роли Участник")
    public void sendMessageInHomespaceChannel() {
        var context = groupFactory.privateChannelWithParticipant();

        loginHelper.performUILogin(context.getParticipant());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatVerify.verifyMessageInputAndSendButtonAbsence();
    }

    @Test
    @DisplayName("Проверка меню пространства для роли Участника")
    public void waitForWorkspaceAndCheckParticipants() {
        var context = workspaceFactory.createWorkspaceWithParticipant();

        loginHelper.performUILogin(context.getParticipant());
        pages.leftSidebarSteps.clickSpaceByName(context.getWsName());
        pages.spaceSteps.openSpaceMenu();
        pages.spaceMenuVerifier.verifyParticipantMenu(context.getWsName());
    }

    @Test
    @DisplayName("Приватный канал в рабочем пространстве, отсутствие интерфейса отправки сообщения, проверка меню канала для роли Участник")
    public void waitForPrivateChannelInWorkspace() {
        var context = groupFactory.privateChannelInWsWithParticipant();

        loginHelper.performUILogin(context.getParticipant());
        pages.leftSidebarSteps.clickSpaceByName(context.getWorkspaceName());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatVerify.verifyMessageInputAndSendButtonAbsence();
        pages.topbarSteps.clickChannelHeader();
        pages.channelMenuVerifier.verifyParticipantMenu(context.getGroupName(), "Закрытый канал");
    }

    @Test
    @DisplayName("Публичный канал в рабочем пространстве, отсутствие интерфейса отправки сообщения, проверка меню канала для роли Участник")
    public void waitForPublicChannelInWorkspace() {
        var context = groupFactory.publicChannelInWsWithParticipant();

        loginHelper.performUILogin(context.getParticipant());
        pages.leftSidebarSteps.clickSpaceByName(context.getWorkspaceName());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatVerify.verifyMessageInputAndSendButtonAbsence();
        pages.topbarSteps.clickChannelHeader();
        pages.channelMenuVerifier.verifyParticipantMenu(context.getGroupName(), "Открытый канал");
    }

    @Test
    @DisplayName("Приватная группа в рабочем пространстве, отправить текстовое сообщение, проверка меню группы для роли Участник")
    public void waitForPrivateGroupInWorkspace() {
        var context = groupFactory.privateGroupInWsWithParticipant();

        loginHelper.performUILogin(context.getParticipant());
        pages.leftSidebarSteps.clickSpaceByName(context.getWorkspaceName());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.inputBarSteps.clickMessageInputField();
        pages.inputBarSteps.enterMessage("Текстовое сообщение от участника в группу рабочего пространства");
        pages.chatVerify.saveMessageInputText();
        pages.inputBarSteps.clickSendMessageButton();
        pages.chatVerify.verifySaveMessageSent();
        pages.topbarSteps.clickGroupHeader();
        pages.groupMenuVerifier.verifyParticipantMenu(context.getGroupName(), "Закрытая группа");
    }

    @Test
    @DisplayName("Публичная группа в рабочем пространстве, отправить текстовое сообщение, проверка меню группы для роли Участник")
    public void waitForPublicGroupInWorkspace() {
        var context = groupFactory.publicGroupInWsWithParticipant();

        loginHelper.performUILogin(context.getParticipant());
        pages.leftSidebarSteps.clickSpaceByName(context.getWorkspaceName());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.inputBarSteps.clickMessageInputField();
        pages.inputBarSteps.enterMessage("Текстовое сообщение от участника в группу рабочего пространства");
        pages.chatVerify.saveMessageInputText();
        pages.inputBarSteps.clickSendMessageButton();
        pages.chatVerify.verifySaveMessageSent();
        pages.topbarSteps.clickGroupHeader();
        pages.groupMenuVerifier.verifyParticipantMenu(context.getGroupName(), "Открытая группа");
    }

    @Test
    @DisplayName("Создать чат с не контактом по URL")
    public void sendMessageToNoncontact() {
        var context = participantOpensNonContactChat();

        loginHelper.performUILogin(context.getParticipant());
        open(UrlConstants.getP2pChatUrl(context.getOwner().getId().toString()));
        pages.inputBarSteps.clickMessageInputField();
        pages.inputBarSteps.enterMessage("Текстовое сообщение не контакту");
        pages.chatVerify.saveMessageInputText();
        pages.inputBarSteps.clickSendMessageButton();
        pages.chatVerify.verifySaveMessageSent();
    }

    /** participant — логин; owner — пользователь по URL, не в контактах. */
    private GroupContext participantOpensNonContactChat() {
        UsersEnum participant = userFactory.takeUser();
        UsersEnum nonContact = userFactory.takeUser();
        return GroupContext.builder()
                .participant(participant)
                .owner(nonContact)
                .build();
    }
}
