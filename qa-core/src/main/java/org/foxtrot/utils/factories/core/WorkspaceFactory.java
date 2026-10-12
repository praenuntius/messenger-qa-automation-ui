package org.foxtrot.utils.factories.core;

import org.foxtrot.enums.UsersEnum;
import org.foxtrot.utils.TestData;
import org.foxtrot.utils.TestDataBuilder;
import org.foxtrot.utils.factories.contexts.WorkspaceContext;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class WorkspaceFactory {
    private final UserFactory userFactory = new UserFactory();

    public WorkspaceContext createWorkspace() {
        String wsName = TestData.generateWorkspaceName();
        UsersEnum owner = userFactory.takeUser();
        TestDataBuilder ownerBuilder = new TestDataBuilder(owner);
        long wsId = ownerBuilder.createWorkspace(wsName);

        return WorkspaceContext.builder()
                .wsName(wsName)
                .owner(owner)
                .wsId(wsId)
                .ownerBuilder(ownerBuilder)
                .build();
    }

    public WorkspaceContext createWorkspaceWithParticipant() {
        WorkspaceContext context = createWorkspace();
        UsersEnum participant = userFactory.takeUser();
        TestDataBuilder participantBuilder = new TestDataBuilder(participant);

        context.getOwnerBuilder().addUsersToWorkspace(context.getWsId(), participant);
        return context.toBuilder()
                .participant(participant)
                .participantBuilder(participantBuilder)
                .build();
    }

    public WorkspaceContext createWorkspaceWithWorkspaceAdmin() {
        WorkspaceContext context = createWorkspace();
        UsersEnum admin = userFactory.takeUser();
        TestDataBuilder adminBuilder = new TestDataBuilder(admin);

        context.getOwnerBuilder().addUsersToWorkspace(context.getWsId(), admin);
        context.getOwnerBuilder().updateWorkspaceRole(context.getWsId(), admin, "ADMIN");
        return context.toBuilder()
                .admin(admin)
                .adminBuilder(adminBuilder)
                .build();
    }

    public WorkspaceContext createWorkspaceWithAdminAndParticipant() {
        WorkspaceContext context = createWorkspaceWithWorkspaceAdmin();
        UsersEnum participant = userFactory.takeUser();
        TestDataBuilder participantBuilder = new TestDataBuilder(participant);
        context.getOwnerBuilder().addUsersToWorkspace(context.getWsId(), participant);
        return context.toBuilder()
                .participant(participant)
                .participantBuilder(participantBuilder)
                .build();
    }

    public List<WorkspaceContext> createMultipleWorkspaces(int count) {
        UsersEnum owner = userFactory.takeUser();
        TestDataBuilder ownerBuilder = new TestDataBuilder(owner);

        String baseName = "AutoWS_" + TestData.generateWorkspaceName().split("_")[0];

        return IntStream.range(1, count + 1)
                .mapToObj(i -> {
                    String wsName = baseName + "_" + i;
                    long wsId = ownerBuilder.createWorkspace(wsName);

                    return WorkspaceContext.builder()
                            .wsName(wsName)
                            .owner(owner)
                            .wsId(wsId)
                            .ownerBuilder(ownerBuilder)
                            .build();
                })
                .collect(Collectors.toList());
    }

    public void cleanUp() {
        userFactory.releaseAll();
    }
}