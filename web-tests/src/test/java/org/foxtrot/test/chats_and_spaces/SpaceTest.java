package org.foxtrot.test.chats_and_spaces;

import io.qameta.allure.AllureId;
import io.qameta.allure.junit5.AllureJunit5;
import org.aeonbits.owner.ConfigFactory;
import org.foxtrot.config.EmailConfig;
import org.foxtrot.enums.UsersEnum;
import org.foxtrot.test.BaseTest;
import org.foxtrot.utils.LoginHelper;
import org.foxtrot.utils.email.ImapEmailClient;
import org.foxtrot.utils.factories.contexts.GroupContext;
import org.foxtrot.utils.factories.contexts.WorkspaceContext;
import org.foxtrot.utils.factories.core.GroupFactory;
import org.foxtrot.utils.factories.core.UserFactory;
import org.foxtrot.utils.factories.core.WorkspaceFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(AllureJunit5.class)
public class SpaceTest extends BaseTest {

    private static final String TEST_MESSAGE = "space notification test message";

    private final LoginHelper loginHelper = new LoginHelper();
    private final GroupFactory groupFactory = new GroupFactory();
    private final WorkspaceFactory workspaceFactory = new WorkspaceFactory();
    private final UserFactory userFactory = new UserFactory();
    private final EmailConfig emailConfig = ConfigFactory.create(EmailConfig.class);
    private final ImapEmailClient imapEmailClient = new ImapEmailClient();

    @AfterEach
    public void tearDown() {
        userFactory.releaseAll();
    }

    @Test
    @AllureId("52336")
    @DisplayName("Включить уведомления в пространстве > сообщения в пространстве поступают")
    public void enableWorkspaceNotificationsMessagesAreReceived() {
        GroupContext context = groupFactory.privateGroupInWsWithParticipant();
        String workspaceId = String.valueOf(context.getWorkspaceId());

        context.getParticipantBuilder().updateWorkspaceNotification(context.getWorkspaceId(), false);

        loginHelper.performUILogin(context.getParticipant());
        pages.leftSidebarSteps.clickSpaceByName(context.getWorkspaceName());
        pages.spaceSteps.openSpaceMenu();
        pages.spaceMenuVerifier.verifyNotificationsOFFToggle();

        context.getOwnerBuilder().sendTextMessage(context.getGroupId(), TEST_MESSAGE, workspaceId);
        pages.leftSidebarSteps.verifyUnreadBadgeIsMutedOnSpace(context.getWorkspaceName());

        pages.spaceMenuSteps.notificationsOnToggle();
        pages.spaceMenuVerifier.verifyNotificationsONToggle();
        pages.spaceMenuSteps.closeMenuButton();

        context.getOwnerBuilder().sendTextMessage(context.getGroupId(), TEST_MESSAGE, workspaceId);
        pages.leftSidebarSteps.verifyUnreadBadgeOnSpace(context.getWorkspaceName());
    }

    @Test
    @AllureId("52335")
    @DisplayName("Отключить уведомления в пространстве > сообщения из пространства не поступают")
    public void disableWorkspaceNotificationsMessagesAreNotReceived() {
        GroupContext context = groupFactory.privateGroupInWsWithParticipant();
        String workspaceId = String.valueOf(context.getWorkspaceId());

        context.getParticipantBuilder().updateWorkspaceNotification(context.getWorkspaceId(), false);

        loginHelper.performUILogin(context.getParticipant());
        pages.leftSidebarSteps.clickSpaceByName(context.getWorkspaceName());
        pages.spaceSteps.openSpaceMenu();
        pages.spaceMenuVerifier.verifyNotificationsOFFToggle();
        pages.spaceMenuSteps.closeMenuButton();

        context.getOwnerBuilder().sendTextMessage(context.getGroupId(), TEST_MESSAGE, workspaceId);
        pages.leftSidebarSteps.verifyUnreadBadgeIsMutedOnSpace(context.getWorkspaceName());
    }

    @Test
    @AllureId("52331")
    @DisplayName("Пригласить пользователя в пространство через ссылку-приглашение (админ) > пользователь успешно присоединился к пространству")
    public void inviteUserToWorkspaceViaLinkByAdmin() {
        var wsContext = workspaceFactory.createWorkspaceWithParticipant();
        var admin = wsContext.getParticipant();
        UsersEnum invitee = userFactory.takeUser();
        var ownerBuilder = wsContext.getOwnerBuilder();
        var adminBuilder = wsContext.getParticipantBuilder();

        ownerBuilder.updateWorkspaceRole(wsContext.getWsId(), admin, "ADMIN");
        long p2pGroupId = adminBuilder.createP2PGroup(invitee);
        String inviteLink = ownerBuilder.getWorkSpaceInviteLink(wsContext.getWsId());
        adminBuilder.sendLinkMassage(inviteLink, p2pGroupId);

        loginHelper.performUILogin(invitee);
        pages.chatListSteps.clickChatByName(admin.getName());
        pages.chatActions.findAndClickLinkMessageByText(inviteLink);
        pages.spaceSteps.verifySpaceName(wsContext.getWsName());
        pages.leftSidebarSteps.verifySpaceIsActive(wsContext.getWsName());
    }

    @Test
    @AllureId("52323")
    @DisplayName("Пригласить пользователя в пространство через ссылку-приглашение (владелец) > пользователь успешно присоединился к пространству")
    public void inviteUserToWorkspaceViaLinkByOwner() {
        var wsContext = workspaceFactory.createWorkspace();
        var invitee = userFactory.takeUser();
        var ownerBuilder = wsContext.getOwnerBuilder();

        long p2pGroupId = ownerBuilder.createP2PGroup(invitee);
        String inviteLink = ownerBuilder.getWorkSpaceInviteLink(wsContext.getWsId());
        ownerBuilder.sendLinkMassage(inviteLink, p2pGroupId);

        loginHelper.performUILogin(invitee);
        pages.leftSidebarSteps.verifySpaceNotAvailableByName(wsContext.getWsName());
        pages.chatListSteps.clickChatByName(wsContext.getOwner().getName());
        pages.chatActions.findAndClickLinkMessageByText(inviteLink);
        pages.spaceSteps.verifySpaceName(wsContext.getWsName());
        pages.leftSidebarSteps.verifySpaceIsActive(wsContext.getWsName());
    }

    @Test
    @AllureId("52322")
    @DisplayName("При создании пространства нажать кнопку \"Создать\" не заполнив обязательные поля > появляется алерт: Обязательное поле не может быть пустым")
    public void createWorkspaceWithoutRequiredFieldsShowsValidationAlert() {
        UsersEnum owner = userFactory.takeUser();

        loginHelper.performUILogin(owner);
        pages.leftSidebarSteps.enterOrCreateSpaceButton();
        pages.availableWorkspacesModalSteps.clickCreateNewWorkspaceButton();
        pages.newWorkspaceModalSteps.clickCreateButton()
                .verifyRequiredFieldErrorIsVisible();
    }

    @Test
    @AllureId("52328")
    @DisplayName("Совершить вход в неактивное пространство > вход в пространство успешно осуществлен")
    public void enterInactiveWorkspace() {
        var context = workspaceFactory.createWorkspace();
        context.getOwnerBuilder().deactivateAllSpaces();
        loginHelper.performUILogin(context.getOwner());
        pages.leftSidebarSteps.verifySpaceNotAvailableByName(context.getWsName());
        pages.leftSidebarSteps.enterOrCreateSpaceButton();
        pages.availableWorkspacesModalSteps.enterWorkspaceByName(context.getWsName());
        pages.availableWorkspacesModalSteps.closeAvailableWorkspacesModal();
        pages.leftSidebarSteps.isSpaceAvailableByName(context.getWsName());
        pages.leftSidebarSteps.clickSpaceByName(context.getWsName());
        pages.spaceSteps.verifySpaceName(context.getWsName());
    }

    @Test
    @AllureId("52327")
    @DisplayName("Совершить выход из активного пространства > выход из пространства успешно осуществлен")
    public void exitActiveWorkspace() {
        var context = workspaceFactory.createWorkspace();

        loginHelper.performUILogin(context.getOwner());
        pages.leftSidebarSteps.isSpaceAvailableByName(context.getWsName());
        pages.leftSidebarSteps.clickSpaceByName(context.getWsName());
        pages.leftSidebarSteps.verifySpaceIsActive(context.getWsName());

        pages.leftSidebarSteps.enterOrCreateSpaceButton();
        pages.availableWorkspacesModalSteps.exitingWorkspacesByNames(context.getWsName());
        pages.leftSidebarSteps.verifySpaceNotAvailableByName(context.getWsName());
        pages.leftSidebarSteps.verifySpaceIsActive("Чаты");
    }

    @Test
    @AllureId("52332")
    @Tag("email")
    @DisplayName("Пригласить пользователя в пространство через email (админ) > перейдя по ссылке пользователь успешно присоединился к пространству")
    public void inviteUserToWorkspaceViaEmailByAdmin() {
        var wsContext = workspaceFactory.createWorkspaceWithParticipant();
        var admin = wsContext.getParticipant();
        UsersEnum invitee = userFactory.takeUser();

        wsContext.getOwnerBuilder().updateWorkspaceRole(wsContext.getWsId(), admin, "ADMIN");
        inviteUserToWorkspaceViaEmail(admin, wsContext, invitee);
    }

    @Test
    @AllureId("52324")
    @Tag("email")
    @DisplayName("Пригласить пользователя в пространство через email (владелец) > пользователь успешно присоединился к пространству")
    public void inviteUserToWorkspaceViaEmailByOwner() {
        var wsContext = workspaceFactory.createWorkspace();
        UsersEnum invitee = userFactory.takeUser();

        inviteUserToWorkspaceViaEmail(wsContext.getOwner(), wsContext, invitee);
    }

    private void inviteUserToWorkspaceViaEmail(UsersEnum inviter, WorkspaceContext wsContext, UsersEnum invitee) {
        String inviteEmail = ImapEmailClient.uniqueAddress(emailConfig.emailAddress());

        loginHelper.performUILogin(inviter);
        pages.leftSidebarSteps.clickSpaceByName(wsContext.getWsName());
        pages.spaceSteps.openSpaceMenu();
        pages.spaceMenuSteps.clickInviteUsertoWorkspaceButton();
        pages.inviteMembersToWorkspaceModalSteps
                .switchTabToEmail()
                .enterEmail(inviteEmail)
                .submitEmailInvite();

        String inviteLink = imapEmailClient.waitForWorkspaceInviteLink(inviteEmail);

        loginHelper.performUILoginForTest(invitee);
        pages.leftSidebarSteps.verifySpaceNotAvailableByName(wsContext.getWsName());
        pages.baseSteps.openCustomPage(inviteLink);
        pages.spaceSteps.verifySpaceName(wsContext.getWsName());
        pages.leftSidebarSteps.verifySpaceIsActive(wsContext.getWsName());
    }
}