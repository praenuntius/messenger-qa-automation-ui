package org.foxtrot.api.dto.message;

import java.util.List;

public class MultiforwardMessagesRequest {

    private Long fromGroupId;
    private List<Long> messageIds;
    private List<Recipient> recipients;

    public static class Recipient {
        private Long toGroupId;
        private Long toWorkspaceId;
    }
}

