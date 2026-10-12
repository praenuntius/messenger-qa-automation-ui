package org.foxtrot.api.steps;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.foxtrot.api.BaseApiService;
import org.foxtrot.api.dto.users.BlockRequest;
import org.foxtrot.api.dto.users.UnBlockRequest;
import org.foxtrot.enums.UsersEnum;

import static org.foxtrot.api.constants.ApiEndpoints.BLOCK;
import static org.foxtrot.api.constants.ApiEndpoints.UN_BLOCK;

public class UsersApiSteps extends BaseApiService {

    private final UsersEnum user;

    public UsersApiSteps(UsersEnum user) {
        this.user = user;
    }

    private RequestSpecification request() {
        return getBaseRequestSpec(user.getProcessId());
    }

    @Step("Заблокировать пользователя")
    public Response blockUser(BlockRequest body) {
        return request()
                .body(body)
                .post(BLOCK)
                .then()
                .extract().response();
    }

    @Step("Разблокировать пользователя")
    public Response unBlockUser(UnBlockRequest body) {
        return request()
                .body(body)
                .post(UN_BLOCK)
                .then()
                .extract().response();
    }
}
