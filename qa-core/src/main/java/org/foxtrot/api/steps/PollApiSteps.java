package org.foxtrot.api.steps;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.foxtrot.api.BaseApiService;
import org.foxtrot.api.dto.poll.CreatePollRequest;
import org.foxtrot.api.dto.poll.VotePollRequest;
import org.foxtrot.api.dto.poll.response.CreatePollResponse;

import static org.foxtrot.api.constants.ApiEndpoints.CREATE_POLL;
import static org.foxtrot.api.constants.ApiEndpoints.VOTE_POLL;

public class PollApiSteps extends BaseApiService {

    private RequestSpecification request(String user, String workspaceId) {
        return getBaseRequestSpec(user)
                .header("workspaceId", workspaceId != null ? workspaceId : -1);
    }

    @Step("Создать опрос через API")
    public CreatePollResponse createPoll(CreatePollRequest body, String user, String workspaceId) {
        return request(user, workspaceId)
                .body(body)
                .post(CREATE_POLL)
                .then()
                .extract()
                .as(CreatePollResponse.class);
    }

    @Step("Проголосовать в опросе через API")
    public Response vote(VotePollRequest body, String user, String workspaceId) {
        return request(user, workspaceId)
                .body(body)
                .post(VOTE_POLL)
                .then()
                .extract()
                .response();
    }
}
