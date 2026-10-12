package org.foxtrot.api.dto.workSpace;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeleteUserRequest {

    private Long userId;
}
