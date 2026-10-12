package org.foxtrot.api.dto.contact;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetContactsResponse {
    private List<ContactDto> contacts;

    public List<ContactDto> getContacts() {
        return contacts != null ? contacts : Collections.emptyList();
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ContactDto {
        private long ownerUserId;
        private long id;
        private String type;
        private long userId;
        private String name;
        private String middleName;
        private String lastname;
        private boolean bot;
    }
}
