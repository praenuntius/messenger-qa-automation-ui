package org.foxtrot.test.chats_and_spaces;

import io.qameta.allure.AllureId;
import io.qameta.allure.junit5.AllureJunit5;
import org.foxtrot.allure.ChannelAllureIds;
import org.foxtrot.enums.UsersEnum;
import org.foxtrot.test.BaseTest;
import org.foxtrot.utils.LoginHelper;
import org.foxtrot.utils.TestData;
import org.foxtrot.utils.UrlConstants;
import org.foxtrot.utils.factories.contexts.GroupContext;
import org.foxtrot.utils.factories.contexts.WorkspaceContext;
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
public class OwnerTest extends BaseTest {

    private final LoginHelper loginHelper = new LoginHelper();
    private final GroupFactory groupFactory = new GroupFactory();
    private final WorkspaceFactory workspaceFactory = new WorkspaceFactory();
    private final UserFactory userFactory = new UserFactory();

    @AfterEach
    public void tearDown() {
        groupFactory.cleanUp();
        workspaceFactory.cleanUp();
    }

    @AllureId("52277")
    @Test
    @DisplayName("Добавить пользователя 2 (Админ) в Контакты")
    public void addAdminToContacts() {
        var context = groupFactory.p2pChat();

        loginHelper.performUILogin(context.getOwner());
        open(UrlConstants.getP2pChatUrl(context.getParticipant().getId().toString()));
        pages.inputBarSteps.clickMessageInputField();
        pages.inputBarSteps.enterMessage("тест");
        pages.chatVerify.saveMessageInputText();
        pages.inputBarSteps.clickSendMessageButton();
        pages.p2pChatMenuSteps.openP2pChatMenu();
        pages.p2pChatMenuSteps.clickAddToContactsButton();
        pages.p2pChatMenuSteps.checkContactAdded();
    }

    @AllureId("52283") //also: 52284, 52279, 52278
    @Test
    @DisplayName("Создать группу в Хоумспейсе с участником-контактом")
    public void createGroupInHomespaceAndAddAdmin() {
        var context = groupFactory.p2pChat();
        context.getOwnerBuilder().addContacts(context.getParticipant());
        context.getParticipantBuilder().sendTextMessage(
                context.getGroupId(), "autotest precondition " + System.currentTimeMillis());
        String groupName = TestData.generateHomespaceGroupName();

        loginHelper.performUILogin(context.getOwner());
        pages.homespaceSteps.clickCreateNewChatButton();
        pages.newMessageModalSteps.clickCreateGroupButton();
        pages.newGroupModalSteps.uploadPhoto("images/tinyJPG.jpg");
        pages.newGroupModalSteps.createGroupWithParticipants(
                "Homespace", "private", groupName, context.getParticipant().getName());
        pages.topbarSteps.verifyChatName(groupName);
    }

    @AllureId("48674") //also: кнопка удаления чата - 52508
    @Test
    @DisplayName("Проверить меню группы для роли владельца")
    public void verifyGroupMenu() {
        var context = groupFactory.privateGroupWithParticipant();

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.topbarSteps.clickGroupHeader();
        pages.groupMenuVerifier.verifyOwnerMenu(context.getGroupName(), "Закрытая группа");
    }

    @AllureId("52494")
    @Test
    @DisplayName("Назначить Участника админом группы в Хоумспейсе")
    public void appointParticipantAsAdminInGroup() {
        var context = groupFactory.privateGroupWithParticipant();

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.topbarSteps.clickGroupHeader();
        pages.groupMenuSteps.clickMembersButton();
        pages.membersMenuSteps.makeUserAdministrator(context.getParticipant().getName());
        pages.groupMenuSteps.clickMembersButton();
        pages.membersMenuSteps.verifyUserIsAdministrator(context.getParticipant().getName());
    }

    @AllureId("52290") //also: 52288, public: 52287, 52289
    @Test
    @DisplayName("Создать канал в хоумспейсе с участником-контактом ")
    public void createChannelInHomespaceAddAdmin() {
        var context = groupFactory.p2pChat();
        context.getOwnerBuilder().addContacts(context.getParticipant());

        context.getParticipantBuilder().sendTextMessage(
                context.getGroupId(), "autotest precondition " + System.currentTimeMillis());
        String channelName = TestData.generateHomespaceChannelName();

        loginHelper.performUILogin(context.getOwner());
        pages.homespaceSteps.clickCreateNewChatButton();
        pages.newMessageModalSteps.clickCreateChannelButton();
        pages.newGroupModalSteps.uploadPhoto("images/tinyJPG.jpg");
        pages.newChannelModalSteps.createChannelWithParticipants(
                "Homespace", "private", channelName, context.getParticipant().getName());
        pages.topbarSteps.verifyChatName(channelName);
    }

    @AllureId("48678")
    @Test
    @DisplayName("Проверить меню канала для роли владельца")
    public void verifyChannelMenu() {
        var context = groupFactory.privateChannelWithParticipant();

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.topbarSteps.clickChannelHeader();
        pages.channelMenuVerifier.verifyOwnerMenu(context.getGroupName(), "Закрытый канал");
    }

    @AllureId("52552")
    @Test
    @DisplayName("Назначить Участника админом канала в Хоумспейсе")
    public void appointParticipantAsAdminInChannel() {
        var context = groupFactory.privateChannelWithParticipant();

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.topbarSteps.clickChannelHeader();
        pages.channelMenuSteps.clickSubscribersButton();
        pages.subscribersMenuSteps.makeUserAdministrator(context.getParticipant().getName());
        pages.channelMenuSteps.clickSubscribersButton();
        pages.subscribersMenuSteps.verifyUserIsAdministrator(context.getParticipant().getName());
    }

    @AllureId("52321")
    @Test
    @DisplayName("Создать рабочее пространство")
    public void createWorkspace() {
        var owner = userFactory.takeUser();
        String workspaceName = TestData.generateWorkspaceName();

        loginHelper.performUILogin(owner);
        pages.leftSidebarSteps.enterOrCreateSpaceButton();
        pages.availableWorkspacesModalSteps.clickCreateNewWorkspaceButton();
        pages.newWorkspaceModalSteps.UploadAvatar("images/tinyJPG.jpg");
        pages.newWorkspaceModalSteps.createWorkspace(workspaceName);
        pages.spaceSteps.verifySpaceName(workspaceName);
    }

    @AllureId("52675") //- вставка текста в чат сохраненные
    @Test
    @DisplayName("Отправить ссылку на пространство в чат 'Сохраненные'")
    public void sendWorkspaceLinkToChatSave() {
        var wsContext = workspaceFactory.createWorkspace();

        loginHelper.performUILogin(wsContext.getOwner());
        pages.leftSidebarSteps.clickSpaceByName(wsContext.getWsName());
        pages.spaceSteps.openSpaceMenu();
        pages.spaceMenuSteps.clickInviteUsertoWorkspaceButton();
        pages.inviteModalSteps.copyWorkspaceLink();
        pages.leftSidebarSteps.clickChatsButton();
        pages.chatListSteps.clickSavedChat();
        pages.inputBarSteps.clickMessageInputField();
        pages.inputBarSteps.pasteTextIntoInput();
        pages.chatVerify.saveMessageInputText();
        pages.inputBarSteps.clickSendMessageButton();
        pages.chatVerify.verifySaveMessageSent();
    }

    @AllureId("52325")
    @Test
    @DisplayName("Добавить контакт в участники пространства")
    public void addAdminToWorkspace() {
        var wsContext = workspaceWithAdminAsContactOnly();

        loginHelper.performUILogin(wsContext.getOwner());
        pages.leftSidebarSteps.clickSpaceByName(wsContext.getWsName());
        pages.spaceSteps.openSpaceMenu();
        pages.spaceMenuSteps.clickInviteUsertoWorkspaceButton();
        pages.inviteMembersToWorkspaceModalSteps.selectPerson(wsContext.getParticipant().getName());
        pages.spaceMenuSteps.membersButton();
        pages.workspaceMembersMenuSteps.checkUserPresenceInSpace(wsContext.getParticipant().getName());
    }

    @AllureId("52330")
    @Test
    @DisplayName("Назначить Участника админом рабочего пространства")
    public void appointParticipantAsWorkspaceAdmin() {
        var wsContext = workspaceFactory.createWorkspaceWithParticipant();

        loginHelper.performUILogin(wsContext.getOwner());
        pages.leftSidebarSteps.clickSpaceByName(wsContext.getWsName());
        pages.spaceSteps.openSpaceMenu();
        pages.spaceMenuSteps.membersButton();
        pages.workspaceMembersMenuSteps.appointUserAsSpaceAdmin(wsContext.getParticipant().getName());
        pages.spaceMenuSteps.membersButton();
        pages.workspaceMembersMenuSteps.verifyUserIsAdministrator(wsContext.getParticipant().getName());
    }

    @AllureId("52304") //добавить функцию загрузки аватара
    @Test
    @DisplayName("Создать приватный канал в рабочем пространстве")
    public void createPrivateChannelInWorkspace() {
        var wsContext = workspaceWithAdminContact();
        String channelName = TestData.generateWorkspacePrivateChannelName();

        long p2pId = wsContext.getOwnerBuilder().createP2PGroup(wsContext.getParticipant());
        wsContext.getOwnerBuilder().sendTextMessage(p2pId, "autotest_" + System.currentTimeMillis());

        loginHelper.performUILogin(wsContext.getOwner());
        pages.leftSidebarSteps.clickSpaceByName(wsContext.getWsName());
        pages.spaceSteps.openSpaceMenu();
        pages.spaceMenuSteps.clickCreateChannelButton();
        pages.newGroupModalSteps.uploadPhoto("images/tinyJPG.jpg");
        pages.newChannelModalSteps.createChannelWithParticipants(
                "Workspace", "private", channelName, wsContext.getParticipant().getName());
        pages.topbarSteps.verifyChatName(channelName);
    }

    @AllureId("52564") //кнопка удалить только для админа
    @Test
    @DisplayName("Проверить меню владельца приватного канала в рабочем пространстве")
    public void verifyPrivateChannelInWorkspaceMenu() {
        var context = groupFactory.privateChannelInWsWithParticipant();

        loginHelper.performUILogin(context.getOwner());
        pages.leftSidebarSteps.clickSpaceByName(context.getWorkspaceName());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.channelMenuSteps.openChannelMenu();
        pages.channelMenuVerifier.verifyOwnerMenu(context.getGroupName(), "Закрытый канал");
    }

    @Test
    @DisplayName("Назначить Участника админом приватного канала в рабочем пространстве")
    public void appointParticipantAsAdminInWorkspacePrivateChannel() {
        var context = groupFactory.privateChannelInWsWithParticipant();

        loginHelper.performUILogin(context.getOwner());
        pages.leftSidebarSteps.clickSpaceByName(context.getWorkspaceName());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.channelMenuSteps.openChannelMenu();
        pages.channelMenuSteps.clickSubscribersButton();
        pages.subscribersMenuSteps.makeUserAdministrator(context.getParticipant().getName());
        pages.channelMenuSteps.clickSubscribersButton();
        pages.subscribersMenuSteps.verifyUserIsAdministrator(context.getParticipant().getName());
    }

    @AllureId("52303")
    @Test
    @DisplayName("Создать публичный канал в рабочем пространстве")
    public void createPublicChannelInWorkspace() {
        var wsContext = workspaceWithAdminContact();
        String channelName = TestData.generateWorkspacePublicChannelName();

        long p2pId = wsContext.getOwnerBuilder().createP2PGroup(wsContext.getParticipant());
        wsContext.getOwnerBuilder().sendTextMessage(p2pId, "autotest_" + System.currentTimeMillis());

        loginHelper.performUILogin(wsContext.getOwner());
        pages.leftSidebarSteps.clickSpaceByName(wsContext.getWsName());
        pages.spaceSteps.openSpaceMenu();
        pages.spaceMenuSteps.clickCreateChannelButton();
        pages.newGroupModalSteps.uploadPhoto("images/tinyJPG.jpg");
        pages.newChannelModalSteps.createChannelWithParticipants(
                "Workspace", "public", channelName, wsContext.getParticipant().getName());
        pages.topbarSteps.verifyChatName(channelName);
    }

    @Test
    @DisplayName("Проверить меню владельца публичного канала в рабочем пространстве")
    public void verifyPublicChannelInWorkspaceMenu() {
        var context = groupFactory.publicChannelInWsWithParticipant();

        loginHelper.performUILogin(context.getOwner());
        pages.leftSidebarSteps.clickSpaceByName(context.getWorkspaceName());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.channelMenuSteps.openChannelMenu();
        pages.channelMenuVerifier.verifyOwnerMenu(context.getGroupName(), "Открытый канал");
    }

    @Test
    @DisplayName("Назначить Участника админом публичного канала в рабочем пространстве")
    public void appointParticipantAsAdminInWorkspacePublicChannel() {
        var context = groupFactory.publicChannelInWsWithParticipant();

        loginHelper.performUILogin(context.getOwner());
        pages.leftSidebarSteps.clickSpaceByName(context.getWorkspaceName());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.channelMenuSteps.openChannelMenu();
        pages.channelMenuSteps.clickSubscribersButton();
        pages.subscribersMenuSteps.makeUserAdministrator(context.getParticipant().getName());
        pages.channelMenuSteps.clickSubscribersButton();
        pages.subscribersMenuSteps.verifyUserIsAdministrator(context.getParticipant().getName());
    }

    @AllureId("52295")
    @Test
    @DisplayName("Создать приватную группу в рабочем пространстве")
    public void createPrivateGroupInWorkspace() {
        var wsContext = workspaceWithAdminContact();
        String groupName = TestData.generateWorkspacePrivateGroupName();

        long p2pId = wsContext.getOwnerBuilder().createP2PGroup(wsContext.getParticipant());
        wsContext.getOwnerBuilder().sendTextMessage(p2pId, "autotest_" + System.currentTimeMillis());

        loginHelper.performUILogin(wsContext.getOwner());
        pages.leftSidebarSteps.clickSpaceByName(wsContext.getWsName());
        pages.spaceSteps.openSpaceMenu();
        pages.spaceMenuSteps.clickCreateGroupButton();
        pages.newGroupModalSteps.uploadPhoto("images/tinyJPG.jpg");
        pages.newGroupModalSteps.createGroupWithParticipants( //отправить сообщение предусловием
                "Workspace", "private", groupName, wsContext.getParticipant().getName());
        pages.topbarSteps.verifyChatName(groupName);
    }

    @Test
    @DisplayName("Проверить меню владельца приватной группы в рабочем пространстве")
    public void verifyPrivateGroupInWorkspaceMenu() {
        var context = groupFactory.privateGroupInWsWithParticipant();

        loginHelper.performUILogin(context.getOwner());
        pages.leftSidebarSteps.clickSpaceByName(context.getWorkspaceName());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.topbarSteps.clickGroupHeader();
        pages.groupMenuVerifier.verifyOwnerMenu(context.getGroupName(), "Закрытая группа");
    }

    @Test
    @DisplayName("Назначить Участника админом приватной группы в рабочем пространстве")
    public void appointParticipantAsAdminInWorkspacePrivateGroup() {
        var context = groupFactory.privateGroupInWsWithParticipant();

        loginHelper.performUILogin(context.getOwner());
        pages.leftSidebarSteps.clickSpaceByName(context.getWorkspaceName());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.topbarSteps.clickGroupHeader();
        pages.groupMenuSteps.clickMembersButton();
        pages.membersMenuSteps.makeUserAdministrator(context.getParticipant().getName());
        pages.groupMenuSteps.clickMembersButton();
        pages.membersMenuSteps.verifyUserIsAdministrator(context.getParticipant().getName());
    }

    @AllureId("52299")
    @Test
    @DisplayName("Создать публичную группу в рабочем пространстве")
    public void createPublicGroupInWorkspace() {
        var wsContext = workspaceWithAdminContact();
        String groupName = TestData.generateWorkspacePublicGroupName();

        long p2pId = wsContext.getOwnerBuilder().createP2PGroup(wsContext.getParticipant());
        wsContext.getOwnerBuilder().sendTextMessage(p2pId, "autotest_" + System.currentTimeMillis());

        loginHelper.performUILogin(wsContext.getOwner());
        pages.leftSidebarSteps.clickSpaceByName(wsContext.getWsName());
        pages.spaceSteps.openSpaceMenu();
        pages.spaceMenuSteps.clickCreateGroupButton();
        pages.newGroupModalSteps.uploadPhoto("images/tinyJPG.jpg");
        pages.newGroupModalSteps.createGroupWithParticipants( //добавить отправку сообщения обходом бага
                "Workspace", "public", groupName, wsContext.getParticipant().getName());
        pages.topbarSteps.verifyChatName(groupName);
    }

    @Test
    @DisplayName("Проверить меню владельца публичной группы в рабочем пространстве")
    public void verifyPublicGroupInWorkspaceMenu() {
        var context = groupFactory.publicGroupInWsWithParticipant();

        loginHelper.performUILogin(context.getOwner());
        pages.leftSidebarSteps.clickSpaceByName(context.getWorkspaceName());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.topbarSteps.clickGroupHeader();
        pages.groupMenuVerifier.verifyOwnerMenu(context.getGroupName(), "Открытая группа");
    }

    @Test
    @DisplayName("Назначить Участника админом публичной группы в рабочем пространстве")
    public void appointParticipantAsAdminInWorkspacePublicGroup() {
        var context = groupFactory.publicGroupInWsWithParticipant();

        loginHelper.performUILogin(context.getOwner());
        pages.leftSidebarSteps.clickSpaceByName(context.getWorkspaceName());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.topbarSteps.clickGroupHeader();
        pages.groupMenuSteps.clickMembersButton();
        pages.membersMenuSteps.makeUserAdministrator(context.getParticipant().getName());
        pages.groupMenuSteps.clickMembersButton();
        pages.membersMenuSteps.verifyUserIsAdministrator(context.getParticipant().getName());
    }

    @Test
    @DisplayName("Отправить ссылку на группу в хоумспейсе в чат 'Сохраненные'")
    public void sendHomespacePrivateGroupLinkToSavedChat() {
        var context = groupFactory.privateGroupWithParticipant();

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.topbarSteps.clickGroupHeader();
        pages.groupMenuSteps.clickInviteButton();
        pages.groupInviteByLinkModalSteps.copyGroupLink();
        pages.groupInviteByLinkModalSteps.closeModal();
        pages.leftSidebarSteps.clickChatsButton();
        pages.chatListSteps.clickSavedChat();
        pages.inputBarSteps.clickMessageInputField();
        pages.inputBarSteps.pasteTextIntoInput();
        pages.chatVerify.saveMessageInputText();
        pages.inputBarSteps.clickSendMessageButton();
        pages.chatVerify.verifySaveMessageSent();
    }

    @Test
    @DisplayName("Отправить ссылку на канал в хоумспейсе в чат 'Сохраненные'")
    public void sendHomespacePublicChannelLinkToSavedChat() {
        var context = groupFactory.privateChannelWithParticipant();

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.topbarSteps.clickChannelHeader();
        pages.channelMenuSteps.clickInviteCloseChannelLinkButton();
        pages.channelInviteByLinkModalSteps.copyChannelLink();
        pages.channelInviteByLinkModalSteps.closeModal();
        pages.leftSidebarSteps.clickChatsButton();
        pages.chatListSteps.clickSavedChat();
        pages.inputBarSteps.clickMessageInputField();
        pages.inputBarSteps.pasteTextIntoInput();
        pages.chatVerify.saveMessageInputText();
        pages.inputBarSteps.clickSendMessageButton();
        pages.chatVerify.verifySaveMessageSent();
    }

    @Test
    @DisplayName("Отправить ссылку на приватный канал в рабочем пространстве в чат 'Сохраненные'")
    public void sendWorkspacePrivateChannelLinkToSavedChat() {
        var context = groupFactory.privateChannelInWsWithParticipant();

        loginHelper.performUILogin(context.getOwner());
        pages.leftSidebarSteps.clickSpaceByName(context.getWorkspaceName());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.topbarSteps.clickChannelHeader();
        pages.channelMenuSteps.clickInviteCloseChannelLinkButton();
        pages.channelInviteByLinkModalSteps.copyChannelLink();
        pages.channelInviteByLinkModalSteps.closeModal();
        pages.leftSidebarSteps.clickChatsButton();
        pages.chatListSteps.clickSavedChat();
        pages.inputBarSteps.clickMessageInputField();
        pages.inputBarSteps.pasteTextIntoInput();
        pages.chatVerify.saveMessageInputText();
        pages.inputBarSteps.clickSendMessageButton();
        pages.chatVerify.verifySaveMessageSent();
    }

    @Test
    @DisplayName("Отправить ссылку на публичный канал в рабочем пространстве в чат 'Сохраненные'")
    public void sendWorkspacePublicChannelLinkToSavedChat() {
        var context = groupFactory.publicChannelInWsWithParticipant();

        loginHelper.performUILogin(context.getOwner());
        pages.leftSidebarSteps.clickSpaceByName(context.getWorkspaceName());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.topbarSteps.clickChannelHeader();
        pages.channelMenuSteps.clickCopyLinkOpenChannelButton();
        pages.channelInviteByLinkModalSteps.verifyCopyToastAppears();
        pages.leftSidebarSteps.clickChatsButton();
        pages.chatListSteps.clickSavedChat();
        pages.inputBarSteps.clickMessageInputField();
        pages.inputBarSteps.pasteTextIntoInput();
        pages.chatVerify.saveMessageInputText();
        pages.inputBarSteps.clickSendMessageButton();
        pages.chatVerify.verifySaveMessageSent();
    }

    @Test
    @DisplayName("Отправить ссылку на приватную группу в рабочем пространстве в чат 'Сохраненные'")
    public void sendWorkspacePrivateGroupLinkToSavedChat() {
        var context = groupFactory.privateGroupInWsWithParticipant();

        loginHelper.performUILogin(context.getOwner());
        pages.leftSidebarSteps.clickSpaceByName(context.getWorkspaceName());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.topbarSteps.clickGroupHeader();
        pages.groupMenuSteps.clickInviteButton();
        pages.groupInviteByLinkModalSteps.copyGroupLink();
        pages.groupInviteByLinkModalSteps.closeModal();
        pages.leftSidebarSteps.clickChatsButton();
        pages.chatListSteps.clickSavedChat();
        pages.inputBarSteps.clickMessageInputField();
        pages.inputBarSteps.pasteTextIntoInput();
        pages.chatVerify.saveMessageInputText();
        pages.inputBarSteps.clickSendMessageButton();
        pages.chatVerify.verifySaveMessageSent();
    }

    @Test
    @DisplayName("Отправить ссылку на публичную группу в рабочем пространстве в чат 'Сохраненные'")
    public void sendWorkspacePublicGroupLinkToSavedChat() {
        var context = groupFactory.publicGroupInWsWithParticipant();

        loginHelper.performUILogin(context.getOwner());
        pages.leftSidebarSteps.clickSpaceByName(context.getWorkspaceName());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.topbarSteps.clickGroupHeader();
        pages.groupMenuSteps.clickInviteButton();
        pages.groupInviteByLinkModalSteps.verifyCopyToastAppears();
        pages.leftSidebarSteps.clickChatsButton();
        pages.chatListSteps.clickSavedChat();
        pages.inputBarSteps.clickMessageInputField();
        pages.inputBarSteps.pasteTextIntoInput();
        pages.chatVerify.saveMessageInputText();
        pages.inputBarSteps.clickSendMessageButton();
        pages.chatVerify.verifySaveMessageSent();
    }

    @AllureId("54706")
    @Test
    @DisplayName("Удалить пользователя 2 (Админ) из контактов")
    public void deleteAdminToContact() {
        var context = ownerWithAdminContact();

        loginHelper.performUILogin(context.getOwner());
        open(UrlConstants.getP2pChatUrl(context.getParticipant().getId().toString()));
        pages.p2pChatMenuSteps.openP2pChatMenu();
        pages.p2pChatMenuSteps.clickDeleteContactButton();
        pages.p2pChatMenuSteps.checkContactDelete();
    }

    @ChannelAllureIds(web = "60198", desktop = "60375")
    @Test
    @DisplayName("Создание канала в хоумспейсе с разрешенными реакциями")
    public void createHomespaceChannelWithReactionsEnabled() {
        var owner = userFactory.takeUser();
        String channelName = TestData.generateHomespaceChannelName();

        loginHelper.performUILogin(owner);
        pages.homespaceSteps.clickCreateNewChatButton();
        pages.newMessageModalSteps.clickCreateChannelButton();
        pages.newChannelModalSteps
                .verifyAllowReactionsToggleEnabled()
                .createChannelWithoutParticipants("Homespace", "private", channelName);
        pages.topbarSteps.verifyChatName(channelName);
        pages.topbarSteps.clickChannelHeader();
        pages.channelMenuSteps.openChannelSettings();
        pages.groupAndChannelEditModalSteps.verifyAllowReactionsToggleEnabled();
    }

    @ChannelAllureIds(web = "60199", desktop = "60376")
    @Test
    @DisplayName("Создание канала в хоумспейсе с запрещёнными реакциями")
    public void createHomespaceChannelWithReactionsDisabled() {
        var owner = userFactory.takeUser();
        String channelName = TestData.generateHomespaceChannelName();

        loginHelper.performUILogin(owner);
        pages.homespaceSteps.clickCreateNewChatButton();
        pages.newMessageModalSteps.clickCreateChannelButton();
        pages.newChannelModalSteps
                .clickAllowReactionsToggle()
                .createChannelWithoutParticipants("Homespace", "private", channelName);
        pages.topbarSteps.verifyChatName(channelName);
        pages.topbarSteps.clickChannelHeader();
        pages.channelMenuSteps.openChannelSettings();
        pages.groupAndChannelEditModalSteps.verifyAllowReactionsToggleDisabled();
    }

    @ChannelAllureIds(web = "60284", desktop = "60364")
    @Test
    @DisplayName("Создание канала в воркспейсе с разрешенными реакциями")
    public void createWorkspaceChannelWithReactionsEnabled() {
        var wsContext = workspaceFactory.createWorkspace();
        String channelName = TestData.generateWorkspacePrivateChannelName();

        loginHelper.performUILogin(wsContext.getOwner());
        pages.leftSidebarSteps.clickSpaceByName(wsContext.getWsName());
        pages.spaceSteps.openSpaceMenu();
        pages.spaceMenuSteps.clickCreateChannelButton();
        pages.newChannelModalSteps
                .verifyAllowReactionsToggleEnabled()
                .createChannelWithoutParticipants("Workspace", "private", channelName);
        pages.topbarSteps.verifyChatName(channelName);
        pages.topbarSteps.clickChannelHeader();
        pages.channelMenuSteps.openChannelSettings();
        pages.groupAndChannelEditModalSteps.verifyAllowReactionsToggleEnabled();
    }

    @ChannelAllureIds(web = "60285", desktop = "60359")
    @Test
    @DisplayName("Создание канала в воркспейсе с запрещёнными реакциями")
    public void createWorkspaceChannelWithReactionsDisabled() {
        var wsContext = workspaceFactory.createWorkspace();
        String channelName = TestData.generateWorkspacePrivateChannelName();

        loginHelper.performUILogin(wsContext.getOwner());
        pages.leftSidebarSteps.clickSpaceByName(wsContext.getWsName());
        pages.spaceSteps.openSpaceMenu();
        pages.spaceMenuSteps.clickCreateChannelButton();
        pages.newChannelModalSteps
                .clickAllowReactionsToggle()
                .createChannelWithoutParticipants("Workspace", "private", channelName);
        pages.topbarSteps.verifyChatName(channelName);
        pages.topbarSteps.clickChannelHeader();
        pages.channelMenuSteps.openChannelSettings();
        pages.groupAndChannelEditModalSteps.verifyAllowReactionsToggleDisabled();
    }

    private GroupContext ownerWithAdminContact() {
        var context = groupFactory.p2pChat();
        context.getOwnerBuilder().addContacts(context.getParticipant());
        return context;
    }

    private WorkspaceContext workspaceWithAdminContact() {
        var wsContext = workspaceWithAdminAsContactOnly();
        wsContext.getOwnerBuilder().addUsersToWorkspace(wsContext.getWsId(), wsContext.getParticipant());
        return wsContext;
    }

    /** Контакт владельца без участника пространства — для сценариев добавления через UI. */
    private WorkspaceContext workspaceWithAdminAsContactOnly() {
        var wsContext = workspaceFactory.createWorkspace();
        UsersEnum admin = userFactory.takeUser();
        wsContext.getOwnerBuilder().addContacts(admin);
        return wsContext.toBuilder().participant(admin).build();
    }
}
