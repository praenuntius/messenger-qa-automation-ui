package org.foxtrot.utils;

import io.restassured.response.Response;
import org.foxtrot.api.dto.account.responce.GetAllWorkspacesResponse;
import org.foxtrot.api.dto.contact.DeleteContactRequest;
import org.foxtrot.api.dto.contact.GetContactsResponse;
import org.foxtrot.api.dto.group.ClearHistoryRequest;
import org.foxtrot.api.dto.group.DeleteGroupRequest;
import org.foxtrot.api.dto.group.HideGroupRequest;
import org.foxtrot.api.dto.group.response.GetUserGroupStatesResponse;
import org.foxtrot.api.dto.workSpace.DeleteWorkspaceRequest;
import org.foxtrot.api.steps.AccountApiSteps;
import org.foxtrot.api.steps.ContactApiSteps;
import org.foxtrot.api.steps.GroupApiSteps;
import org.foxtrot.api.steps.WorkSpaceApiSteps;
import org.foxtrot.enums.UsersEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

public class UserTestDataCleaner {

    private static final Logger logger = LoggerFactory.getLogger(UserTestDataCleaner.class);

    private static final String SAVED_CHAT_NAME = "Сохраненные";
    private static final Set<String> DELETABLE_OWNER_TYPES = Set.of("GROUP", "INFO_CHANNEL");

    public void clean(UsersEnum... users) {
        if (users == null) return;

        for (UsersEnum user : users) {
            if (user == null) continue;

            AccountApiSteps accountApiSteps = new AccountApiSteps(user);
            GroupApiSteps groupApiSteps = new GroupApiSteps(user);
            ContactApiSteps contactApiSteps = new ContactApiSteps(user);
            WorkSpaceApiSteps workSpaceApiSteps = new WorkSpaceApiSteps(user);

            try {
                cleanWorkspaces(accountApiSteps, workSpaceApiSteps);
                cleanGroups(groupApiSteps);
                cleanContacts(contactApiSteps);
            } catch (Exception e) {
                System.err.println("Ошибка при очистке пользователя " + user + ": " + e.getMessage());
                if (isUnusableAccount(e)) {
                    UserPoolManager.quarantine(user);
                }
            }
        }
    }

    public void resetUserProfile(UsersEnum user) {
        try {
            TestDataBuilder builder = new TestDataBuilder(user);
            builder.updateFullProfile(
                    user.getFirstName(),
                    user.getLastName(),
                    ""
            );
            logger.info("Профиль пользователя {} сброшен к исходному состоянию", user);
        } catch (Exception e) {
            logger.warn("Не удалось сбросить профиль пользователя {}: {}", user, e.getMessage());
        }
    }

    private void cleanWorkspaces(AccountApiSteps accountApiSteps, WorkSpaceApiSteps workSpaceApiSteps) {
        GetAllWorkspacesResponse resp = accountApiSteps.getAllWorkspace();

        resp.getAvailableWorkspaces().stream()
                .filter(ws -> "OWNER".equals(ws.getUserRole()))
                .filter(UserTestDataCleaner::isAutotestWorkspace)
                .forEach(ws -> {
                    try {
                        workSpaceApiSteps.deleteWorkspace(new DeleteWorkspaceRequest(ws.getId()));
                    } catch (Exception e) {
                        logger.warn("Не удалось удалить workspace {} (OWNER): {}",
                                ws.getId(), e.getMessage());
                    }
                });
    }

    public static boolean isUnusableAccount(Throwable error) {
        for (Throwable t = error; t != null; t = t.getCause()) {
            String message = t.getMessage();
            if (message != null && message.contains("WORKSPACE_DOES_NOT_EXIST")) {
                return true;
            }
        }
        return false;
    }

    /** Homespace / чужие спейсы не трогаем — иначе следующий signIn даёт WORKSPACE_DOES_NOT_EXIST. */
    public static boolean isAutotestWorkspace(GetAllWorkspacesResponse.Workspace ws) {
        String name = ws.getName();
        if (name == null || name.isBlank()) {
            return false;
        }
        return name.startsWith("Workspace_") || name.startsWith("AutoWS_");
    }

    private void cleanGroups(GroupApiSteps groupApiSteps) {
        List<GetUserGroupStatesResponse> states = groupApiSteps.getUserGroupStates();

        states.forEach(state -> {
            long groupId = state.getGroupId();
            String type = state.getGroup().getType();
            String role = state.getRole();
            String groupName = state.getGroup().getName();
            Long lastMessageId = state.getLastMessageId();

            try {
                if ("P2P".equals(type) && lastMessageId != null && lastMessageId > 0) {
                    groupApiSteps.hideGroup(new HideGroupRequest(groupId));
                } else if (SAVED_CHAT_NAME.equals(groupName)) {
                    groupApiSteps.clearHistory(new ClearHistoryRequest(groupId));
                } else if ("OWNER".equals(role) && isDeletableOwnerGroup(type, groupName)) {
                    deleteOwnedGroup(groupApiSteps, groupId, type);
                }
            } catch (Exception e) {
                logger.warn("Не удалось обработать группу {} (type={}, role={}): {}",
                        groupId, type, role, e.getMessage());
            }
        });
    }

    private boolean isDeletableOwnerGroup(String type, String groupName) {
        if (type == null || SAVED_CHAT_NAME.equals(groupName)) {
            return false;
        }
        return DELETABLE_OWNER_TYPES.contains(type);
    }

    private void deleteOwnedGroup(GroupApiSteps groupApiSteps, long groupId, String type) {
        Response response = groupApiSteps.deleteGroup(new DeleteGroupRequest(groupId));
        if (response == null) {
            logger.warn("deleteGroup {} (type={}): пустой ответ API", groupId, type);
            return;
        }
        if (response.getStatusCode() >= 400) {
            String body = response.getBody().asString();
            if (body.contains("GROUP_TYPE_DONT_ALLOW")) {
                logger.debug("Пропуск удаления группы {} (type={}): API не разрешает deleteGroup",
                        groupId, type);
            } else {
                logger.warn("deleteGroup {} (type={}) вернул {}: {}",
                        groupId, type, response.getStatusCode(), body);
            }
        }
    }

    private void cleanContacts(ContactApiSteps contactApiSteps) {
        GetContactsResponse resp = contactApiSteps.getContacts();

        resp.getContacts().forEach(contact -> {
            try {
                contactApiSteps.deleteContact(new DeleteContactRequest(contact.getId()));
            } catch (Exception e) {
                logger.warn("Не удалось удалить контакт {}: {}", contact.getId(), e.getMessage());
            }
        });
    }
}
