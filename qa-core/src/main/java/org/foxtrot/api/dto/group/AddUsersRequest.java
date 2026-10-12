package org.foxtrot.api.dto.group;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddUsersRequest {

    private long groupId;
    private List<Long> users;
}
