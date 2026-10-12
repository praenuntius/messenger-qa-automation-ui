package org.foxtrot.utils;

import io.restassured.response.Response;
import lombok.Getter;
import org.foxtrot.api.DataGenerator;
import org.foxtrot.api.dto.account.UpdateFullProfileRequest;
import org.foxtrot.api.dto.contact.AddContactRequest;
import org.foxtrot.api.dto.group.*;
import org.foxtrot.api.dto.group.response.CreateCommentGroupResponse;
import org.foxtrot.api.dto.group.response.CreateGroupResponse;
import org.foxtrot.api.dto.message.*;
import org.foxtrot.api.dto.message.response.SendTextMessageResponse;
import org.foxtrot.api.dto.poll.CreatePollRequest;
import org.foxtrot.api.dto.poll.VotePollRequest;
import org.foxtrot.api.dto.poll.response.CreatePollResponse;
import org.foxtrot.utils.factories.contexts.GroupContext;
import org.foxtrot.api.dto.users.BlockRequest;
import org.foxtrot.api.dto.users.UnBlockRequest;
import org.foxtrot.api.dto.workSpace.*;
import org.foxtrot.api.dto.workSpace.response.CreateWorkSpaceResponse;
import org.foxtrot.api.steps.*;
import org.foxtrot.enums.GroupTypesEnum;
import org.foxtrot.enums.UsersEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class TestDataBuilder {

    private static final Logger logger = LoggerFactory.getLogger(TestDataBuilder.class);

    // --- ЗАВИСИМОСТИ И КОНТЕКСТ ---
    private final String processId;
    private final ContactApiSteps contactApiSteps;
    private final GroupApiSteps groupApiSteps;
    private final MessageApiSteps messageApiSteps;
    private final MassageApiSteps massageApiSteps;
    private final PollApiSteps pollApiSteps;
    private final UsersApiSteps usersApiSteps;
    private final WorkSpaceApiSteps workSpaceApiSteps;
    private final AccountApiSteps accountApiSteps;

    public TestDataBuilder(UsersEnum user) {
        this.processId = user.getProcessId();
        this.contactApiSteps = new ContactApiSteps(user);
        this.groupApiSteps = new GroupApiSteps(user);
        this.messageApiSteps = new MessageApiSteps(user);
        this.massageApiSteps = new MassageApiSteps(user);
        this.pollApiSteps = new PollApiSteps();
        this.usersApiSteps = new UsersApiSteps(user);
        this.workSpaceApiSteps = new WorkSpaceApiSteps(user);
        this.accountApiSteps = new AccountApiSteps(user);
    }

    // --- ГРУППЫ (GROUPS) ---
    public Long createGroup(GroupConfig config) {
        CreateGroupRequest request = new CreateGroupRequest(
                config.name,
                config.type.name(),
                config.visible
        );

        if (config.description != null) {
            request.setDescription(config.description);
        }

        if (config.image != null) {
            request.setImage(config.image);
        }

        // ChannelOptions currently control comments/reactions for both channels and groups in our API.
        if (config.commentsEnabled != null || config.reactionsEnabled != null) {
            CreateGroupRequest.ChannelOptions channelOptions = new CreateGroupRequest.ChannelOptions();
            if (config.commentsEnabled != null) {
                channelOptions.setCommentsEnabled(config.commentsEnabled);
            }
            if (config.reactionsEnabled != null) {
                channelOptions.setReactionsEnabled(config.reactionsEnabled);
            }
            request.setChannelOptions(channelOptions);
        }

        CreateGroupResponse response;
        if (config.workspaceId != null) {
            response = groupApiSteps.createGroup(request, config.workspaceId);
        } else {
            response = groupApiSteps.createGroup(request);
        }
        if (response == null || response.getGroupId() == null) {
            throw new IllegalStateException(
                    "createGroup API returned null groupId. " +
                            "name=" + config.name +
                            ", type=" + config.type +
                            ", visible=" + config.visible +
                            ", workspaceId=" + config.workspaceId
            );
        }
        return response.getGroupId();
    }

    public Long createCommentGroup(Long channelId, Long postMessageId) {
        // Пост-сообщение может быть ещё не видно read-модели сервера сразу после sendTextMessage
        // (eventual consistency) — ретраим на MESSAGE_DOES_NOT_EXIST, как в addReactionWithRetry.
        int maxAttempts = 25;
        long delayMs = 500;
        CreateCommentGroupRequest request = new CreateCommentGroupRequest(channelId, postMessageId);
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            Response response = groupApiSteps.createCommentGroupRaw(request);
            if (response.getStatusCode() < 400) {
                long groupId = response.as(CreateCommentGroupResponse.class).getGroupId();
                if (groupId != 0) {
                    return groupId;
                }
            }
            String body = response.getBody().asString();
            boolean messageNotReady = body.contains("MESSAGE_DOES_NOT_EXIST");
            if (!messageNotReady || attempt == maxAttempts) {
                throw new IllegalStateException(
                        "Failed to create comment group for channel " + channelId
                                + ", post " + postMessageId + ": HTTP " + response.getStatusCode() + " " + body
                );
            }
            sleep(delayMs);
        }
        throw new IllegalStateException("createCommentGroup: недостижимое состояние");
    }

    public Long createP2PGroup(UsersEnum otherUser) {
        return groupApiSteps
                .createP2PGroup(new CreateP2PGroupRequest(otherUser.getId()))
                .getGroupId();
    }

    public void updateMessageAutoDeletion(Long groupId, Long sek) {
        groupApiSteps.updateMessageAutoDeletion(
                new UpdateMessageAutoDeletionRequest(groupId, sek));
    }

    public void updateGroupNotificationOFF(Long groupId, Long workspaceId) {
        groupApiSteps.updateGroupNotification(new UpdateGroupNotification(
                groupId, "OFF"), workspaceId);
    }

    public void updateGroupUnread(Long groupId, Long workspaceId) {
        groupApiSteps.updateGroupUnread(new UpdateGroupUnreadRequest(
                groupId, true), workspaceId);
    }

    public void updateGroupUnread(Long groupId, Boolean unread) {
        groupApiSteps.updateGroupUnread(new UpdateGroupUnreadRequest(
                groupId, unread));
    }

    public void updateGroup(Long groupId, Long workspaceId, Boolean visible,
            String name, String inviteLink) {
        UpdateGroupRequest request = new UpdateGroupRequest();
        request.setGroupId(groupId);
        request.setVisible(visible);
        request.setName(name);
        request.setInviteLink(inviteLink);
        request.setDescription("");
        request.setChannelOptions(new UpdateGroupRequest.ChannelOptions(true));
        groupApiSteps.updateGroup(request, workspaceId);
    }

    public void updateMessageAutoDeletion(Long groupId) {
        groupApiSteps.updateMessageAutoDeletion(
                new UpdateMessageAutoDeletionRequest(groupId, null));
    }

    public Long createScheduledGroup(Long groupId) {
        return groupApiSteps
                .createScheduledGroup(new CreateScheduledGroupRequest(groupId))
                .getGroupId();
    }

    public void deleteGroup(Long groupId) {
        groupApiSteps.deleteGroup(new DeleteGroupRequest(groupId));
    }

    public void deleteGroup(Long groupId, Long workspaceId) {
        groupApiSteps.deleteGroup(new DeleteGroupRequest(groupId), workspaceId);
    }

    public void clearHistory(Long groupId) {
        groupApiSteps.clearHistory(new ClearHistoryRequest(groupId));
    }

    public void hideGroup(Long groupId) {
        groupApiSteps.hideGroup(new HideGroupRequest(groupId));
    }

    public void addUsers(Long groupId, UsersEnum member) {
        awaitGroupReady("addUsers(group=" + groupId + ")",
                () -> groupApiSteps.addUsers(new AddUsersRequest(groupId, List.of(member.getId()))));
    }

    public void addUsers(Long groupId, Long workspaceId, UsersEnum member) {
        awaitGroupReady("addUsers(group=" + groupId + ", ws=" + workspaceId + ")",
                () -> groupApiSteps.addUsers(new AddUsersRequest(groupId, List.of(member.getId())), workspaceId));
    }

    public void removeUser(Long groupId, UsersEnum userToRemove) {
        groupApiSteps
                .removeUser(new RemoveUserRequest(groupId, userToRemove.getId()));
    }

    public void editAdmin(Long groupId, UsersEnum member, Boolean makeAdmin) {
        awaitGroupReady("editAdmin(group=" + groupId + ")",
                () -> groupApiSteps.editAdmin(new EditAdminRequest(groupId, member.getId(), makeAdmin)));
    }

    public void editAdmin(Long groupId, Long workspaceId, UsersEnum member, Boolean makeAdmin) {
        awaitGroupReady("editAdmin(group=" + groupId + ", ws=" + workspaceId + ")",
                () -> groupApiSteps.editAdmin(new EditAdminRequest(groupId, member.getId(), makeAdmin), workspaceId));
    }

    public String regenerateInviteLink(Long groupId) {
        return groupApiSteps
                .regenerateInviteLink(new RegenerateInviteLinkRequest(groupId))
                .getInviteLink();
    }

    // --- СООБЩЕНИЯ (MESSAGES) ---

    public Long sendGroupMentionMessage(Long groupId, String message, Long mentionedGroupId) {
        SendTextMessageRequest request = new SendTextMessageRequest(message, 0L, groupId);

        SendTextMessageRequest.GroupMention groupMention = new SendTextMessageRequest.GroupMention();
        groupMention.setLength(message.length());
        groupMention.setGroupId(mentionedGroupId);
        groupMention.setGroupType("GROUP");
        groupMention.setWorkspaceId(-1L);

        SendTextMessageRequest.Entity entity = new SendTextMessageRequest.Entity();
        entity.setGroupMention(groupMention);

        request.setEntities(Collections.singletonList(entity));

        return messageApiSteps.sendTextMessage(request).getMessageId();
    }

    public List<Long> sendTextMessages(Long groupId, String... texts) {
        List<Long> messageIds = new ArrayList<>();

        for (String text : texts) {
            Long id = messageApiSteps
                    .sendTextMessage(new SendTextMessageRequest(text, 0, groupId))
                    .getMessageId();
            messageIds.add(id);
        }

        return messageIds;
    }

    public void sendTextMessages(Long groupId, int count, String textPrefix) {
        for (int i = 0; i < count; i++) {
            sendTextMessage(groupId, textPrefix + i);
        }
    }

    public Long sendTextMessage(Long groupId, String text) {
        Response response = awaitGroupReady("sendTextMessage(group=" + groupId + ")",
                () -> messageApiSteps.sendTextMessageRaw(new SendTextMessageRequest(text, 0, groupId), null));
        return response.as(SendTextMessageResponse.class).getMessageId();
    }

    public Response formatTextMessage(Long groupId, Long messageId, String message) {
        return messageApiSteps.formatTextMessage(
                FormatMessageRequest.allTextStyles(groupId, messageId, message));
    }

    public Long sendTextMessageWithReply(Long groupId, String text, long replyToMessageId) {
        SendTextMessageRequest request = new SendTextMessageRequest(text, 0, groupId);
        request.setReplyMsgId(replyToMessageId);
        return messageApiSteps.sendTextMessage(request).getMessageId();
    }

    public Long sendTextMessage(Long groupId, String text, long clientRandomId, long... tags) {
        return messageApiSteps
                .sendTextMessage(new SendTextMessageRequest(text, clientRandomId, groupId, tags), "-1")
                .getMessageId();
    }

    public Long sendTextMessage(Long groupId, String text, String workspaceId) {
        return messageApiSteps
                .sendTextMessage(new SendTextMessageRequest(text, 0, groupId), workspaceId)
                .getMessageId();
    }

    public Long sendTextMessage(Long groupId, String text, String workspaceId, long clientRandomId, long... tags) {
        return messageApiSteps
                .sendTextMessage(new SendTextMessageRequest(text, clientRandomId, groupId, tags), workspaceId)
                .getMessageId();
    }

    public Long sendLinkMassage(String url, Long groupId) {
        return massageApiSteps.sendLinkMassage(new org.foxtrot.api.dto.massage.SendLinkMassageRequest(url, groupId))
                .getMessageId();
    }

    public void sendTextMassage(String text, Long groupId) {
        massageApiSteps.sendTextMassage(new org.foxtrot.api.dto.massage.SendTextMassageRequest(text, 0, groupId));
    }

    public void pinMessage(Long groupId, String messageId) {
        messageApiSteps.pinMessage(
                new PinMessageRequest(String.valueOf(groupId), Collections.singletonList(messageId)),
                "-1"
        );
    }

    public void pinMessages(Long groupId, List<String> messageIds) {
        messageApiSteps.pinMessage(
                new PinMessageRequest(String.valueOf(groupId), messageIds),
                "-1"
        );
    }

    public Long createPoll(CreatePollRequest request, String workspaceId) {
        return createPollResponse(request, workspaceId).getMessageId();
    }

    public CreatePollResponse createPollResponse(CreatePollRequest request, String workspaceId) {
        return pollApiSteps.createPoll(request, processId, workspaceId);
    }

    public void votePollFirstOption(GroupContext group) {
        votePoll(group, group.getPollFirstOptionId());
    }

    public void votePoll(GroupContext group, Long optionId) {
        if (group.getPollId() == null || group.getMessageId() == null || optionId == null) {
            throw new IllegalStateException("GroupContext must contain pollId, messageId and option id for vote");
        }
        VotePollRequest voteRequest = new VotePollRequest(
                group.getGroupId(),
                group.getMessageId(),
                group.getPollId(),
                Collections.singletonList(optionId)
        );
        pollApiSteps.vote(voteRequest, processId, workspaceHeader(group));
    }

    private static String workspaceHeader(GroupContext group) {
        return group.getWorkspaceId() != null
                ? String.valueOf(group.getWorkspaceId())
                : "-1";
    }

    public Long sendImage(Long groupId, String workspaceId) {
        SendImageRequest request = DataGenerator.getDefaultSendImageMessageRequest(groupId);
        return messageApiSteps.sendImage(request, workspaceId).getMessageId();
    }

    public Long sendVideo(Long groupId, String workspaceId) {
        SendVideoRequest request = DataGenerator.getDefaultSendVideoMessageRequest(groupId);
        return messageApiSteps.sendVideo(request, workspaceId).getMessageId();
    }

    public Long sendAudio(Long groupId, String workspaceId) {
        SendAudioRequest request = DataGenerator.getDefaultSendAudioMessageRequest(groupId);
        return messageApiSteps.sendAudio(request, workspaceId).getMessageId();
    }

    public Long sendFile(Long groupId, String workspaceId) {
        SendFileRequest request = DataGenerator.getDefaultSendFileMessageRequest(groupId);
        return messageApiSteps.sendFile(request, workspaceId).getMessageId();
    }

    public Long sendVideoWithMessage(Long groupId, String messageText, String workspaceId) {
        SendVideoRequest request = DataGenerator.getDefaultSendVideoMessageRequest(groupId);
        request.setMessage(messageText);
        return messageApiSteps.sendVideo(request, workspaceId).getMessageId();
    }

    public Long sendImageWithMessage(Long groupId, String messageText, String workspaceId) {
        SendImageRequest request = DataGenerator.getDefaultSendImageMessageRequest(groupId);
        request.setMessage(messageText);
        return messageApiSteps.sendImage(request, workspaceId).getMessageId();
    }

    public Long sendFileWithMessage(Long groupId, String messageText, String workspaceId) {
        SendFileRequest request = DataGenerator.getDefaultSendFileMessageRequest(groupId);
        request.setMessage(messageText);
        return messageApiSteps.sendFile(request, workspaceId).getMessageId();
    }

    public Long sendContact(Long groupId, long contactUserId, String workspaceId) {
        SendContactRequest request = new SendContactRequest(System.currentTimeMillis(), groupId, contactUserId);
        return messageApiSteps.sendContactMessage(request, workspaceId).getMessageId();
    }

    public Long sendContact(Long groupId, long contactUserId) {
        SendContactRequest request = new SendContactRequest(System.currentTimeMillis(), groupId, contactUserId);
        return messageApiSteps.sendContactMessage(request, "-1").getMessageId();
    }

    public void addReaction(Long messageId, Long groupId, String reaction) {
        addReactionWithRetry(messageId, groupId, reaction, "-1");
    }

    public void addReaction(Long messageId, Long groupId, String reaction, Long workspaceId) {
        addReactionWithRetry(messageId, groupId, reaction, String.valueOf(workspaceId));
    }

    private void addReactionWithRetry(Long messageId, Long groupId, String reaction, String workspaceId) {
        int maxAttempts = 10;
        long delayMs = 300;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            Response response = messageApiSteps.addReaction(messageId, groupId, reaction, workspaceId);
            if (response.getStatusCode() < 400) {
                return;
            }
            String body = response.getBody().asString();
            boolean messageNotReady = body.contains("MESSAGE_DOES_NOT_EXIST");
            if (!messageNotReady || attempt == maxAttempts) {
                throw new IllegalStateException(
                        "Failed to add reaction '" + reaction + "' on message " + messageId
                                + " in group " + groupId + ": HTTP " + response.getStatusCode() + " " + body
                );
            }
            sleep(delayMs);
        }
    }

    /**
     * Только что созданная (через createGroup) группа может быть ещё не видна read-модели сервера
     * (eventual consistency), из-за чего addUsers/editAdmin/sendTextMessage сразу после создания
     * отвечают 400. Аналогично editAdmin сразу после addUsers может вернуть
     * EDITED_USER_DOES_NOT_EXIST_IN_GROUP, пока участник не появился в read-модели.
     * Такие транзиентные ошибки ретраим, на реальные — падаем явно с телом ответа.
     */
    private Response awaitGroupReady(String op, Supplier<Response> call) {
        int maxAttempts = 15;
        long delayMs = 400;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            Response response = call.get();
            if (response.getStatusCode() < 400) {
                return response;
            }
            String body = response.getBody().asString();
            if (!isTransientGroupApiError(body) || attempt == maxAttempts) {
                throw new IllegalStateException(op + " failed: HTTP " + response.getStatusCode() + " " + body);
            }
            sleep(delayMs);
        }
        throw new IllegalStateException(op + ": недостижимое состояние");
    }

    private static boolean isTransientGroupApiError(String body) {
        return body.contains("GROUP_DOES_NOT_EXIST")
                || body.contains("MESSAGE_DOES_NOT_EXIST")
                || body.contains("EDITED_USER_DOES_NOT_EXIST_IN_GROUP")
                || body.contains("USER_DOES_NOT_EXIST_IN_GROUP");
    }

    private static void sleep(long delayMs) {
        try {
            Thread.sleep(delayMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    public void confirmMessage(Long messageId, Long chatId) {
        messageApiSteps.confirmMessage(messageId, chatId);
    }

    public Long sendScheduledText(Long scheduledGroupId, String text) {
        return messageApiSteps
                .sendTextMessage(DataGenerator.getDefaultSendTextMessageRequestScheduled(scheduledGroupId, text))
                .getMessageId();
    }

    public Long sendScheduledTextAt(Long scheduledGroupId, String text, long secondsUntilSend) {
        long scheduledAt = System.currentTimeMillis() + secondsUntilSend;
        return messageApiSteps
                .sendTextMessage(SendTextMessageRequest.scheduled(text, scheduledGroupId, scheduledAt))
                .getMessageId();
    }

    public Long sendScheduledImage(Long scheduledGroupId) {
        return messageApiSteps
                .sendImage(DataGenerator.getDefaultSendImageMessageRequestScheduled(scheduledGroupId), "-1")
                .getMessageId();
    }

    public Long sendScheduledVideo(Long scheduledGroupId) {
        return messageApiSteps
                .sendVideo(DataGenerator.getDefaultSendVideoMessageRequestScheduled(scheduledGroupId), "-1")
                .getMessageId();
    }

    public Long sendScheduledTextWithReply(Long scheduledGroupId, String text, Long replyToMessageId) {
        return messageApiSteps
                .sendTextMessage(
                        DataGenerator.getDefaultSendTextMessageRequestScheduledWithReply(scheduledGroupId, text,
                                replyToMessageId))
                .getMessageId();
    }

    public Long createPoll(CreatePollRequest request) {
        return createPollResponse(request).getMessageId();
    }

    public CreatePollResponse createPollResponse(CreatePollRequest request) {
        return pollApiSteps.createPoll(request, processId, "-1");
    }

    // --- РАБОЧИЕ ПРОСТРАНСТВА (WORKSPACES) ---
    public void markSpaceRead(Long workspaceId) {
        workSpaceApiSteps.markWorkspaceAsRead(workspaceId);
    }

    public Long createWorkspace(String name) {
        long wsId = workSpaceApiSteps
                .createWorkspace(new CreateWorkSpaceRequest(new CreateWorkSpaceRequest.Workspace(name, "")))
                .getId();
        try {
            workSpaceApiSteps.showActiveWorkspace(wsId);
        } catch (Exception e) {
            logger.warn("showActiveWorkspace({}) после create не удался: {}", wsId, e.getMessage());
        }
        return wsId;
    }

    public WorkspaceInfo createWorkspaceInfo(String name) {
        CreateWorkSpaceResponse response = workSpaceApiSteps
                .createWorkspace(new CreateWorkSpaceRequest(new CreateWorkSpaceRequest.Workspace(name, "")));
        try {
            workSpaceApiSteps.showActiveWorkspace(response.getId());
        } catch (Exception e) {
            logger.warn("showActiveWorkspace({}) после create не удался: {}", response.getId(), e.getMessage());
        }
        return new WorkspaceInfo(response.getId(), response.getInviteCode());
    }

    public void deleteWorkspace(Long workspaceId) {
        workSpaceApiSteps.deleteWorkspace(new DeleteWorkspaceRequest(workspaceId));
    }

    public void addUsersToWorkspace(Long workspaceId, UsersEnum member) {
        workSpaceApiSteps
                .addUserToWorkspace(new AddUserToWorkspaceRequest(List.of(member.getId())), workspaceId);
    }

    public void updateWorkspaceRole(Long workspaceId, UsersEnum member, String role) {
        workSpaceApiSteps.updateWorkspaceRole(new UpdateWorkspaceRoleRequest(member.getId(), role), workspaceId);
    }

    public String getWorkSpaceInviteLink(Long workspaceId) {
        return workSpaceApiSteps.getInviteLink(workspaceId).getInviteLink();
    }

    public void deleteUserFromWorkspace(Long workspaceId, UsersEnum member) {
        workSpaceApiSteps.deleteUser(new DeleteUserRequest(member.getId()), workspaceId);
    }

    public void deactivateAllSpaces() {
        workSpaceApiSteps.deactivateAllSpaces();
    }

    public void updateWorkspaceNotification(Long workspaceId, boolean enable) {
        UpdateNotificationRequest.NotificationType notificationType = enable
                ? UpdateNotificationRequest.NotificationType.ON
                : UpdateNotificationRequest.NotificationType.OFF;
        workSpaceApiSteps.updateNotification(
                new UpdateNotificationRequest(String.valueOf(workspaceId), notificationType));
    }

    // --- ПОЛЬЗОВАТЕЛИ (USERS) & КОНТАКТЫ ---
    public List<Long> addContacts(UsersEnum... usersToAdd) {
        List<Long> contactIds = new ArrayList<>();
        for (UsersEnum user : usersToAdd) {
            long id = contactApiSteps.addContact(new AddContactRequest(user.getId())).getId();
            contactIds.add(id);
        }
        return contactIds;
    }

    public void blockUser(UsersEnum userToBlock) {
        usersApiSteps
                .blockUser(new BlockRequest(userToBlock.getId()));
    }

    public void unBlockUser(UsersEnum userToUnblock) {
        usersApiSteps
                .unBlockUser(new UnBlockRequest(userToUnblock.getId()));
    }

    // --- АККАУНТ (ACCOUNT) ---

    public void updateFullProfile(String firstName, String lastName, String middleName) {
        accountApiSteps.updateFullProfile(new UpdateFullProfileRequest(firstName, lastName, middleName));
    }

    public void showPhone() {
        accountApiSteps.updateUserPrivacyAll();
    }

    public void hidePhone() {
        accountApiSteps.updateUserPrivacyHidePhone();
    }

    // --- ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ (UTILS) ---
    public void saveToContext(String key, Object value) {
        TestContext.put(key, value);
    }

    // --- ВНУТРЕННИЕ КЛАССЫ (CONFIGS & DTOs) ---

    /**
     * Конфигуратор для Групп
     */
    @Getter
    public static class GroupConfig {
        private final String name;
        private final GroupTypesEnum type;
        private Long workspaceId = null;
        private boolean visible = false;
        private Boolean commentsEnabled = true;
        private Boolean reactionsEnabled = null;
        private CreateGroupRequest.Image image = null;
        private String description = null;

        private GroupConfig(String name, GroupTypesEnum type) {
            this.name = name;
            this.type = type;
        }

        public static GroupConfig ofGroup(String name) {
            return new GroupConfig(name, GroupTypesEnum.GROUP);
        }

        public static GroupConfig ofChannel(String name) {
            return new GroupConfig(name, GroupTypesEnum.INFO_CHANNEL);
        }

        public GroupConfig inWorkspace(Long workspaceId) {
            this.workspaceId = workspaceId;
            return this;
        }

        public GroupConfig visible(boolean isVisible) {
            this.visible = isVisible;
            return this;
        }

        public GroupConfig channelOptions(boolean commentsEnabled, boolean reactionsEnabled) {
            this.commentsEnabled = commentsEnabled;
            this.reactionsEnabled = reactionsEnabled;
            return this;
        }

        public GroupConfig commentsEnabled(boolean commentsEnabled) {
            this.commentsEnabled = commentsEnabled;
            return this;
        }

        public GroupConfig reactionsEnabled(boolean reactionsEnabled) {
            this.reactionsEnabled = reactionsEnabled;
            return this;
        }

        public GroupConfig image(CreateGroupRequest.Image image) {
            this.image = image;
            return this;
        }

        public GroupConfig description(String description) {
            this.description = description;
            return this;
        }
    }

    @Getter
    public static class WorkspaceInfo {
        private final Long id;
        private final String inviteCode;

        public WorkspaceInfo(Long id, String inviteCode) {
            this.id = id;
            this.inviteCode = inviteCode;
        }
    }
}