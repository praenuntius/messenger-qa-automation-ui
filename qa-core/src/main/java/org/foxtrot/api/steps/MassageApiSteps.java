package org.foxtrot.api.steps;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.foxtrot.api.BaseApiService;
import org.foxtrot.api.dto.massage.SendLinkMassageRequest;
import org.foxtrot.api.dto.massage.SendTextMassageRequest;
import org.foxtrot.api.dto.massage.response.SendTextMassageResponse;
import org.foxtrot.api.dto.message.MultiforwardMessagesRequest;
import org.foxtrot.api.dto.message.TypingRequest;
import org.foxtrot.enums.UsersEnum;

import static org.foxtrot.api.constants.ApiEndpoints.*;

public class MassageApiSteps extends BaseApiService {

    private final UsersEnum user;

    public MassageApiSteps(UsersEnum user) {
        this.user = user;
    }

    private RequestSpecification request() {
        return getBaseRequestSpec(user.getProcessId())
                .header("workspaceId", -1);
    }

    @Step("Отправить ссылку")
    public SendTextMassageResponse sendLinkMassage(SendLinkMassageRequest body) {
        return request()
                .body(body)
                .post(SEND_MASSAGE)
                .then().extract().as(SendTextMassageResponse.class);
    }

    @Step("Отправить текст")
    public SendTextMassageResponse sendTextMassage(SendTextMassageRequest body) {
        return request()
                .body(body)
                .post(SEND_MASSAGE)
                .then().extract().as(SendTextMassageResponse.class);
    }

    @Step("Пользователь начинает ввод сообщения в чат")
    public Response typing(TypingRequest body) {
        return request()
                .body(body)
                .post(TYPING)
                .then().extract().response();
    }

    @Step("Переслать сообщение")
    public Response multiforwardMessages(MultiforwardMessagesRequest body) {
        return request()
                .body(body)
                .post(MULTIFORWARD_MESSAGES)
                .then().extract().response();
    }
}
