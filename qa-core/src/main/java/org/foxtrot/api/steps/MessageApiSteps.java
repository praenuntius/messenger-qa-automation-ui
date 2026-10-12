package org.foxtrot.api.steps;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.foxtrot.api.BaseApiService;
import org.foxtrot.api.dto.message.*;
import org.foxtrot.api.dto.message.response.SendTextMessageResponse;
import org.foxtrot.enums.UsersEnum;

import static org.foxtrot.api.constants.ApiEndpoints.*;

public class MessageApiSteps extends BaseApiService {

    private final UsersEnum user;

    public MessageApiSteps(UsersEnum user) {
        this.user = user;
    }

    private RequestSpecification request(Object workspaceId) {
        return getBaseRequestSpec(user.getProcessId())
                .header("workspaceId", workspaceId != null ? workspaceId : -1);
    }

    private RequestSpecification request() {
        return request(null);
    }

    @Step("Отправить текстовое сообщение через API")
    public SendTextMessageResponse sendTextMessage(SendTextMessageRequest body, String workspaceId) {
        return request(workspaceId)
                .body(body)
                .post(SEND_TEXT_MESSAGE)
                .then().extract().as(SendTextMessageResponse.class);
    }

    @Step("Отправить текстовое сообщение через API")
    public SendTextMessageResponse sendTextMessage(SendTextMessageRequest body) {
        return sendTextMessage(body, null);
    }

    @Step("Отформатировать текстовое сообщение через API")
    public Response formatTextMessage(FormatMessageRequest body, String workspaceId) {
        return request(workspaceId)
                .body(body)
                .post(UPDATE_TEXT_MESSAGE)
                .then().extract().response();
    }

    @Step("Отформатировать текстовое сообщение через API")
    public Response formatTextMessage(FormatMessageRequest body) {
        return formatTextMessage(body, null);
    }

    @Step("Отправить текстовое сообщение через API")
    public Response sendTextMessageRaw(SendTextMessageRequest body, String workspaceId) {
        return request(workspaceId)
                .body(body)
                .post(SEND_TEXT_MESSAGE)
                .then().extract().response();
    }

    @Step("Отправить карточку контакта через API")
    public Response sendContact(SendContactRequest body, String workspaceId) {
        return request(workspaceId)
                .body(body)
                .post(SEND_CONTACT)
                .then().extract().response();
    }

    @Step("Отправить карточку контакта через API")
    public SendTextMessageResponse sendContactMessage(SendContactRequest body, String workspaceId) {
        return request(workspaceId)
                .body(body)
                .post(SEND_CONTACT)
                .then().extract().as(SendTextMessageResponse.class);
    }

    @Step("Закрепить сообщение через API")
    public Response pinMessage(PinMessageRequest body, String workspaceId) {
        return request(workspaceId)
                .body(body)
                .post(PIN_MESSAGE)
                .then().extract().response();
    }

    @Step("Закрепить сообщение через API")
    public Response pinMessage(PinMessageRequest body) {
        return pinMessage(body, null);
    }

    @Step("Просмотреть сообщение '{messageId}' в чате '{chatId}' через API")
    public Response confirmMessage(Long messageId, Long chatId) {
        ConfirmMessageRequest body = new ConfirmMessageRequest(messageId, chatId);
        return request()
                .body(body)
                .post(CONFIRM_MESSAGE)
                .then().extract().response();
    }

    @Step("Добавить реакцию '{reaction}' на сообщение '{messageId}' в группе '{groupId}' через API")
    public Response addReaction(long messageId, long groupId, String reaction, String workspaceId) {
        AddReactionRequest body = new AddReactionRequest(groupId, messageId, reaction);
        return request(workspaceId)
                .body(body)
                .post(ADD_REACTION)
                .then().extract().response();
    }

    @Step("Отправить image через API")
    public SendTextMessageResponse sendImage(SendImageRequest body, String workspaceId) {
        return request(workspaceId)
                .body(body)
                .post(SEND_IMAGE)
                .then().extract().as(SendTextMessageResponse.class);
    }

    @Step("Отправить video через API")
    public SendTextMessageResponse sendVideo(SendVideoRequest body, String workspaceId) {
        return request(workspaceId)
                .body(body)
                .post(SEND_VIDEO)
                .then().extract().as(SendTextMessageResponse.class);
    }

    @Step("Отправить audio через API")
    public SendTextMessageResponse sendAudio(SendAudioRequest body, String workspaceId) {
        return request(workspaceId)
                .body(body)
                .post(SEND_AUDIO)
                .then().extract().as(SendTextMessageResponse.class);
    }

    @Step("Отправить file через API")
    public SendTextMessageResponse sendFile(SendFileRequest body, String workspaceId) {
        return request(workspaceId)
                .body(body)
                .post(SEND_FILE)
                .then().extract().as(SendTextMessageResponse.class);
    }
}
