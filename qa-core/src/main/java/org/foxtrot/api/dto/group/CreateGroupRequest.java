package org.foxtrot.api.dto.group;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateGroupRequest {

    private String name;
    private String groupType;
    private String description;
    
    // Аватар чата

    private Image image;
    private Boolean visible;
    
    // Инвайт-линк в чат  
    
    private String inviteLink;
    
    // Добавление участников при создании чата.

    private List<Long> participantIds;
    private ChannelOptions channelOptions;
    private GroupOptions groupOptions;
    private List<Board> boards;

    public CreateGroupRequest(String name, String groupType, boolean visible) {
        this.name = name;
        this.groupType = groupType;
        this.visible = visible;
    }

    /** Preprod RC: часть ручек читает {@code type} вместо {@code groupType}. */
    @JsonGetter("type")
    public String getType() {
        return groupType;
    }

    //конструктор для создания чата с параметрами

    public CreateGroupRequest(String name, String groupType, boolean visible, String description, ChannelOptions channelOptions) {
        this.name = name;
        this.groupType = groupType;
        this.visible = visible;
        this.description = description;
        this.channelOptions = channelOptions;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Image {
        private String id;
        private String url;
        private String key;
        private String transformation;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChannelOptions {
        private Boolean showParticipantsCount;
        private Boolean commentsEnabled;
        private Boolean reactionsEnabled;
        private Boolean showForwardedName;

        public ChannelOptions(Boolean commentsEnabled){
            this.commentsEnabled = commentsEnabled;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GroupOptions {
        private Boolean restrictVoiceMessages;
        private Boolean restrictVideoRoundMessages;
        private Boolean profileViewDisabled;
        private Boolean hideSearchAction;
        private Boolean hidePinnedMessageAction;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Board {
        private Long id;
        private Long groupId;
        private Long workspaceId;
        private String name;
        private String description;
        private List<Long> tags;
    }
}
