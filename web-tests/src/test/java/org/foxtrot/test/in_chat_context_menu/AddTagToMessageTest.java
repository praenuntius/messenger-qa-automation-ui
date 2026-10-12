package org.foxtrot.test.in_chat_context_menu;

import io.qameta.allure.AllureId;
import io.qameta.allure.junit5.AllureJunit5;
import org.foxtrot.api.dto.poll.CreatePollRequest;
import org.foxtrot.api.dto.poll.CreatePollRequest.Option;
import org.foxtrot.api.dto.poll.CreatePollRequest.Poll;
import org.foxtrot.api.dto.poll.CreatePollRequest.Settings;
import org.foxtrot.test.BaseTest;
import org.foxtrot.utils.LoginHelper;
import org.foxtrot.utils.factories.contexts.GroupContext;
import org.foxtrot.utils.factories.core.GroupFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;

import static com.codeborne.selenide.Selenide.clearBrowserCookies;
import static com.codeborne.selenide.Selenide.clearBrowserLocalStorage;
import static com.codeborne.selenide.Selenide.refresh;
import static java.lang.Thread.sleep;

@ExtendWith(AllureJunit5.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AddTagToMessageTest extends BaseTest {

    private static final long TAG_IMPORTANT = 3007015062565120L;
    private static final long TAG_DONE = 3007015063621889L;
    private static final long TAG_DISCUSS = 3007015063810306L;

    private static final AtomicLong CLIENT_RANDOM_ID = new AtomicLong(1_200);

    private final LoginHelper loginHelper = new LoginHelper();
    private final GroupFactory groupFactory = new GroupFactory();

    @AfterEach
    void tearDown() {
        groupFactory.cleanUp();
    }

    //TODO: допилить все регрессные кейсы https://testops.domain.net/project/8/test-cases/54567?treeId=28&filterId=40

    @AllureId("72285")
    @Test
    @DisplayName("Поместить тег на свое сообщение")
    public void sendTagOnOwnMessage() {
        GroupContext context = groupFactory.privateGroup();
        String messageId = String.valueOf(
                context.getOwnerBuilder().sendTextMessage(context.getGroupId(), "Owner text via API"));

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.findMessageById(messageId);
        pages.chatActions.openMessageContextMenuById(messageId);
        pages.messageBubbleContextMenu.clickAddTagButton();
        pages.tagsSteps.clickImportantTag();
        pages.tagsVerify.verifyAddedTagsCounter(1);
        pages.inputBarSteps.clickSendMessageButton();
        pages.chatVerify.verifyMessageHasTagById(messageId);
    }

    @AllureId("72286")
    @Test
    @DisplayName("Поместить тег на чужое сообщение")
    public void sendTagOnPeerMessage() {
        GroupContext context = groupFactory.privateGroupWithAdmin();
        String messageId = String.valueOf(
                context.getAdminBuilder().sendTextMessage(context.getGroupId(), "Admin text via API"));

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.findMessageById(messageId);
        pages.chatActions.openMessageContextMenuById(messageId);
        pages.messageBubbleContextMenu.clickAddTagButton();
        pages.tagsSteps.clickImportantTag();
        pages.tagsVerify.verifyAddedTagsCounter(1);
        pages.inputBarSteps.clickSendMessageButton();
        pages.chatVerify.verifyMessageHasTagById(messageId);
    }

    @AllureId("72299")
    @Test
    @DisplayName("Поместить тег на свое редактируемое сообщение")
    public void placeTagOnOwnMessageWhileEditing() {
        GroupContext context = groupFactory.privateGroup();
        String messageId = String.valueOf(context.getOwnerBuilder().sendTextMessage(
                context.getGroupId(),
                "Owner text via API",
                nextClientRandomId(),
                TAG_IMPORTANT));

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.findMessageById(messageId);
        pages.chatActions.openMessageContextMenuById(messageId);
        pages.messageBubbleContextMenu.clickEditButton();
        pages.inputBarSteps.clickTagMenuButton();
        pages.tagsSteps.clickDiscussTag();
        pages.inputBarSteps.verifyTagCounterInInputBar(2);
        pages.inputBarSteps.clickSendMessageButton();
        pages.chatVerify.verifyMessageHasTagById(messageId);
    }

    @AllureId("73964")
    @Test
    @DisplayName("Удалить тег при редактировании сообщения")
    public void deleteTagFromSentMessageWhileEditing() {
        GroupContext context = groupFactory.privateGroup();
        String messageId = String.valueOf(context.getOwnerBuilder().sendTextMessage(
                context.getGroupId(),
                "Owner tagged text via API",
                nextClientRandomId(),
                TAG_IMPORTANT));

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.findMessageById(messageId);
        pages.chatActions.openMessageContextMenuById(messageId);
        pages.messageBubbleContextMenu.clickEditButton();
        pages.inputBarSteps.clickTagMenuButton();
        pages.tagsSteps.clickImportantTag();
        pages.inputBarSteps.verifyNoTagCounterInInputBar(); //дефект https://allprojects.atlassian.net/browse/FB-17716
        pages.inputBarSteps.clickSendMessageButton();
        pages.chatVerify.verifyMessageHasNoTagById(messageId);
    }

    @AllureId("72266")
    @Test
    @DisplayName("Отправить сообщение-файл с тегом")
    public void sendTaggedFileMessage() {
        GroupContext context = groupFactory.privateGroup();

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.inputBarSteps.clickTagMenuButton();
        pages.tagsSteps.clickDoneTag();
        pages.tagsSteps.clickCloseTagMenuButton();
        pages.inputBarSteps.verifyTagCounterInInputBar(1);
        pages.inputBarSteps.uploadMediaFileViaAttachMenu("documents/tinyTXT.txt");
        pages.inputBarSteps.clickSendMessageButton();
        pages.chatVerify
                .verifyFileSent()
                .verifyLastMessageHasTag();
    }

    @AllureId("72225")
    @Test
    @DisplayName("Отправить сообщение текст+медиа с тегом")
    public void sendTaggedMediaMessage() throws InterruptedException {
        GroupContext context = groupFactory.privateGroup();

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.inputBarSteps.clickTagMenuButton();
        pages.tagsSteps.clickDoneTag();
        pages.tagsSteps.clickCloseTagMenuButton();
        pages.inputBarSteps.verifyTagCounterInInputBar(1);
        pages.inputBarSteps.clickMessageInputField();
        pages.inputBarSteps.enterMessage("Tagged text message + multimedia");
        pages.inputBarSteps.uploadMediaFileViaAttachMenu("documents/tinyTXT.txt");
        sleep(300);
        pages.inputBarSteps.uploadMediaFileViaAttachMenu("images/tinyJPG.jpg");
        sleep(300);
        pages.inputBarSteps.uploadMediaFileViaAttachMenu("videos/14MBMOV.mov");
        sleep(300);
        pages.inputBarSteps.clickSendMessageButton();
        pages.chatVerify.verifyMessageSent("Tagged text message + multimedia");
        pages.chatVerify
                .verifyMultimediaSent()
                .verifyLastMessageHasTag();
    }

    @AllureId("72273")
    @Test
    @DisplayName("Отправить сообщение-текст с тегом")
    public void sendTaggedTextMessage() {
        GroupContext context = groupFactory.privateGroup();

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.inputBarSteps.clickTagMenuButton();
        pages.tagsSteps.clickImportantTag();
        pages.tagsSteps.clickCloseTagMenuButton();
        pages.inputBarSteps.verifyTagCounterInInputBar(1);
        pages.inputBarSteps.clickMessageInputField();
        pages.inputBarSteps.enterMessage("Tagged text message");
        pages.inputBarSteps.clickSendMessageButton();
        pages.chatVerify.verifyMessageSent("Tagged text message");
        pages.chatVerify.verifyLastMessageHasTag();
    }

    @AllureId("72226")
    @Test
    @DisplayName("Поместить тег на опрос")
    public void placeTagOnPoll() {
        GroupContext context = groupFactory.privateGroup();

        CreatePollRequest createPollRequest = new CreatePollRequest();
        createPollRequest.setClientRandomId(nextClientRandomId());
        createPollRequest.setGroupId(context.getGroupId());
        createPollRequest.setMessage("HEX");
        createPollRequest.setPeerId(0L);

        Poll poll = new Poll();
        poll.setTitle("Owner's Poll");
        poll.setCountDownMillis(2678400000L);

        Option option1 = new Option();
        option1.setTitle("option1");
        Option option2 = new Option();
        option2.setTitle("option2");
        poll.setOptions(new java.util.ArrayList<>(Arrays.asList(option1, option2)));

        Settings settings = new Settings();
        settings.setAnonymous(true);
        settings.setMultiChoice(true);
        poll.setSettings(settings);

        createPollRequest.setPoll(poll);

        String pollMessageId = String.valueOf(context.getOwnerBuilder().createPoll(createPollRequest));

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.findMessageById(pollMessageId);
        pages.chatActions.openMessageContextMenuById(pollMessageId);
        pages.messageBubbleContextMenu.clickAddTagButton();
        pages.tagsSteps.clickImportantTag();
        pages.tagsVerify.verifyAddedTagsCounter(1);
        pages.inputBarSteps.clickSendMessageButton();
        pages.chatVerify.verifyMessageHasTagById(pollMessageId);
    }

    @AllureId("72268")
    @Test
    @DisplayName("Снять установленный тег с сообщения")
    public void removeSpecifiedTag() {
        GroupContext context = groupFactory.privateGroup();

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.inputBarSteps.clickTagMenuButton();
        pages.tagsSteps.clickDoneTag();
        pages.inputBarSteps.verifyTagCounterInInputBar(1);
        pages.tagsSteps.clickDoneTag();
        pages.inputBarSteps.verifyNoTagCounterInInputBar();
    }

    @AllureId("72352")
    @Test
    @DisplayName("Клик по кнопке 'Искать глобально' открывает меню глобального поиска")
    public void openGlobalSearchViaTag() {
        GroupContext context = groupFactory.privateGroupWithAdmin();
        String messageId = String.valueOf(context.getAdminBuilder().sendTextMessage(
                context.getGroupId(),
                "Admin tagged text via API",
                nextClientRandomId(),
                TAG_DONE));

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.findMessageById(messageId);
        pages.chatActions.openMessageContextMenuById(messageId);
        pages.chatActions.openTagContextMenuById(messageId);
        pages.tagOnMessageContextMenuSteps.clickGlobalSearchButton();
        pages.searchUnitSteps.verifyGlobalSearchElementsVisible();
    }

    @Test
    @DisplayName("Клик по кнопке 'Искать в чате' открывает меню поиска в чате")
    public void openLocalSearchViaTag() {
        GroupContext context = groupFactory.privateGroupWithAdmin();
        String messageId = String.valueOf(context.getAdminBuilder().sendTextMessage(
                context.getGroupId(),
                "Admin tagged text via API",
                nextClientRandomId(),
                TAG_DONE));

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.findMessageById(messageId);
        pages.chatActions.openMessageContextMenuById(messageId);
        pages.chatActions.openTagContextMenuById(messageId);
        pages.tagOnMessageContextMenuSteps.clickLocalSearchButton();
        pages.localSearchUnitSteps.verifyLocalSearchElementsVisible();
    }

    @AllureId("72362")
    @Test
    @DisplayName("Копирование имени тега кнопкой 'Копировать текст'")
    public void copyTagNameViaContextMenu() {
        GroupContext context = groupFactory.privateGroupWithAdmin();
        String messageId = String.valueOf(context.getAdminBuilder().sendTextMessage(
                context.getGroupId(),
                "Admin tagged text via API",
                nextClientRandomId(),
                TAG_DONE));

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.findMessageById(messageId);
        pages.chatVerify.verifyMessageHasTagById(messageId);
        pages.chatActions.openTagContextMenuById(messageId);
        pages.tagOnMessageContextMenuSteps.clickCopyButton();
        pages.snackbarsSteps.verifyTextCopiedSnackbarVisible();
    }

    @AllureId("72357")
    @Test
    @DisplayName("Убрать тег с публикации канала")
    public void removeTagFromPost() {
        GroupContext context = groupFactory.privateChannelWithAdmin();
        String postId = String.valueOf(context.getOwnerBuilder().sendTextMessage(
                context.getGroupId(),
                "Owner tagged post via API",
                nextClientRandomId(),
                TAG_IMPORTANT));

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatVerify.verifyMessageHasTagById(postId);
        pages.chatActions.openTagContextMenuById(postId);
        pages.tagOnMessageContextMenuSteps.clickRemoveButton();
        pages.chatVerify.verifyMessageHasNoTagById(postId);
    }

    @AllureId("72355")
    @Test
    @DisplayName("Убрать публичный тег с сообщений в группе разными ролями")
    public void removeTagFromGroupMessage() {
        GroupContext context = groupFactory.privateGroupWithAdminAndParticipant();

        String ownerTaggedMessageId = String.valueOf(context.getOwnerBuilder().sendTextMessage(
                context.getGroupId(),
                "Owner tagged text via API",
                nextClientRandomId(),
                TAG_IMPORTANT));
        String adminTaggedMessageId = String.valueOf(context.getAdminBuilder().sendTextMessage(
                context.getGroupId(),
                "Admin tagged text via API",
                nextClientRandomId(),
                TAG_DONE));
        String participantTaggedMessageId = String.valueOf(context.getParticipantBuilder().sendTextMessage(
                context.getGroupId(),
                "Participant tagged text via API",
                nextClientRandomId(),
                TAG_DISCUSS));

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.findMessageById(ownerTaggedMessageId);
        pages.chatActions.openMessageContextMenuById(ownerTaggedMessageId);
        pages.messageBubbleContextMenu.clickAddTagButton();
        pages.tagsSteps.clickDoneTag();
        pages.inputBarSteps.clickSendMessageButton();
        pages.chatVerify.verifyMessageHasTagById(ownerTaggedMessageId);
        pages.chatActions.openTagContextMenuByIdAndText(ownerTaggedMessageId, "Готово");
        pages.tagOnMessageContextMenuSteps.clickRemoveButton();
        pages.chatVerify.verifyMessageHasNoTagByIdAndText(ownerTaggedMessageId, "Готово");
        clearBrowserLocalStorage();
        clearBrowserCookies();
        refresh();
        loginHelper.performUILogin(context.getParticipant());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.findMessageById(participantTaggedMessageId);
        pages.chatActions.openMessageContextMenuById(participantTaggedMessageId);
        pages.chatVerify.verifyMessageHasTagById(participantTaggedMessageId);
        pages.chatActions.openTagContextMenuById(participantTaggedMessageId);
        pages.tagOnMessageContextMenuSteps.clickRemoveButton();
        pages.chatVerify.verifyMessageHasNoTagById(participantTaggedMessageId);
        pages.chatActions.findMessageById(adminTaggedMessageId);
        pages.chatActions.openMessageContextMenuById(adminTaggedMessageId);
        pages.chatVerify.verifyMessageHasTagById(adminTaggedMessageId);
        pages.chatActions.openTagContextMenuById(adminTaggedMessageId);
        pages.tagOnMessageContextMenuSteps.clickRemoveButton();
        pages.chatVerify.verifyMessageHasNoTagById(adminTaggedMessageId);
    }

    @AllureId("72356")
    @Test
    @DisplayName("Убрать тег с сообщения в P2P-чате")
    public void removeTagFromP2P() {
        GroupContext context = groupFactory.privateGroupWithAdmin();
        long p2pGroupId = context.getOwnerBuilder().createP2PGroup(context.getAdmin());

        String ownerTaggedP2PMessageId = String.valueOf(context.getOwnerBuilder().sendTextMessage(
                p2pGroupId,
                "Owner tagged P2P message via API",
                nextClientRandomId(),
                TAG_IMPORTANT));
        String adminTaggedP2PMessageId = String.valueOf(context.getAdminBuilder().sendTextMessage(
                p2pGroupId,
                "Admin tagged P2P message via API",
                nextClientRandomId(),
                TAG_DONE));

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getAdmin().getName());
        pages.chatActions.findMessageById(ownerTaggedP2PMessageId);
        pages.chatVerify.verifyMessageHasTagById(ownerTaggedP2PMessageId);
        pages.chatActions.openTagContextMenuById(ownerTaggedP2PMessageId);
        pages.tagOnMessageContextMenuSteps.clickRemoveButton();
        pages.chatVerify.verifyMessageHasNoTagById(ownerTaggedP2PMessageId);
        pages.chatActions.findMessageById(adminTaggedP2PMessageId);
        pages.chatVerify.verifyMessageHasTagById(adminTaggedP2PMessageId);
        pages.chatActions.openTagContextMenuById(adminTaggedP2PMessageId);
        pages.tagOnMessageContextMenuSteps.clickRemoveButton();
        pages.chatVerify.verifyMessageHasNoTagById(adminTaggedP2PMessageId);
    }

    private static long nextClientRandomId() {
        return CLIENT_RANDOM_ID.incrementAndGet();
    }
}
