package org.foxtrot.test.chats_and_spaces;

import org.foxtrot.allure.ChannelAllureIds;
import io.qameta.allure.junit5.AllureJunit5;
import org.foxtrot.enums.UsersEnum;
import org.foxtrot.test.BaseTest;
import org.foxtrot.utils.ChatHistoryExportArchiveSteps;
import org.foxtrot.utils.LoginHelper;
import org.foxtrot.utils.factories.contexts.GroupContext;
import org.foxtrot.utils.factories.core.GroupFactory;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;

@ExtendWith(AllureJunit5.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ChatHistoryExportTest extends BaseTest {

    private final LoginHelper loginHelper = new LoginHelper();
    private final GroupFactory groupFactory = new GroupFactory();
    private final ChatHistoryExportArchiveSteps archiveSteps = new ChatHistoryExportArchiveSteps();

    @AfterEach
    public void tearDown() {
        groupFactory.cleanUp();
    }

    @ChannelAllureIds(web = "54119", desktop = "53829")
    @Test
    @DisplayName("Кнопка \"Экспорт истории чата\" НЕ отображается в групповом чате для обычного пользователя")
    public void exportHistoryButtonIsNotVisibleForParticipantInGroupChat() {
        var context = groupFactory.privateGroupWithParticipant();
        loginAndVerifyExportHistoryButtonIsNotVisible(context, context.getParticipant());
    }

    @ChannelAllureIds(web = "54122", desktop = "53832")
    @Test
    @DisplayName("Кнопка \"Экспорт истории чата\" НЕ отображается в канале для администратора")
    public void exportHistoryButtonIsNotVisibleForAdminInChannel() {
        var context = groupFactory.privateChannelWithAdmin();
        loginAndVerifyExportHistoryButtonIsNotVisible(context, context.getAdmin());
    }

    @ChannelAllureIds(web = "54121", desktop = "53831")
    @Test
    @DisplayName("Кнопка \"Экспорт истории чата\" НЕ отображается в канале для владельца")
    public void exportHistoryButtonIsNotVisibleForOwnerInChannel() {
        var context = groupFactory.privateChannel();
        loginAndVerifyExportHistoryButtonIsNotVisible(context, context.getOwner());
    }

    @ChannelAllureIds(web = "54123", desktop = "53833")
    @Test
    @DisplayName("Кнопка \"Экспорт истории чата\" НЕ отображается в канале для обычного пользователя")
    public void exportHistoryButtonIsNotVisibleForParticipantInChannel() {
        var context = groupFactory.privateChannelWithParticipant();
        loginAndVerifyExportHistoryButtonIsNotVisible(context, context.getParticipant());
    }

    @ChannelAllureIds(web = "54120", desktop = "53830")
    @Test
    @DisplayName("Кнопка \"Экспорт истории чата\" отображается в p2p для обоих пользователей")
    public void exportHistoryButtonIsVisibleForBothUsersInP2pChat() {
        var context = groupFactory.p2pChat();
        context.getOwnerBuilder().sendTextMessages(context.getGroupId(), "test");

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getParticipant());
        pages.topbarSteps.clickPreferencesMenuButton();
        pages.chatHeaderPreferencesMenuSteps.verifyExportHistoryButtonIsVisible();

        loginHelper.performUILoginForTest(context.getParticipant());
        pages.chatListSteps.clickChatByName(context.getOwner());
        pages.topbarSteps.clickPreferencesMenuButton();
        pages.chatHeaderPreferencesMenuSteps.verifyExportHistoryButtonIsVisible();
    }

    @ChannelAllureIds(web = "54118", desktop = "53828")
    @Test
    @DisplayName("Кнопка \"Экспорт истории чата\" отображается в групповом чате для администратора")
    public void exportHistoryButtonIsVisibleForAdminInGroupChat() {
        var context = groupFactory.privateGroupWithAdmin();
        loginAndVerifyExportHistoryButtonIsVisible(context, context.getAdmin());
    }

    @ChannelAllureIds(web = "54117", desktop = "53827")
    @Test
    @DisplayName("Кнопка \"Экспорт истории чата\" отображается в групповом чате для владельца")
    public void exportHistoryButtonIsVisibleForOwnerInGroupChat() {
        var context = groupFactory.privateGroup();
        loginAndVerifyExportHistoryButtonIsVisible(context, context.getOwner());
    }

    @ChannelAllureIds(web = "54125", desktop = "53852 ")
    @Test
    @DisplayName("Формирование архива для экспорта истории чата")
    public void shouldCreateArchiveForChatHistoryExport() {
        var context = groupFactory.privateGroup();

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.topbarSteps.clickPreferencesMenuButton();
        pages.chatHeaderPreferencesMenuSteps.clickExportHistoryButton();
        pages.chatHistoryExportModalSteps.clickExportButton();
        pages.snackbarsSteps.verifyChatHistoryExportIsVisible();
    }

    @ChannelAllureIds(web = "54126", desktop = "53836")
    @Test
    @DisplayName("Отображение архива в системном чате, который состоит из одной части")
    public void verifySinglePartArchiveInSystemChat() {
        var context = groupFactory.privateGroup();

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.topbarSteps.clickPreferencesMenuButton();
        pages.chatHeaderPreferencesMenuSteps.clickExportHistoryButton();
        pages.chatHistoryExportModalSteps.clickExportButton();
        pages.snackbarsSteps.verifyChatHistoryExportIsVisible();
        pages.chatListSteps.clickTangoChat();
        pages.chatVerify.verifyExportMessageAndArchiveDisplayed(context.getGroupName());
    }

    @Tag("archive")
    @ChannelAllureIds(web = "54128", desktop = "53883")
    @Test
    @DisplayName("Проверка структуры распакованного архива")
    public void checkingUnpackedArchive() throws IOException {
        var context = groupFactory.privateGroup();

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.topbarSteps.clickPreferencesMenuButton();
        pages.chatHeaderPreferencesMenuSteps.clickExportHistoryButton();
        pages.chatHistoryExportModalSteps.clickExportButton();
        pages.snackbarsSteps.verifyChatHistoryExportIsVisible();
        pages.chatListSteps.clickTangoChat();
        pages.chatVerify.verifyExportMessageAndArchiveDisplayed(context.getGroupName());

        pages.chatActions.clickChatHistoryExportArchive(context.getGroupName());
        archiveSteps.verifyLatestDownloadedArchiveStructure();
    }

    private void loginAndVerifyExportHistoryButtonIsNotVisible(GroupContext context, UsersEnum user) {
        loginHelper.performUILogin(user);
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.topbarSteps.clickPreferencesMenuButton();
        pages.chatHeaderPreferencesMenuSteps.verifyExportHistoryButtonIsNotVisible();
    }

    private void loginAndVerifyExportHistoryButtonIsVisible(GroupContext context, UsersEnum user) {
        loginHelper.performUILogin(user);
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.topbarSteps.clickPreferencesMenuButton();
        pages.chatHeaderPreferencesMenuSteps.verifyExportHistoryButtonIsVisible();
    }
}