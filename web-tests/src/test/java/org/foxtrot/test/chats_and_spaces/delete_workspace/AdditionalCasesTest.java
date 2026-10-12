package org.foxtrot.test.chats_and_spaces.delete_workspace;

import org.foxtrot.allure.ChannelAllureIds;
import io.qameta.allure.junit5.AllureJunit5;
import org.foxtrot.test.BaseTest;
import org.foxtrot.utils.LoginHelper;
import org.foxtrot.utils.factories.contexts.GroupContext;
import org.foxtrot.utils.factories.core.GroupFactory;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(AllureJunit5.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AdditionalCasesTest extends BaseTest {

    private final GroupFactory groupFactory = new GroupFactory();
    private final LoginHelper loginHelper = new LoginHelper();
    private GroupContext context;

    @AfterEach
    public void tearDown() {
        groupFactory.cleanUp();
    }

    @Test
    @ChannelAllureIds(web = "57637", desktop = "57739")
    @DisplayName("Проверить поведение при удалении, если участник находится в чатах Хоумспейса")
    public void deleteWorkspaceWhileInHomespace() {
        context = groupFactory.privateGroupInWsWithParticipant();
        loginHelper.performUILogin(context.getParticipant());

        pages.leftSidebarSteps.isSpaceAvailableByName(context.getWorkspaceName());
        pages.chatListSteps.clickSavedChat();

        context.getOwnerBuilder().deleteWorkspace(context.getWorkspaceId());

        pages.leftSidebarSteps.verifySpaceNotAvailableByName(context.getWorkspaceName());
    }

    @Test
    @ChannelAllureIds(web = "57635", desktop = "57740")
    @DisplayName("Удаление пространства в момент нахождения на экране FAQ")
    public void deleteWorkspaceWhileViewingFaq() {
        context = groupFactory.privateGroupInWsWithParticipant();
        prepareTestData();

        loginHelper.performUILogin(context.getParticipant());
        pages.leftSidebarSteps.clickSpaceByName(context.getWorkspaceName());
        pages.leftSidebarSteps.openFaqMenu();
        pages.faqPageSteps.verifyFaqTitleHeaderVisible();

        context.getOwnerBuilder().deleteWorkspace(context.getWorkspaceId());

        verifyPostDeletionState();
    }

    @Test
    @ChannelAllureIds(web = "57634", desktop = "57737")
    @DisplayName("Удаление пространства в момент нахождения на экране настроек приложения")
    public void removeSpaceOnAppSettingsScreen() {
        context = groupFactory.privateGroupInWsWithParticipant();
        prepareTestData();

        loginHelper.performUILogin(context.getParticipant());
        pages.leftSidebarSteps.clickSpaceByName(context.getWorkspaceName());
        pages.leftSidebarSteps.openSettings();
        pages.settingsMenuSteps.verifySettingsTitleHeaderVisible();

        context.getOwnerBuilder().deleteWorkspace(context.getWorkspaceId());

        verifyPostDeletionState();
    }

    // Вынесенная проверка удаления пространства из всех списков
    private void verifyPostDeletionState() {
        pages.workspaceDeletedNotificationModalSteps
                .verifyWorkspaceDeletedModalIsVisible()
                .clickConfirmButton();

        pages.leftSidebarSteps.verifySpaceNotAvailableByName(context.getWorkspaceName());
        pages.leftSidebarSteps.enterOrCreateSpaceButton();
        pages.availableWorkspacesModalSteps
                .verifySpaceNotAvailableByName(context.getWorkspaceName())
                .closeAvailableWorkspacesModal();

        pages.chatListSteps.clickSavedChat();
        pages.chatActions.openMessageContextMenu("test");
        pages.messageBubbleContextMenu.clickForwardButton();
        pages.forwardMenuSteps
                .openForwardSpacesList()
                .verifySpaceNotInForwardList(context.getWorkspaceName());
    }

    private void prepareTestData() {
        context.getParticipantBuilder().sendTextMessage(context.getParticipant().getSavedChatId(), "test");
    }
}
