package org.foxtrot.utils.factories.core;

import org.foxtrot.api.dto.poll.CreatePollRequest;
import org.foxtrot.api.dto.poll.CreatePollRequest.Option;
import org.foxtrot.api.dto.poll.CreatePollRequest.Poll;
import org.foxtrot.api.dto.poll.CreatePollRequest.Settings;
import org.foxtrot.api.dto.poll.response.CreatePollResponse;
import org.foxtrot.enums.UsersEnum;
import org.foxtrot.utils.TestData;
import org.foxtrot.utils.TestDataBuilder;
import org.foxtrot.utils.factories.contexts.GroupContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Готовые чаты с опросами через API — по аналогии с {@link GroupFactory} и {@link WorkspaceFactory}.
 * <p>
 * {@link GroupFactory} создаёт группу/канал (в homespace или через {@link WorkspaceFactory}),
 * {@code PollFactory} добавляет опрос. Голосование и прочие точечные действия — в тесте через {@link TestDataBuilder}.
 */
public class PollFactory {

    private final GroupFactory groupFactory = new GroupFactory();
    private final UserFactory userFactory = new UserFactory();

    public void cleanUp() {
        groupFactory.cleanUp();
    }

    // --- Homespace + participant + poll ---

    public GroupContext privateGroupWithParticipantAndPoll(PollConfig config) {
        return withPoll(groupFactory.privateGroupWithParticipant(), config);
    }

    public GroupContext privateChannelWithParticipantAndPoll(PollConfig config) {
        return withPoll(groupFactory.privateChannelWithParticipant(), config);
    }

    /**
     * Homespace: группа и канал у одного owner (как в legacy {@code prepareTestData} на main),
     * общий participant, опрос в каждом чате.
     */
    public GroupAndChannelPollContexts homespaceGroupAndChannelWithParticipantAndPolls(
            PollConfig groupConfig, PollConfig channelConfig) {
        GroupContext group = groupFactory.privateGroupWithParticipant();
        GroupContext channel = privateChannelWithParticipantForOwner(group);
        return new GroupAndChannelPollContexts(
                withPoll(group, groupConfig),
                withPoll(channel, channelConfig));
    }

    // --- Homespace: роли (admin, guest) + poll ---

    public GroupContext homespaceGroupWithAdminAndGuestAndPoll(PollConfig config) {
        return withPoll(prepareHomespaceGroupWithAdminAndGuest(), config);
    }

    /** Опрос от «гостя» ({@link GroupContext#getParticipant()} после подготовки группы). */
    public GroupContext homespaceGroupWithAdminAndGuestAndPollByGuest(PollConfig config) {
        GroupContext group = prepareHomespaceGroupWithAdminAndGuest();
        return withPoll(group, config, group.getParticipant());
    }

    public GroupContext homespaceChannelWithAdminAndPoll(PollConfig config) {
        return withPoll(prepareHomespaceChannelWithAdmin(), config);
    }

    // --- Workspace: гость в пространстве, не в чате + poll ---

    public GroupContext publicGroupInWsWithPollForWorkspaceGuest(PollConfig config) {
        return withPollForWorkspaceGuest(groupFactory.publicGroupInWs(), config);
    }

    public GroupContext publicChannelInWsWithPollForWorkspaceGuest(PollConfig config) {
        return withPollForWorkspaceGuest(groupFactory.publicChannelInWs(), config);
    }

    // --- Чат без опроса (UI-создание опроса в тесте) ---

    public GroupContext privateGroupWithParticipant() {
        return groupFactory.privateGroupWithParticipant();
    }

    public GroupContext privateChannelWithParticipant() {
        return groupFactory.privateChannelWithParticipant();
    }

    // --- Низкий уровень: опрос поверх готового контекста ---

    public GroupContext withPoll(GroupContext group, PollConfig config) {
        return withPoll(group, config, group.getOwner());
    }

    public GroupContext withPoll(GroupContext group, PollConfig config, UsersEnum author) {
        CreatePollResponse response = new TestDataBuilder(author)
                .createPollResponse(buildRequest(group.getGroupId(), config), workspaceHeader(group));
        return enrichWithPoll(group, response);
    }

    private GroupContext withPollForWorkspaceGuest(GroupContext chat, PollConfig config) {
        UsersEnum workspaceGuest = userFactory.takeUser();
        chat.getOwnerBuilder().addUsersToWorkspace(chat.getWorkspaceId(), workspaceGuest);
        return withPoll(chat, config).toBuilder()
                .participant(workspaceGuest)
                .participantBuilder(new TestDataBuilder(workspaceGuest))
                .build();
    }

    private GroupContext privateChannelWithParticipantForOwner(GroupContext group) {
        String channelName = TestData.generateAnyEntityName();
        TestDataBuilder ownerBuilder = group.getOwnerBuilder();
        long channelId = ownerBuilder.createGroup(
                TestDataBuilder.GroupConfig.ofChannel(channelName).visible(false));
        ownerBuilder.addUsers(channelId, group.getParticipant());
        return GroupContext.builder()
                .owner(group.getOwner())
                .participant(group.getParticipant())
                .groupId(channelId)
                .groupName(channelName)
                .ownerBuilder(ownerBuilder)
                .participantBuilder(group.getParticipantBuilder())
                .build();
    }

    private GroupContext prepareHomespaceGroupWithAdminAndGuest() {
        GroupContext group = groupFactory.privateGroupWithParticipant();
        UsersEnum groupAdmin = userFactory.takeUser();
        UsersEnum guest = userFactory.takeUser();
        TestDataBuilder builder = group.getOwnerBuilder();
        builder.addUsers(group.getGroupId(), groupAdmin);
        builder.editAdmin(group.getGroupId(), groupAdmin, true);
        builder.addUsers(group.getGroupId(), guest);
        return group.toBuilder().admin(groupAdmin).participant(guest).build();
    }

    private GroupContext prepareHomespaceChannelWithAdmin() {
        GroupContext channel = groupFactory.privateChannelWithParticipant();
        UsersEnum groupAdmin = userFactory.takeUser();
        TestDataBuilder builder = channel.getOwnerBuilder();
        builder.addUsers(channel.getGroupId(), groupAdmin);
        builder.editAdmin(channel.getGroupId(), groupAdmin, true);
        return channel.toBuilder().admin(groupAdmin).build();
    }

    private static GroupContext enrichWithPoll(GroupContext group, CreatePollResponse response) {
        GroupContext.GroupContextBuilder builder = group.toBuilder().messageId(response.getMessageId());
        if (response.getPoll() != null) {
            builder.pollId(response.getPoll().getId());
            if (response.getPoll().getOptions() != null && !response.getPoll().getOptions().isEmpty()) {
                builder.pollFirstOptionId(response.getPoll().getOptions().get(0).getId());
            }
        }
        return builder.build();
    }

    private static String workspaceHeader(GroupContext group) {
        return group.getWorkspaceId() != null
                ? String.valueOf(group.getWorkspaceId())
                : "-1";
    }

    private CreatePollRequest buildRequest(long groupId, PollConfig config) {
        CreatePollRequest request = new CreatePollRequest();
        request.setClientRandomId(config.clientRandomId());
        request.setGroupId(groupId);
        request.setMessage("HEX");
        request.setPeerId(0L);

        Poll poll = new Poll();
        poll.setTitle(config.title());
        poll.setCountDownMillis(config.countDownMillis());

        List<Option> options = new ArrayList<>();
        for (String optionTitle : config.options()) {
            Option option = new Option();
            option.setTitle(optionTitle);
            options.add(option);
        }
        poll.setOptions(options);

        Settings settings = new Settings();
        settings.setAnonymous(false);
        settings.setMultiChoice(config.multiChoice());
        poll.setSettings(settings);
        request.setPoll(poll);
        return request;
    }

    public record GroupAndChannelPollContexts(GroupContext group, GroupContext channel) {
    }

    public record PollConfig(
            long clientRandomId,
            String title,
            List<String> options,
            long countDownMillis,
            boolean multiChoice) {

        public static PollConfig of(long clientRandomId, String title, String option1, String option2,
                                    long countDownMillis, boolean multiChoice) {
            return new PollConfig(clientRandomId, title, List.of(option1, option2), countDownMillis, multiChoice);
        }

        public static PollConfig withOptions(long clientRandomId, String title, List<String> options,
                                           long countDownMillis, boolean multiChoice) {
            return new PollConfig(clientRandomId, title, List.copyOf(options), countDownMillis, multiChoice);
        }
    }
}
