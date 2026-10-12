package org.foxtrot.test.chats_and_spaces;

import io.qameta.allure.junit5.AllureJunit5;
import org.foxtrot.allure.ChannelAllureIds;
import org.foxtrot.enums.UsersEnum;
import org.foxtrot.test.BaseTest;
import org.foxtrot.utils.LoginHelper;
import org.foxtrot.utils.UrlConstants;
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
public class AdminTest extends BaseTest {

    private final LoginHelper loginHelper = new LoginHelper();
    private final GroupFactory groupFactory = new GroupFactory();
    private final WorkspaceFactory workspaceFactory = new WorkspaceFactory();
    private final UserFactory userFactory = new UserFactory();

    @AfterEach
    public void tearDown() {
        groupFactory.cleanUp();
        workspaceFactory.cleanUp();
    }

    @Test
    @ChannelAllureIds(web = "78866", desktop = "96151")
    @DisplayName("Перейти по ссылке на профиль {memberName} и отправить сообщение")
    public void sendMessageToUser3() {
        var context = groupFactory.p2pChat();

        loginHelper.performUILogin(context.getOwner());
        open(UrlConstants.getP2pChatUrl(context.getParticipant().getId().toString()));
        pages.inputBarSteps.clickMessageInputField();
        pages.inputBarSteps.enterMessage("тест");
        pages.chatVerify.saveMessageInputText();
        pages.inputBarSteps.clickSendMessageButton();
        pages.chatVerify.verifySaveMessageSent();
    }

    @Test
    @ChannelAllureIds(web = "48675", desktop = "96150")
    @DisplayName("Проверка меню группы для роли Админа, добавление пользователя {memberName}")
    public void checkHomespaceGroupAndAddUser3() {
        var context = groupFactory.privateGroupWithAdmin();
        UsersEnum participant = userFactory.takeUser();
        long p2pId = context.getAdminBuilder().createP2PGroup(participant);
        context.getAdminBuilder().sendTextMessage(p2pId, "autotest precondition " + System.currentTimeMillis());

        loginHelper.performUILogin(context.getAdmin());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.topbarSteps.clickGroupHeader();
        pages.groupMenuVerifier.verifyAdminMenu(context.getGroupName(), "Закрытая группа");
        pages.groupMenuSteps.clickMembersButton();
        pages.membersMenuSteps.clickAddMembersButton();
        pages.addPeopleListSteps.selectPerson(participant.getName());
        pages.topbarSteps.clickGroupHeader();
        pages.groupMenuSteps.clickMembersButton();
        pages.membersMenuSteps.checkUserPresenceInSpace(participant.getName());
    }

    @Test
    @ChannelAllureIds(web = "78843", desktop = "96190")
    @DisplayName("Проверка меню канала для роли Админа")
    public void checkHomespaceChannelAndAddUser3() {
        var context = groupFactory.privateChannelWithAdmin();

        loginHelper.performUILogin(context.getAdmin());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.topbarSteps.clickChannelHeader();
        pages.channelMenuVerifier.verifyAdminMenu(context.getGroupName(), "Закрытый канал");
    }

    @Test
    @ChannelAllureIds(web = "78822", desktop = "96193")
    @DisplayName("Проверка меню пространства для роли Админа, добавление не контакта в пространство")
    public void waitForWorkspaceAndCheckParticipants() {
        var context = workspaceFactory.createWorkspaceWithWorkspaceAdmin();
        UsersEnum participant = userFactory.takeUser();

        loginHelper.performUILogin(context.getAdmin());
        pages.leftSidebarSteps.clickSpaceByName(context.getWsName());
        pages.spaceSteps.openSpaceMenu();
        pages.spaceMenuVerifier.verifyAdminMenu(context.getWsName());
        pages.spaceMenuSteps.clickInviteUsertoWorkspaceButton();
        pages.inviteMembersToWorkspaceModalSteps.verifyUserNotInList(participant.getName());
    }

    @Test
    @ChannelAllureIds(web = "52333", desktop = "96152")
    @DisplayName("Добавление пользователя в пространство, от роли админа")
    public void addUser3ToContactsAndWorkspace() {
        var context = workspaceFactory.createWorkspaceWithWorkspaceAdmin();
        UsersEnum participant = userFactory.takeUser();
        context.getAdminBuilder().addContacts(participant);

        loginHelper.performUILogin(context.getAdmin());
        pages.leftSidebarSteps.clickSpaceByName(context.getWsName());
        pages.spaceSteps.openSpaceMenu();
        pages.spaceMenuSteps.clickInviteUsertoWorkspaceButton();
        pages.inviteMembersToWorkspaceModalSteps.selectPerson(participant.getName());
        pages.spaceMenuSteps.membersButton();
        pages.workspaceMembersMenuSteps.checkUserPresenceInSpace(participant.getName());
    }

    @Test
    @ChannelAllureIds(web = "78831", desktop = "96197")
    @DisplayName("Проверка меню P2p-чата (контакта)")
    public void checkP2pChatMenuForContact() {
        var context = groupFactory.p2pChat();
        context.getOwnerBuilder().addContacts(context.getParticipant());
        context.getParticipantBuilder().sendTextMessage(
                context.getGroupId(), "autotest precondition " + System.currentTimeMillis());

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getParticipant().getName());
        pages.topbarSteps.clickP2pHeader();
        pages.p2pMenuVerifier.verifyP2pSidebarMenuElements(
                "contact", "Enabled", context.getParticipant().getName());
    }

    @Test
    @ChannelAllureIds(web = "78845", desktop = "96201")
    @DisplayName("Открытие пространства,проверка меню Админа канала")
    public void waitForWorkspacePrivateChannelAndAddUser3() {
        var context = groupFactory.withWorkspaceAndChatAdmin(groupFactory.privateChannelInWs());

        loginHelper.performUILogin(context.getAdmin());
        pages.leftSidebarSteps.clickSpaceByName(context.getWorkspaceName());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.topbarSteps.clickChannelHeader();
        pages.channelMenuVerifier.verifyAdminMenu(context.getGroupName(), "Закрытый канал");
    }

    @Test
    @ChannelAllureIds(web = "78856", desktop = "96211")
    @DisplayName("Открытие пространства, проверка меню Админа канала, добавление пользователя в публичный канал")
    public void waitForWorkspacePublicChannelAndAddUser3() {
        var context = groupFactory.withWorkspaceMember(
                groupFactory.withWorkspaceAndChatAdmin(groupFactory.publicChannelInWs()));
        long p2pId = context.getAdminBuilder().createP2PGroup(context.getParticipant());
        context.getAdminBuilder().sendTextMessage(p2pId, "autotest precondition " + System.currentTimeMillis());

        loginHelper.performUILogin(context.getAdmin());
        pages.leftSidebarSteps.clickSpaceByName(context.getWorkspaceName());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.topbarSteps.clickChannelHeader();
        pages.channelMenuVerifier.verifyAdminMenu(context.getGroupName(), "Открытый канал");
        pages.channelMenuSteps.clickSubscribersButton();
        pages.subscribersMenuSteps.clickAddSubscribers();
        pages.addSubscribersListSteps.selectSubscriber(context.getParticipant().getName());
        pages.addSubscribersListSteps.clickAddSubscribersButton();
        pages.topbarSteps.clickChannelHeader();
        pages.channelMenuSteps.clickSubscribersButton();
        pages.subscribersMenuSteps.verifySubscriberExists(context.getParticipant().getName());
    }

    @Test
    @ChannelAllureIds(web = "78824", desktop = "96212")
    @DisplayName("Открытие пространства, проверка меню группы Админа")
    public void waitForWorkspacePrivateGroupAndAddUser3() {
        var context = groupFactory.withWorkspaceAndChatAdmin(groupFactory.privateGroupInWs());

        loginHelper.performUILogin(context.getAdmin());
        pages.leftSidebarSteps.clickSpaceByName(context.getWorkspaceName());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.topbarSteps.clickGroupHeader();
        pages.groupMenuVerifier.verifyAdminMenu(context.getGroupName(), "Закрытая группа");
    }

    @Test
    @ChannelAllureIds(web = "78823", desktop = "96213")
    @DisplayName("Открытие пространства, проверка меню Админа, добавление пользователя Participant в публичную группу")
    public void waitForWorkspacePublicGroupAndAddUser3() {
        var context = groupFactory.withWorkspaceMember(
                groupFactory.withWorkspaceAndChatAdmin(groupFactory.publicGroupInWs()));
        long p2pId = context.getAdminBuilder().createP2PGroup(context.getParticipant());
        context.getAdminBuilder().sendTextMessage(p2pId, "autotest precondition " + System.currentTimeMillis());

        loginHelper.performUILogin(context.getAdmin());
        pages.leftSidebarSteps.clickSpaceByName(context.getWorkspaceName());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.topbarSteps.clickGroupHeader();
        pages.groupMenuVerifier.verifyAdminMenu(context.getGroupName(), "Открытая группа");
        pages.groupMenuSteps.clickMembersButton();
        pages.membersMenuSteps.clickAddMembersButton();
        pages.addPeopleListSteps.selectPerson(context.getParticipant().getName());
        pages.topbarSteps.clickGroupHeader();
        pages.groupMenuSteps.clickMembersButton();
        pages.membersMenuSteps.checkUserPresenceInSpace(context.getParticipant().getName());
    }
}
