package org.foxtrot.api.steps;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.foxtrot.api.BaseApiService;
import org.foxtrot.api.dto.workSpace.*;
import org.foxtrot.api.dto.workSpace.response.CreateWorkSpaceResponse;
import org.foxtrot.api.dto.workSpace.response.GetInviteLinkResponse;
import org.foxtrot.enums.UsersEnum;
import org.foxtrot.utils.UserTestDataCleaner;

import static org.foxtrot.api.constants.ApiEndpoints.*;

public class WorkSpaceApiSteps extends BaseApiService {

    private final UsersEnum user;

    public WorkSpaceApiSteps(UsersEnum user) {
        this.user = user;
    }

    private RequestSpecification request(Long workspaceId) {
        return getBaseRequestSpec(user.getProcessId())
                .header("workspaceId", workspaceId != null ? workspaceId : -1);
    }

    private RequestSpecification request() {
        return request(null);
    }

    @Step("Пометить пространство как прочитанное")
    public Response markWorkspaceAsRead(Long workspaceId) {
        return request(workspaceId)
                .post(MARK_WORKSPACE_READ)
                .then().extract().response();
    }

    @Step("Удалить пользователя из пространства")
    public Response deleteUser(DeleteUserRequest body, Long workspaceId) {
        return request(workspaceId)
                .body(body)
                .post(DELETE_USER)
                .then().extract().response();
    }

    @Step("Получить ссылку-приглашение в пространство")
    public GetInviteLinkResponse getInviteLink(Long workspaceId) {
        return request(workspaceId)
                .post(GET_INVITE_LINK)
                .then().extract().as(GetInviteLinkResponse.class);
    }

    @Step("Выйти из пространства {workspaceId}")
    public Response hideActiveWorkspace(Long workspaceId) {
        return request()
                .body(new ActiveWorkspaceRequest(workspaceId))
                .post(HIDE_ACTIVE_WORKSPACE)
                .then().extract().response();
    }

    @Step("Войти в пространство {workspaceId}")
    public Response showActiveWorkspace(Long workspaceId) {
        return request()
                .body(new ActiveWorkspaceRequest(workspaceId))
                .post(SHOW_ACTIVE_WORKSPACE)
                .then().extract().response();
    }

    @Step("Выйти из автотестовых пространств (homespace не трогаем)")
    public void deactivateAllSpaces() {
        new AccountApiSteps(user).getAllWorkspace().getAvailableWorkspaces().stream()
                .filter(UserTestDataCleaner::isAutotestWorkspace)
                .forEach(ws -> hideActiveWorkspace(ws.getId()));
    }

    @Step("Создать пространство")
    public CreateWorkSpaceResponse createWorkspace(CreateWorkSpaceRequest body) {
        return request()
                .body(body)
                .post(CREATE_WORKSPACE)
                .then().extract().as(CreateWorkSpaceResponse.class);
    }

    @Step("Сделать пользователя админом пространства")
    public Response updateWorkspaceRole(UpdateWorkspaceRoleRequest body, Long workspaceId) {
        return request(workspaceId)
                .body(body)
                .post(UPDATE_WORKSPACE_ROLE)
                .then().extract().response();
    }

    @Step("Добавить пользователя в пространство")
    public Response addUserToWorkspace(AddUserToWorkspaceRequest body, Long workspaceId) {
        return request(workspaceId)
                .body(body)
                .post(ADD_USER_TO_WORKSPACE)
                .then().extract().response();
    }

    @Step("Удалить пространство")
    public Response deleteWorkspace(DeleteWorkspaceRequest body) {
        return request()
                .body(body)
                .post(DELETE_WORKSPACE)
                .then().extract().response();
    }

    @Step("Изменить статус уведомлений группы")
    public Response updateNotification(UpdateNotificationRequest body) {
        return request()
                .body(body)
                .post(UPDATE_NOTIFICATION)
                .then().extract().response();
    }
}
