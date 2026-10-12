package org.foxtrot.api.dto.workSpace;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateNotificationRequest {

    private String workspaceId;
    private NotificationType notificationType;

    public enum NotificationType {
        ON, OFF
    }
}