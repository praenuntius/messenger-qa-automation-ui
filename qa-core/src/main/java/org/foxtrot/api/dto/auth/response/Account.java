package org.foxtrot.api.dto.auth.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Account {

    private long id;
    private String firstName;
    private String middleName;
    private String lastName;
    private MainAuthWay mainAuthWay;
    private String jobTitle;
    private long phone;
    private List<String> additionalPhones;
    private List<String> additionalEmails;
    private boolean tfaEnabled;
    private AccountSettings accountSettings;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MainAuthWay {
        private String type;
        private String value;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AccountSettings {
        private List<String> permissions;
        private String unreadMessagesMode;
        private String contactDataVisibility;
        private List<String> reactions;
        private String quickReaction;
    }

    /** Для совместимости с тестами, ожидающими имя из UsersEnum. */
    public String getDisplayName() {
        if (firstName != null && lastName != null && !lastName.isEmpty()) {
            return firstName + " " + lastName;
        }
        return firstName != null ? firstName : ("User " + id);
    }
}
