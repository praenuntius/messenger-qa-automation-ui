package org.foxtrot.backoffice.service;

import io.restassured.response.Response;
import org.foxtrot.backoffice.BaseBackofficeApiService;
import org.foxtrot.backoffice.dto.AddWhitelistCellphonesRequest;
import org.foxtrot.backoffice.dto.UserExistRequest;
import org.foxtrot.backoffice.dto.UserExistResponse;
import org.foxtrot.backoffice.dto.WhitelistCellphoneUserDto;

import java.util.List;

import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.apache.http.HttpStatus.SC_OK;
import static org.foxtrot.backoffice.constants.BackofficeEndpoints.USERS_EXIST;
import static org.foxtrot.backoffice.constants.BackofficeEndpoints.USERS_WHITELIST_CELLPHONES;

public class BackofficeUsersApiService extends BaseBackofficeApiService {

    /**
     * Проверяет, существует ли пользователь с указанным телефоном в системе.
     *
     * @param phone номер в формате 7XXXXXXXXXX (11 цифр)
     * @return true — пользователь уже есть, false — номер свободен
     */
    public boolean userExistsByPhone(String phone) {
        Response response = getBackofficeRequestSpec()
                .body(new UserExistRequest("PHONE", normalizePhone(phone)))
                .when()
                .post(USERS_EXIST)
                .then()
                .extract()
                .response();

        if (response.statusCode() != SC_OK) {
            throw new RuntimeException(
                    "Backoffice users/exist failed: status=" + response.statusCode()
                            + ", body=" + response.getBody().asString()
            );
        }

        String body = response.getBody().asString().trim();
        if ("true".equalsIgnoreCase(body) || "false".equalsIgnoreCase(body)) {
            return Boolean.parseBoolean(body);
        }

        UserExistResponse dto = response.getBody().as(UserExistResponse.class);
        if (dto.getExist() != null) {
            return dto.getExist();
        }
        if (dto.getExists() != null) {
            return dto.getExists();
        }
        return response.jsonPath().getBoolean("exist");
    }

    /**
     * Добавляет номер в whitelist организации (создаёт сущность пользователя в backoffice).
     */
    public void addPhoneToWhitelist(long phone, long organizationId, String initiator, String responsible) {
        WhitelistCellphoneUserDto user = new WhitelistCellphoneUserDto(
                organizationId,
                phone,
                responsible != null ? responsible : "",
                initiator
        );
        Response response = getBackofficeRequestSpec()
                .body(new AddWhitelistCellphonesRequest(List.of(user)))
                .when()
                .post(USERS_WHITELIST_CELLPHONES)
                .then()
                .extract()
                .response();

        int status = response.statusCode();
        if (status != SC_OK && status != SC_NO_CONTENT) {
            throw new RuntimeException(
                    "Backoffice whitelist/cellphones failed: status=" + status
                            + ", body=" + response.getBody().asString()
            );
        }
    }

    public void addPhoneToWhitelist(long phone) {
        addPhoneToWhitelist(
                phone,
                config.backofficeOrganizationId(),
                config.backofficeWhitelistInitiator(),
                ""
        );
    }

    private static String normalizePhone(String phone) {
        String digits = phone.replaceAll("\\D", "");
        if (digits.length() == 10 && digits.startsWith("9")) {
            return "7" + digits;
        }
        return digits;
    }
}
