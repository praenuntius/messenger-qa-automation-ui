package org.foxtrot.api.steps;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.foxtrot.api.BaseApiService;
import org.foxtrot.api.dto.contact.AddContactRequest;
import org.foxtrot.api.dto.contact.DeleteContactRequest;
import org.foxtrot.api.dto.contact.GetContactsResponse;
import org.foxtrot.api.dto.contact.response.AddContactResponse;
import org.foxtrot.enums.UsersEnum;

import static org.foxtrot.api.constants.ApiEndpoints.ADD_CONTACT;
import static org.foxtrot.api.constants.ApiEndpoints.DELETE_CONTACT;
import static org.foxtrot.api.constants.ApiEndpoints.GET_CONTACT;

public class ContactApiSteps extends BaseApiService {

    private final UsersEnum user;

    public ContactApiSteps(UsersEnum user) {
        this.user = user;
    }

    private RequestSpecification request() {
        return getBaseRequestSpec(user.getProcessId());
    }

    @Step("Получить список контактов пользователя")
    public GetContactsResponse getContacts() {
        return request()
                .post(GET_CONTACT)
                .then()
                .extract().as(GetContactsResponse.class);
    }

    @Step("Добавить пользователя в контакты")
    public AddContactResponse addContact(AddContactRequest body) {
        return request()
                .body(body)
                .post(ADD_CONTACT)
                .then()
                .extract().as(AddContactResponse.class);
    }

    @Step("Удалить контакт")
    public Response deleteContact(DeleteContactRequest body) {
        return request()
                .body(body)
                .post(DELETE_CONTACT)
                .then()
                .extract().response();
    }
}
