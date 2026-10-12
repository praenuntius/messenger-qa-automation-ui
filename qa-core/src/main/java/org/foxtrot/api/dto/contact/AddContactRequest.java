package org.foxtrot.api.dto.contact;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddContactRequest {

    private Long userId;
}
