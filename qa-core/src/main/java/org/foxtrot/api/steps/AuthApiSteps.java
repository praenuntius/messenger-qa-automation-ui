package org.foxtrot.api.steps;

import io.qameta.allure.Step;
import org.foxtrot.api.dto.auth.SignInRequest;
import org.foxtrot.api.dto.auth.response.SignInResponse;
import org.foxtrot.api.service.AuthApiService;

public class AuthApiSteps {

    AuthApiService apiService = new AuthApiService();

    @Step("Авторизоваться")
    public SignInResponse Authorization(String processId) {
        return apiService.signIn(new SignInRequest(processId, "deviceInfo"))
                .thenReturn().then().extract().as(SignInResponse.class);
    }
}


