package org.foxtrot.api.steps;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.foxtrot.api.BaseApiService;
import org.foxtrot.enums.UsersEnum;

import static org.foxtrot.api.constants.ApiEndpoints.NON_CONTACT_PREVIEW;

public class AccountSettingsApiSteps extends BaseApiService {

    private final UsersEnum user;

    public AccountSettingsApiSteps(UsersEnum user) {
        this.user = user;
    }

    private RequestSpecification request() {
        return getBaseRequestSpec(user.getProcessId());
    }

    @Step("Отображать номера телефонов пользователей не из контактов")
    public Response setNonContactPreviewAttention() {
        String body = "{\"nonContactPreviewType\": \"NON_CONTACT_ATTENTION\"}";
        return request()
                .body(body)
                .post(NON_CONTACT_PREVIEW)
                .then()
                .extract().response();
    }

    @Step("Не отображать номера телефонов пользователей не из контактов")
    public Response setNonContactPreviewLikeContact() {
        String body = "{\"nonContactPreviewType\": \"LIKE_CONTACT\"}";
        return request()
                .body(body)
                .post(NON_CONTACT_PREVIEW)
                .then()
                .extract().response();
    }
}
