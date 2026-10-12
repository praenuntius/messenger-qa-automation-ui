package org.foxtrot.api.dto.contact.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AddContactResponse {

    public long ownerUserId;
    public long id;
    public String type;
    public long userId;
    public String name;
    public String middleName;
    public String lastname;
    public boolean bot;
}
