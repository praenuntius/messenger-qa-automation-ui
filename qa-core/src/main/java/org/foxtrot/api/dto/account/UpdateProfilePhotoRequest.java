package org.foxtrot.api.dto.account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProfilePhotoRequest {
    private Photo photo;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
   public static class Photo {
        private String id;
        private String url;
        private String key;
    }
}
