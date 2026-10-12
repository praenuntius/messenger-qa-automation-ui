package org.foxtrot.api.dto.account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateFullProfileRequest {
    private String firstName;
    private String lastName;
    private String middleName;
    private String jobTitle = "";
    private List<String> additionalEmails = List.of();
    private List<String> additionalPhones = List.of();

    public UpdateFullProfileRequest(String firstName, String lastName, String middleName) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
        this.jobTitle = "";
        this.additionalEmails = new ArrayList<>();
        this.additionalPhones = new ArrayList<>();
    }
}