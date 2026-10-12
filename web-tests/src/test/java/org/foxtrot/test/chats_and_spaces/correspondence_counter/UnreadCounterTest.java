package org.foxtrot.test.chats_and_spaces.correspondence_counter;

import com.codeborne.selenide.WebDriverRunner;
import org.foxtrot.allure.ChannelAllureIds;
import org.foxtrot.test.BaseTest;
import org.foxtrot.utils.BrowserSession;
import org.foxtrot.utils.LoginHelper;
import org.foxtrot.utils.factories.core.GroupFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;

public class UnreadCounterTest extends BaseTest {

    private static final String TEST_MESSAGE = "test";

    private final LoginHelper loginHelper = new LoginHelper();
    private final GroupFactory groupFactory = new GroupFactory();

    @AfterEach
    public void tearDown() {
        groupFactory.cleanUp();
    }

    @Test
    @ChannelAllureIds(web = "52615", desktop = "82031")
    @DisplayName("Обновление счетчика после прочтения входящих сообщений")
    public void CounterUpdateOnRead() {
        var ct = groupFactory.createMultipleGroupsWithParticipants(5, true, false, true);
        var ownerBld = ct.get(0).getOwnerBuilder();
        String wsName = ct.get(0).getWorkspaceName();

        loginHelper.performUILogin(ct.get(0).getParticipant());
        pages.leftSidebarSteps.clickSpaceByName(wsName);
        for (var group : ct) {
            ownerBld.sendTextMessages(group.getGroupId(), TEST_MESSAGE);
        }
        pages.localStorageJsSteps.switchToGroupsCounterMode();
        pages.leftSidebarSteps.verifyUnreadBadgeCountOnSpace(wsName, "5");
        pages.chatListSteps.clickChatByName(ct.get(0).getGroupName());
        pages.leftSidebarSteps.verifyUnreadBadgeCountOnSpace(wsName, "4");
    }

    @Test
    @ChannelAllureIds(web = "52618", desktop = "82033")
    @DisplayName("Отображение статуса в сети в разных местах приложения")
    @Disabled("Забагованно поведение, баг FB-4289")
    public void onlineStatusDisplayedInDifferentPlaces() {
        var ct = groupFactory.p2pChatWithMessage();
        WebDriver ownerDriver = WebDriverRunner.getWebDriver();

        try (BrowserSession participant = BrowserSession.open()) {
            BrowserSession.with(ownerDriver, () -> {
                loginHelper.performUILogin(ct.getOwner());
                pages.chatListSteps.clickChatByName(ct.getParticipant().getName());
            });

            participant.run(() -> {
                pages.baseSteps.openStartPage();
                loginHelper.performUILogin(ct.getParticipant());
                pages.chatListSteps.clickChatByName(ct.getOwner().getName());
            });
        }
    }

    @Test
    @ChannelAllureIds(web = "52599", desktop = "82027")
    @DisplayName("Отображение счетчика в p2p, после входящего сообщения (видео+текст)")
    public void counterDisplaysOneAfterP2pVideoWithTextMessage() {
        var ct = groupFactory.p2pChat();

        loginHelper.performUILogin(ct.getParticipant());
        ct.getParticipantBuilder().markSpaceRead(-1L);
        ct.getOwnerBuilder().sendVideoWithMessage(ct.getGroupId(), TEST_MESSAGE, "-1");

        pages.chatListSteps.verifyChatCounter(ct.getOwner().getName(), "1");
    }

    @Test
    @ChannelAllureIds(web = "52591", desktop = "82024")
    @DisplayName("Отображение счетчика в p2p после двух входящих сообщений")
    public void counterDisplaysTwoAfterTwoP2pIncomingMessages() {
        var ct = groupFactory.p2pChat();

        loginHelper.performUILogin(ct.getParticipant());
        ct.getParticipantBuilder().markSpaceRead(-1L);
        ct.getOwnerBuilder().sendTextMessages(ct.getGroupId(), TEST_MESSAGE, TEST_MESSAGE);

        pages.chatListSteps.verifyChatCounter(ct.getOwner().getName(), "2");
    }

    @Test
    @ChannelAllureIds(web = "52594", desktop = "52594")
    @DisplayName("Отображение счетчика в группе после входящего сообщения (изображение+текст)")
    public void counterDisplaysOneAfterGroupImageWithTextMessage() {
        var ct = groupFactory.privateGroupWithParticipant();

        loginHelper.performUILogin(ct.getParticipant());
        ct.getParticipantBuilder().markSpaceRead(-1L);
        ct.getOwnerBuilder().sendImageWithMessage(ct.getGroupId(), TEST_MESSAGE, "-1");

        pages.chatListSteps.verifyChatCounter(ct.getGroupName(), "1");
    }

    @Test
    @ChannelAllureIds(web = "52597", desktop = "82026")
    @DisplayName("Отображение счетчика в закрытом канале пространства, после входящей публикации")
    public void counterDisplaysOneAfterPublicationInClosedWorkspaceChannel() {
        var ct = groupFactory.privateChannelInWsWithParticipant();

        loginHelper.performUILogin(ct.getParticipant());
        ct.getParticipantBuilder().markSpaceRead(ct.getWorkspaceId());
        ct.getOwnerBuilder().sendTextMessages(ct.getGroupId(), TEST_MESSAGE);

        pages.leftSidebarSteps.clickSpaceByName(ct.getWorkspaceName());
        pages.chatListSteps.verifyChatCounter(ct.getGroupName(), "1");
    }

    @Test
    @ChannelAllureIds(web = "52609", desktop = "82029")
    @DisplayName("Отображение счетчика в открытом канале, после входящих публикаций")
    public void counterDisplaysTwoAfterPublicationsInOpenChannel() {
        var ct = groupFactory.publicChannelWithParticipant();

        loginHelper.performUILogin(ct.getParticipant());
        ct.getParticipantBuilder().markSpaceRead(-1L);
        ct.getOwnerBuilder().sendTextMessages(ct.getGroupId(), TEST_MESSAGE, TEST_MESSAGE);

        pages.chatListSteps.verifyChatCounter(ct.getGroupName(), "2");
    }

    @Test
    @ChannelAllureIds(web = "52616", desktop = "82032")
    @DisplayName("Отображение чата \"Сохраненные\" при форварде")
    public void savedChatDisplayedFirstInForwardList() {
        var ct = groupFactory.privateGroupWithParticipant();
        String messageId = String.valueOf(ct.getOwnerBuilder().sendTextMessage(ct.getGroupId(), TEST_MESSAGE));

        loginHelper.performUILogin(ct.getOwner());
        pages.chatListSteps.clickChatByName(ct.getGroupName());
        pages.chatActions.findMessageById(messageId);
        pages.chatActions.openMessageContextMenuById(messageId);
        pages.messageBubbleContextMenu.clickForwardButton();
        pages.forwardMenuSteps.verifyForwardMenuAvailable();
        pages.forwardMenuSteps.verifySavedChatIsFirstInForwardList();
    }

    @Test
    @ChannelAllureIds(web = "52614", desktop = "82030")
    @DisplayName("Установить формат счетчика: Количество чатов с непрочитанными сообщениями")
    public void setCounterFormatToUnreadChatsCount() {
        var ct = groupFactory.privateGroupInWsWithParticipant();
        var ownerBld = ct.getOwnerBuilder();
        String wsName = ct.getWorkspaceName();
        ownerBld.sendTextMessages(ct.getGroupId(), TEST_MESSAGE, TEST_MESSAGE);

        loginHelper.performUILogin(ct.getParticipant());

        pages.leftSidebarSteps.verifyUnreadBadgeCountOnSpace(wsName, "2");
        pages.leftSidebarSteps.openSettings();
        pages.settingsMenuSteps.verifySettingsTitleHeaderVisible();
        pages.notificationsTabSteps.selectUnreadChatsCounterMode();

        pages.settingsMenuSteps.closeSettingsMenu();
        pages.leftSidebarSteps.verifyUnreadBadgeCountOnSpace(wsName, "1");
    }
}