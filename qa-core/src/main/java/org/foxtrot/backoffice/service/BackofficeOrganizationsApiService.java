package org.foxtrot.backoffice.service;

import io.restassured.response.Response;
import org.foxtrot.backoffice.BaseBackofficeApiService;
import org.foxtrot.backoffice.dto.CreateOrganizationRequest;
import org.foxtrot.backoffice.dto.CreateOrganizationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.http.HttpStatus.SC_OK;
import static org.foxtrot.backoffice.constants.BackofficeEndpoints.ORGANIZATIONS;

public class BackofficeOrganizationsApiService extends BaseBackofficeApiService {

    private static final Logger logger = LoggerFactory.getLogger(BackofficeOrganizationsApiService.class);

    public long createOrganization(CreateOrganizationRequest request) {
        Response response = getBackofficeRequestSpec()
                .body(request)
                .post(ORGANIZATIONS)
                .then()
                .extract()
                .response();

        if (response.statusCode() != SC_OK) {
            throw new RuntimeException(
                    "Backoffice create organization failed: status=" + response.statusCode()
                            + ", body=" + response.getBody().asString()
            );
        }

        long id = parseOrganizationId(response);
        logger.info("Организация создана: id={}, name={}", id, request.getName());
        return id;
    }

    private static long parseOrganizationId(Response response) {
        String body = response.getBody().asString().trim();
        if (body.matches("\\d+")) {
            return Long.parseLong(body);
        }

        CreateOrganizationResponse dto = response.getBody().as(CreateOrganizationResponse.class);
        if (dto != null && dto.getId() != null) {
            return dto.getId();
        }

        Long id = response.jsonPath().getLong("id");
        if (id == null) {
            throw new IllegalStateException("Create organization response missing id, body=" + body);
        }
        return id;
    }
}
