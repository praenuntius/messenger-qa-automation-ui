package org.foxtrot.utils.user;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public class UserRegistrationData {

    private final String phone;
    private final String firstName;
    private final String lastName;
    private final String middleName;
    private final String recoveryEmail;
    private final String password;
    private final String smsCode;
    private final String emailVerificationCode;
    private final Long organizationId;
    private final String whitelistInitiator;
    private final boolean setupRealEncryptionKeys;

    public static UserRegistrationData defaultsForPhone(String phone) {
        String suffix = phone.substring(Math.max(0, phone.length() - 6));
        return UserRegistrationData.builder()
                .phone(phone)
                .firstName("Auto")
                .lastName("User" + suffix)
                .middleName("")
                .recoveryEmail("qa-auto+" + phone + "@domain.net")
                .password(org.foxtrot.api.service.RegistrationApiService.DEFAULT_PASSWORD)
                .smsCode(org.foxtrot.api.service.RegistrationApiService.DEFAULT_SMS_CODE)
                .emailVerificationCode(org.foxtrot.api.service.RegistrationApiService.DEFAULT_SMS_CODE)
                .setupRealEncryptionKeys(false)
                .build();
    }
}
