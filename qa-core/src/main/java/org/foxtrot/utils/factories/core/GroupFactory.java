package org.foxtrot.utils.factories.core;

import org.foxtrot.enums.UsersEnum;
import org.foxtrot.utils.TestData;
import org.foxtrot.utils.TestDataBuilder;
import org.foxtrot.utils.factories.contexts.GroupContext;
import org.foxtrot.utils.factories.contexts.WorkspaceContext;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GroupFactory {
    private final UserFactory userFactory = new UserFactory();
    private final WorkspaceFactory workspaceFactory = new WorkspaceFactory();
    private final MessageFactory messageFactory = new MessageFactory();

    // --- 1. HOMESPACE ---
    public GroupContext privateGroup() {
        return buildInHs(false, true, true, null);
    }

    public GroupContext privateGroup(String description) {
        return buildInHs(false, true, true, description);
    }

    public GroupContext privateChannel() {
        return privateChannel(true);
    }

    public GroupContext privateChannel(String description) {
        return buildInHs(false, false, true, description);
    }

    public GroupContext privateChannel(boolean commentsEnabled) {
        return buildInHs(false, false, commentsEnabled, null);
    }

    public GroupContext publicChannel() {
        return buildInHs(true, false, true, null);
    }

    // С партисипантом
    public GroupContext privateGroupWithParticipant() {
        return withParticipant(privateGroup());
    }

    public GroupContext privateGroupWithAdmin() {
        return withChatAdmin(privateGroup());
    }

    /** Homespace-группа: owner, admin (в контактах и в группе), participant. */
    public GroupContext privateGroupWithAdminAndParticipant() {
        GroupContext group = withParticipant(withChatAdmin(privateGroup()));
        group.getOwnerBuilder().addContacts(group.getAdmin(), group.getParticipant());
        return group;
    }

    public GroupContext privateChannelWithParticipant() {
        return withParticipant(privateChannel());
    }

    public GroupContext privateChannelWithAdmin() {
        return withChatAdmin(privateChannel());
    }

    public GroupContext publicChannelWithParticipant() {
        return withParticipant(publicChannel());
    }


    // --- 2. Workspace ---
    public GroupContext privateGroupInWs() {
        return buildInWs(false, true, true);
    }

    public GroupContext publicGroupInWs() {
        return buildInWs(true, true, true);
    }

    public GroupContext privateChannelInWs() {
        return privateChannelInWs(true);
    }

    public GroupContext privateChannelInWs(boolean commentsEnabled) {
        return buildInWs(false, false, commentsEnabled);
    }

    public GroupContext publicChannelInWs() {
        return buildInWs(true, false, true);
    }

    // С партисипантом
    public GroupContext privateGroupInWsWithParticipant() {
        return buildInWsWithParticipant(false, true);
    }

    public GroupContext publicGroupInWsWithParticipant() {
        return buildInWsWithParticipant(true, true);
    }

    public GroupContext privateChannelInWsWithParticipant() {
        return buildInWsWithParticipant(false, false);
    }

    public GroupContext publicChannelInWsWithParticipant() {
        return buildInWsWithParticipant(true, false);
    }

    public GroupContext p2pChat() {
        var owner = userFactory.takeUser();
        var participant = userFactory.takeUser();

        var ownerBuilder = new TestDataBuilder(owner);
        long groupId = ownerBuilder.createP2PGroup(participant);

        return GroupContext.builder()
                .owner(owner)
                .participant(participant)
                .groupId(groupId)
                .ownerBuilder(ownerBuilder)
                .participantBuilder(new TestDataBuilder(participant))
                .build();
    }

    public GroupContext p2pChatWithMessage() {
        var ctx = p2pChat();
        return messageFactory.sendMessage(ctx);
    }

    // --- 3. ВНУТРЕННЯЯ ЛОГИКА (DRY) ---
    public GroupContext withParticipant(GroupContext ctx) {
        var participant = userFactory.takeUser();
        var builder = ctx.getOwnerBuilder();
        TestDataBuilder participantBuilder = new TestDataBuilder(participant);

        if (ctx.getWorkspaceId() != null) {
            builder.addUsersToWorkspace(ctx.getWorkspaceId(), participant);
            builder.addUsers(ctx.getGroupId(), ctx.getWorkspaceId(), participant);
        } else {
            builder.addUsers(ctx.getGroupId(), participant);
        }

        return ctx.toBuilder()
                .participant(participant)
                .participantBuilder(participantBuilder)
                .build();
    }

    /** Участник + админ чата (канал/группа). Роль воркспейса не меняется. */
    public GroupContext withChatAdmin(GroupContext ctx) {
        var admin = userFactory.takeUser();
        TestDataBuilder adminBuilder = new TestDataBuilder(admin);
        if (ctx.getWorkspaceId() != null) {
            ctx.getOwnerBuilder().addUsersToWorkspace(ctx.getWorkspaceId(), admin);
            ctx.getOwnerBuilder().addUsers(ctx.getGroupId(), ctx.getWorkspaceId(), admin);
            ctx.getOwnerBuilder().editAdmin(ctx.getGroupId(), ctx.getWorkspaceId(), admin, true);
        } else {
            ctx.getOwnerBuilder().addUsers(ctx.getGroupId(), admin);
            ctx.getOwnerBuilder().editAdmin(ctx.getGroupId(), admin, true);
        }

        return ctx.toBuilder()
                .admin(admin)
                .adminBuilder(adminBuilder)
                .build();
    }

    /** Админ воркспейса (роль ADMIN). В сам канал/группу НЕ добавляется. */
    public GroupContext withWorkspaceAdmin(GroupContext ctx) {
        if (ctx.getWorkspaceId() == null) {
            throw new IllegalStateException("withWorkspaceAdmin требует контекст с воркспейсом");
        }
        var admin = userFactory.takeUser();
        ctx.getOwnerBuilder().addUsersToWorkspace(ctx.getWorkspaceId(), admin);
        ctx.getOwnerBuilder().updateWorkspaceRole(ctx.getWorkspaceId(), admin, "ADMIN");

        return ctx.toBuilder()
                .admin(admin)
                .adminBuilder(new TestDataBuilder(admin))
                .build();
    }

    /** Админ воркспейса И админ чата в одном лице. */
    public GroupContext withWorkspaceAndChatAdmin(GroupContext ctx) {
        if (ctx.getWorkspaceId() == null) {
            throw new IllegalStateException("withWorkspaceAndChatAdmin требует контекст с воркспейсом");
        }
        var admin = userFactory.takeUser();
        ctx.getOwnerBuilder().addUsersToWorkspace(ctx.getWorkspaceId(), admin);
        ctx.getOwnerBuilder().updateWorkspaceRole(ctx.getWorkspaceId(), admin, "ADMIN");
        ctx.getOwnerBuilder().addUsers(ctx.getGroupId(), ctx.getWorkspaceId(), admin);
        ctx.getOwnerBuilder().editAdmin(ctx.getGroupId(), ctx.getWorkspaceId(), admin, true);

        return ctx.toBuilder()
                .admin(admin)
                .adminBuilder(new TestDataBuilder(admin))
                .build();
    }

    /** Берёт пользователя, но НЕ добавляет его в группу/канал (неучастник). */
    public GroupContext withNonParticipant(GroupContext ctx) {
        var nonParticipant = userFactory.takeUser();
        return ctx.toBuilder()
                .nonParticipant(nonParticipant)
                .nonParticipantBuilder(new TestDataBuilder(nonParticipant))
                .build();
    }

    /** Участник пространства, в канал/группу не добавляется. */
    public GroupContext withWorkspaceMember(GroupContext ctx) {
        if (ctx.getWorkspaceId() == null) {
            throw new IllegalStateException("withWorkspaceMember требует контекст с воркспейсом");
        }
        var participant = userFactory.takeUser();
        ctx.getOwnerBuilder().addUsersToWorkspace(ctx.getWorkspaceId(), participant);
        return ctx.toBuilder()
                .participant(participant)
                .participantBuilder(new TestDataBuilder(participant))
                .build();
    }

    private GroupContext buildInHs(boolean visible, boolean isGroup, boolean commentsEnabled, String description) {
        var owner = userFactory.takeUser();
        var builder = new TestDataBuilder(owner);
        return executeCreate(builder, owner, null, visible, isGroup, commentsEnabled, description);
    }

    private GroupContext buildInWs(boolean visible, boolean isGroup, boolean commentsEnabled) {
        var WorkspaceCtx = workspaceFactory.createWorkspace();
        return executeCreate(WorkspaceCtx.getOwnerBuilder(), WorkspaceCtx.getOwner(), WorkspaceCtx, visible, isGroup,
                commentsEnabled, null);
    }

    private GroupContext buildInWsWithParticipant(boolean visible, boolean isGroup) {
        var wsCtx = workspaceFactory.createWorkspaceWithParticipant();
        var groupCtx = executeCreate(wsCtx.getOwnerBuilder(), wsCtx.getOwner(), wsCtx, visible, isGroup, true, null);
        wsCtx.getOwnerBuilder().addUsers(groupCtx.getGroupId(), wsCtx.getParticipant());

        return groupCtx.toBuilder()
                .participant(wsCtx.getParticipant())
                .participantBuilder(wsCtx.getParticipantBuilder())
                .build();
    }

    public List<GroupContext> createMultipleGroups(int count, boolean inWorkspace, boolean visible, boolean isGroup) {
        WorkspaceContext workspace = inWorkspace ? workspaceFactory.createWorkspace() : null;

        UsersEnum owner;
        TestDataBuilder ownerBuilder;
        if (workspace != null) {
            owner = workspace.getOwner();
            ownerBuilder = workspace.getOwnerBuilder();
        } else {
            owner = userFactory.takeUser();
            ownerBuilder = new TestDataBuilder(owner);
        }

        String baseName = (isGroup ? "AutoGroup_" : "AutoChannel_") + TestData.generateAnyEntityName().split("_")[0];

        return IntStream.range(1, count + 1)
                .mapToObj(i -> {
                    String groupName = baseName + "_" + i;

                    var config = isGroup
                            ? TestDataBuilder.GroupConfig.ofGroup(groupName)
                            : TestDataBuilder.GroupConfig.ofChannel(groupName);

                    config.visible(visible);
                    if (workspace != null) {
                        config.inWorkspace(workspace.getWsId());
                    }

                    long groupId = ownerBuilder.createGroup(config);

                    var ctxBuilder = GroupContext.builder()
                            .owner(owner)
                            .groupId(groupId)
                            .groupName(groupName)
                            .ownerBuilder(ownerBuilder);

                    if (workspace != null) {
                        ctxBuilder.workspaceId(workspace.getWsId())
                                .workspaceName(workspace.getWsName());
                    }

                    return ctxBuilder.build();
                })
                .collect(Collectors.toList());
    }

    public List<GroupContext> createMultiplePrivateGroups(int count, boolean inWorkspace) {
        return createMultipleGroups(count, inWorkspace, false, true);
    }

    public List<GroupContext> createMultiplePublicChannels(int count, boolean inWorkspace) {
        return createMultipleGroups(count, inWorkspace, true, false);
    }

    public List<GroupContext> createMultipleGroupsWithParticipants(int count, boolean inWorkspace, boolean visible, boolean isGroup) {
        var groups = createMultipleGroups(count, inWorkspace, visible, isGroup);
        if (groups.isEmpty()) return List.of();

        var participant = userFactory.takeUser();
        var pBuilder = new TestDataBuilder(participant);

        Long workspaceId = groups.get(0).getWorkspaceId();
        if (workspaceId != null) {
            groups.get(0).getOwnerBuilder().addUsersToWorkspace(workspaceId, participant);
        }

        return groups.stream()
                .peek(g -> {
                    if (g.getWorkspaceId() != null) {
                        g.getOwnerBuilder().addUsers(g.getGroupId(), g.getWorkspaceId(), participant);
                    } else {
                        g.getOwnerBuilder().addUsers(g.getGroupId(), participant);
                    }
                })
                .map(g -> g.toBuilder().participant(participant).participantBuilder(pBuilder).build())
                .collect(Collectors.toList());
    }

    /**
     * Ядро создания: тут принимаются все решения по конфигу API.
     */
    private GroupContext executeCreate(TestDataBuilder builder, UsersEnum owner, WorkspaceContext workspace,
            boolean visible, boolean isGroup, boolean commentsEnabled, String description) {
        var groupName = TestData.generateAnyEntityName();

        var config = isGroup
                ? TestDataBuilder.GroupConfig.ofGroup(groupName)
                : TestDataBuilder.GroupConfig.ofChannel(groupName);

        config.visible(visible);
        if (description != null) {
            config.description(description);
        }
        if (!isGroup) {
            config.channelOptions(commentsEnabled, true);
        }
        if (workspace != null) {
            config.inWorkspace(workspace.getWsId());
        }

        long groupId
                = builder.createGroup(config);

        var ctxBuilder = GroupContext.builder()
                .owner(owner)
                .groupId(groupId)
                .groupName(groupName)
                .groupDescription(description)
                .ownerBuilder(builder);

        if (workspace != null) {
            ctxBuilder.workspaceId(workspace.getWsId())
                    .workspaceName(workspace.getWsName());
        }

        return ctxBuilder.build();
    }

    public void cleanUp() {
        userFactory.releaseAll();
    }
}