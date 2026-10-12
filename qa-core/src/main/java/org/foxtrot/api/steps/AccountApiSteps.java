package org.foxtrot.api.steps;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.foxtrot.api.BaseApiService;
import org.foxtrot.api.dto.account.RemovePhotoHistoryItemRequest;
import org.foxtrot.api.dto.account.UpdateFullProfileRequest;
import org.foxtrot.api.dto.account.UpdateProfilePhotoRequest;
import org.foxtrot.api.dto.account.responce.GetAllWorkspacesResponse;
import org.foxtrot.enums.UsersEnum;

import static org.foxtrot.api.constants.ApiEndpoints.*;

public class AccountApiSteps extends BaseApiService {

    private final UsersEnum user;

    public AccountApiSteps(UsersEnum user) {
        this.user = user;
    }

    private RequestSpecification request() {
        return getBaseRequestSpec(user.getProcessId());
    }

    @Step("Получить список пространств пользователя")
    public GetAllWorkspacesResponse getAllWorkspace() {
        return request()
                .post(GET_ALL_WORKSPACE)
                .then()
                .extract().as(GetAllWorkspacesResponse.class);
    }

    @Step("Показывать мой регистрационный номер телефона")
    public Response updateUserPrivacyAll() {
        String body = "{\"contactDataVisibility\": \"ALL\"}";
        return request()
                .body(body)
                .post(UPDATE_USER_PRIVACY)
                .then()
                .extract().response();
    }

    @Step("Не показывать мой регистрационный номер телефона")
    public Response updateUserPrivacyHidePhone() {
        String body = "{\"contactDataVisibility\": \"HIDE_PHONE\"}";
        return request()
                .body(body)
                .post(UPDATE_USER_PRIVACY)
                .then()
                .extract().response();
    }

    @Step("Загрузить аватарку")
    public Response UpdateProfilePhoto(UpdateProfilePhotoRequest body) {
        return request()
                .body(body)
                .post(UPDATE_PROFILE_PHOTO)
                .then()
                .extract().response();
    }

    @Step("Редактировать профиль")
    public Response updateFullProfile(UpdateFullProfileRequest body) {
        return request()
                .body(body)
                .post(UPDATE_FULL_PROFILE)
                .then()
                .extract().response();
    }

    @Step("Удалить аватарку профиля")
    public Response removePhotoHistoryItem(RemovePhotoHistoryItemRequest body) {
        return request()
                .body(body)
                .post(REMOVE_PHOTO_HISTORY_ITEM)
                .then()
                .extract().response();
    }
}
