package org.foxtrot.api.steps;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.foxtrot.api.BaseApiService;
import org.foxtrot.api.dto.group.*;
import org.foxtrot.api.dto.group.response.*;
import org.foxtrot.enums.UsersEnum;

import java.util.List;

import static org.foxtrot.api.constants.ApiEndpoints.*;

public class GroupApiSteps extends BaseApiService {

    private final UsersEnum user;

    public GroupApiSteps(UsersEnum user) {
        this.user = user;
    }

    private RequestSpecification request(Long workspaceId) {
        return getBaseRequestSpec(user.getProcessId())
                .header("workspaceId", workspaceId != null ? workspaceId : -1);
    }

    private RequestSpecification request() {
        return request(null);
    }

    public Response updateMessageAutoDeletion(UpdateMessageAutoDeletionRequest body) {
        return request()
                .body(body)
                .post(UPDATE_MESSAGE_AUTO_DELETION)
                .then().extract().response();
    }

    @Step("Создать группу отложенных сообщений")
    public CreateScheduledGroupResponse createScheduledGroup(CreateScheduledGroupRequest body) {
        return request()
                .body(body)
                .post(CREATE_SCHEDULED_GROUP)
                .then().extract().as(CreateScheduledGroupResponse.class);
    }

    @Step("Очистить историю сообщений группы")
    public Response clearHistory(ClearHistoryRequest body) {
        return request()
                .body(body)
                .post(CLEAR_HISTORY)
                .then().extract().response();
    }

    @Step("Получить состояния групп пользователя")
    public List<GetUserGroupStatesResponse> getUserGroupStates() {
        return getUserGroupStates(null);
    }

    @Step("Получить состояния групп пользователя в workspace {0}")
    public List<GetUserGroupStatesResponse> getUserGroupStates(Long workspaceId) {
        return request(workspaceId)
                .post(GET_USER_GROUP_STATES)
                .then()
                .extract()
                .body()
                .jsonPath()
                .getList("", GetUserGroupStatesResponse.class);
    }

    @Step("Создать P2P группу")
    public CreateP2PGroupResponse createP2PGroup(CreateP2PGroupRequest body) {
        return request()
                .body(body)
                .post(CREATE_P2P_GROUP)
                .then().extract().as(CreateP2PGroupResponse.class);
    }

    @Step("Сгенерировать ссылку на приглашение в группу")
    public RegenerateInviteLinkResponse regenerateInviteLink(RegenerateInviteLinkRequest body, Long workspaceId) {
        return request(workspaceId)
                .body(body)
                .post(REGENERATE_INVITE_LINK)
                .then().extract().as(RegenerateInviteLinkResponse.class);
    }

    @Step("Сгенерировать ссылку на приглашение в группу")
    public RegenerateInviteLinkResponse regenerateInviteLink(RegenerateInviteLinkRequest body) {
        return regenerateInviteLink(body, null);
    }

    @Step("Создать группу или канал")
    public CreateGroupResponse createGroup(CreateGroupRequest body, Long workspaceId) {
        Response response = request(workspaceId)
                .body(body)
                .post(CREATE_GROUP)
                .then().extract().response();
        String raw = response.getBody() == null ? "" : response.getBody().asString();
        if (response.statusCode() >= 400) {
            throw new IllegalStateException(
                    "createGroup failed: HTTP " + response.statusCode() + " body=" + raw);
        }
        CreateGroupResponse parsed;
        try {
            parsed = response.as(CreateGroupResponse.class);
        } catch (Exception e) {
            throw new IllegalStateException(
                    "createGroup parse failed: HTTP " + response.statusCode() + " body=" + raw, e);
        }
        if (parsed.getGroupId() == null) {
            parsed.setGroupId(firstLong(response,
                    "groupId", "id", "group.groupId", "group.id", "data.groupId", "data.id"));
        }
        if (parsed.getGroupId() == null) {
            throw new IllegalStateException(
                    "createGroup returned null groupId: HTTP " + response.statusCode() + " body=" + raw);
        }
        return parsed;
    }

    @Step("Создать группу или канал")
    public CreateGroupResponse createGroup(CreateGroupRequest body) {
        return createGroup(body, null);
    }

    @Step("Удалить группу или канал")
    public Response deleteGroup(DeleteGroupRequest body, Long workspaceId) {
        return request(workspaceId)
                .body(body)
                .post(DELETE_GROUP)
                .then().extract().response();
    }

    @Step("Удалить группу или канал")
    public Response deleteGroup(DeleteGroupRequest body) {
        return deleteGroup(body, null);
    }

    @Step("Удалить P2P чат")
    public Response hideGroup(HideGroupRequest body, Long workspaceId) {
        return request(workspaceId)
                .body(body)
                .post(HIDE_GROUP)
                .then().extract().response();
    }

    @Step("Удалить P2P чат")
    public Response hideGroup(HideGroupRequest body) {
        return hideGroup(body, null);
    }

    @Step("Добавить пользователя в группу или канал")
    public Response addUsers(AddUsersRequest body, long workspaceId) {
        return request(workspaceId)
                .body(body)
                .post(ADD_USERS)
                .then().extract().response();
    }

    @Step("Добавить пользователя в группу или канал")
    public Response addUsers(AddUsersRequest body) {
        return addUsers(body, -1L);
    }

    @Step("Удалить пользователя из группы или канала")
    public Response removeUser(RemoveUserRequest body) {
        return request()
                .body(body)
                .post(REMOVE_USER)
                .then().extract().response();
    }

    @Step("Сделать пользователя админом группы или канала")
    public Response editAdmin(EditAdminRequest body, Long workspaceId) {
        return request(workspaceId)
                .body(body)
                .post(EDIT_ADMIN)
                .then().extract().response();
    }

    @Step("Сделать пользователя админом группы или канала")
    public Response editAdmin(EditAdminRequest body) {
        return editAdmin(body, null);
    }

    @Step("Создать группу комментариев к посту в группе")
    public CreateCommentGroupResponse createCommentGroup(CreateCommentGroupRequest body) {
        return request()
                .body(body)
                .post(CREATE_COMMENT_GROUP)
                .then().extract().as(CreateCommentGroupResponse.class);
    }

    @Step("Создать группу комментариев к посту в группе")
    public Response createCommentGroupRaw(CreateCommentGroupRequest body) {
        return request()
                .body(body)
                .post(CREATE_COMMENT_GROUP)
                .then().extract().response();
    }

    @Step("Установить статус уведомлений группы")
    public Response updateGroupNotification(UpdateGroupNotification body, Long workspaceId) {
        return request(workspaceId)
                .body(body)
                .post(UPDATE_GROUP_NOTIFICATION)
                .then().extract().response();
    }

    @Step("Пометить группу непрочитанной")
    public Response updateGroupUnread(UpdateGroupUnreadRequest body, Long workspaceId) {
        return request(workspaceId)
                .body(body)
                .post(UPDATE_GROUP_UNREAD)
                .then().extract().response();
    }

    @Step("Изменить информацию группы")
    public Response updateGroup(UpdateGroupRequest body, Long workspaceId) {
        return request(workspaceId)
                .body(body)
                .post(UPDATE_GROUP)
                .then().extract().response();
    }

    @Step("Пометить группу непрочитанной")
    public Response updateGroupUnread(UpdateGroupUnreadRequest body) {
        return updateGroupUnread(body, null);
    }

    private static Long firstLong(Response response, String... paths) {
        for (String path : paths) {
            try {
                Object value = response.jsonPath().get(path);
                if (value instanceof Number number) {
                    return number.longValue();
                }
                if (value instanceof String text && !text.isBlank()) {
                    return Long.parseLong(text);
                }
            } catch (Exception ignored) {
                // next path
            }
        }
        return null;
    }
}
