package org.foxtrot.api;

import org.foxtrot.api.dto.account.RemovePhotoHistoryItemRequest;
import org.foxtrot.api.dto.account.UpdateProfilePhotoRequest;
import org.foxtrot.api.dto.group.CreateGroupRequest;
import org.foxtrot.api.dto.message.SendAudioRequest;
import org.foxtrot.api.dto.message.SendFileRequest;
import org.foxtrot.api.dto.message.SendImageRequest;
import org.foxtrot.api.dto.message.SendTextMessageRequest;
import org.foxtrot.api.dto.message.SendVideoRequest;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;

public class DataGenerator {

    // Profile photo
    private static final String DEFAULT_PHOTO_ID = "cf96c131-1c7f-4de6-27f1-cabe122a9be2";
    private static final String DEFAULT_PHOTO_KEY = "cMh5o4Vpgg0sdJ+DDOqiIvDAPfW0NCx3kv4KBSHM8GE=";
    private static final String DEFAULT_PHOTO_URL = "https://s3-preprod.domain.net/fileupload-pp/6db9d161-4880-c779-ad1c-9e395402af27-fa44d229-c0a8-dd30-96e4-56437aca9c73.webp";
    private static final String DEFAULT_PHOTO_DELETE_ID = "cf96c131-1c7f-4de6-27f1-cabe122a9be2_toW_150";

    //ScheduledPhoto
    private static final String DEFAULT_SCHEDULED_PHOTO_ID = "0d43c68a-480c-3cad-ae52-28c9ab05b40c";
    private static final String DEFAULT_SCHEDULED_PHOTO_KEY = "ZjTrrc826uP7c/u4r7fiMzYBUNMIaNJPLrujoxxQXpA=";
    private static final String DEFAULT_SCHEDULED_PHOTO_URL = "https://s3-preprod.domain.net/fileupload-pp/ad80a552-f489-c50c-eccb-a46ac6baf4ee-4673a9f6-a1f3-6ca1-173f-26446272ce53.jpg";
    private static final String DEFAULT_SCHEDULED_TRANSFORMATION = "GOST3412-2015/ECB/PKCS7Padding";
    // Image message
    private static final String IMAGE_FILE_NAME = "Без команды.jpg";
    private static final long IMAGE_SIZE = 1_065_938L;
    private static final String IMAGE_MIME_TYPE = "image/jpeg";
    private static final int IMAGE_WIDTH = 1280;
    private static final int IMAGE_HEIGHT = 720;

    // Image message (pre-uploaded fixture from /upload/image)
    private static final String IMAGE_MSG_ID = "05f4bdf9-5f6f-995f-d311-be99a6f7dda7";
    private static final String IMAGE_MSG_URL = "https://s3-preprod.domain.net/fileupload-pp/f4717a6a-198e-6eda-2fa0-8b26b6a3684f-f8ee3aba-1a01-98fa-025d-d9bbbf01f451.PNG";
    private static final String IMAGE_MSG_KEY = "lkrAuFdpfVOavdWEQtGkCaLRgHpnpAn2HuI1cEegotM=";
    private static final String IMAGE_MSG_TRANSFORMATION = "GOST3412-2015/ECB/PKCS7Padding";
    private static final String IMAGE_MSG_FILE_NAME = "IMG_1606.PNG";
    private static final long IMAGE_MSG_LENGTH = 2_113_408L;
    private static final String IMAGE_MSG_MIME_TYPE = "image/png";
    private static final int IMAGE_MSG_WIDTH = 973;
    private static final int IMAGE_MSG_HEIGHT = 1280;

    // Video message
    private static final String VIDEO_FILE_NAME = "test_video.mp4";
    private static final long VIDEO_LENGTH = 1_005_275L;
    private static final String VIDEO_MIME_TYPE = "video/mp4";
    private static final String VIDEO_ID = "64327595-6503-5e58-37bd-a0ccfe583b4b";
    private static final String VIDEO_URL = "https://s3-preprod.domain.net/fileupload-pp/6a1c551a-851c-1f52-e80d-5a34e4ca43ec-e7927595-23d7-f68b-23b7-611b9690f528";
    private static final String THUMB_ID = "64327595-6503-5e58-37bd-a0ccfe583b4b_toW_800";
    private static final String THUMB_URL = "https://s3-preprod.domain.net/fileupload-pp/969c3f0d-7ee6-30e9-c956-78a341394994-36392400-733c-ef95-54ae-75063f12c1a2";
    private static final String THUMB_KEY = "ZjTrrc826uP7c/u4r7fiMzYBUNMIaNJPLrujoxxQXpA=";
    private static final String TRANSFORMATION = "GOST3412-2015/ECB/PKCS7Padding";
    private static final int VIDEO_HEIGHT = 2240;
    private static final int VIDEO_WIDTH = 3584;
    private static final int VIDEO_DURATION_SEC = 2;

    // Audio message
    private static final String AUDIO_FILE_NAME = "test_audio.mp3";
    private static final long AUDIO_LENGTH = 256_000L;
    private static final String AUDIO_MIME_TYPE = "audio/mpeg";
    private static final int AUDIO_DURATION_SEC = 2;

    // File message
    private static final String FILE_FILE_NAME = "test_file.pdf";
    private static final long FILE_LENGTH = 64_000L;
    private static final String FILE_MIME_TYPE = "application/pdf";

    private static long SCHEDULED_AT_MILLIS = LocalDateTime.now()
            .plusDays(10)
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli();

    public static UpdateProfilePhotoRequest getDefaultUpdatePhotoRequest() {
        return new UpdateProfilePhotoRequest(
                new UpdateProfilePhotoRequest.Photo(
                        DEFAULT_PHOTO_ID,
                        DEFAULT_PHOTO_URL,
                        DEFAULT_PHOTO_KEY));
    }

    public static RemovePhotoHistoryItemRequest getDefaultRemovePhotoHistoryItemRequest() {
        return new RemovePhotoHistoryItemRequest(DEFAULT_PHOTO_DELETE_ID
        );
    }

    public static SendImageRequest getDefaultSendImageMessageRequest(long groupId) {
        return new SendImageRequest(
                groupId,
                new SendImageRequest.Image(
                        IMAGE_MSG_FILE_NAME,
                        IMAGE_MSG_LENGTH,
                        IMAGE_MSG_MIME_TYPE,
                        new SendImageRequest.ResourceRef(
                                IMAGE_MSG_ID,
                                IMAGE_MSG_URL,
                                IMAGE_MSG_KEY,
                                IMAGE_MSG_TRANSFORMATION
                        ),
                        IMAGE_MSG_WIDTH,
                        IMAGE_MSG_HEIGHT
                )
        );
    }

    public static SendImageRequest getDefaultSendImageMessageRequestScheduled(long groupId) {
        return new SendImageRequest(
                groupId,
                new SendImageRequest.Image(
                        IMAGE_FILE_NAME,
                        IMAGE_SIZE,
                        IMAGE_MIME_TYPE,
                        new SendImageRequest.ResourceRef(
                                DEFAULT_SCHEDULED_PHOTO_ID,
                                DEFAULT_SCHEDULED_PHOTO_URL,
                                DEFAULT_SCHEDULED_PHOTO_KEY,
                                DEFAULT_SCHEDULED_TRANSFORMATION
                        ),
                        IMAGE_WIDTH,
                        IMAGE_HEIGHT
                ),
                new SendImageRequest.Scheduler(SCHEDULED_AT_MILLIS)
        );
    }

    public static SendVideoRequest getDefaultSendVideoMessageRequestScheduled(long groupId) {
        SendVideoRequest.ResourceRef videoRef = new SendVideoRequest.ResourceRef(
                VIDEO_ID, VIDEO_URL, null, null
        );

        SendVideoRequest.ResourceRef thumbRef = new SendVideoRequest.ResourceRef(
                THUMB_ID, THUMB_URL, THUMB_KEY, TRANSFORMATION
        );

        SendVideoRequest.Video video = new SendVideoRequest.Video(
                VIDEO_FILE_NAME,
                VIDEO_LENGTH,
                VIDEO_MIME_TYPE,
                videoRef,
                thumbRef,
                VIDEO_HEIGHT,
                VIDEO_WIDTH,
                VIDEO_DURATION_SEC
        );

        return new SendVideoRequest(
                "Видео",
                Collections.emptyList(),
                groupId,
                System.currentTimeMillis(),
                false,
                video,
                new SendVideoRequest.Scheduler(SCHEDULED_AT_MILLIS)
        );
    }

    public static SendVideoRequest getDefaultSendVideoMessageRequest(long groupId) {
        SendVideoRequest.ResourceRef videoRef = new SendVideoRequest.ResourceRef(
                VIDEO_ID, VIDEO_URL, null, null
        );

        SendVideoRequest.ResourceRef thumbRef = new SendVideoRequest.ResourceRef(
                THUMB_ID, THUMB_URL, THUMB_KEY, TRANSFORMATION
        );

        SendVideoRequest.Video video = new SendVideoRequest.Video(
                VIDEO_FILE_NAME,
                VIDEO_LENGTH,
                VIDEO_MIME_TYPE,
                videoRef,
                thumbRef,
                VIDEO_HEIGHT,
                VIDEO_WIDTH,
                VIDEO_DURATION_SEC
        );

        return new SendVideoRequest(
                "Видео",
                Collections.emptyList(),
                groupId,
                System.currentTimeMillis(),
                false,
                video,
                null
        );
    }

    public static SendAudioRequest getDefaultSendAudioMessageRequest(long groupId) {
        SendAudioRequest.ResourceRef ref = new SendAudioRequest.ResourceRef(
                VIDEO_ID, VIDEO_URL, null, null
        );
        SendAudioRequest.Audio audio = new SendAudioRequest.Audio(
                AUDIO_FILE_NAME,
                AUDIO_LENGTH,
                AUDIO_MIME_TYPE,
                ref,
                AUDIO_DURATION_SEC
        );
        return new SendAudioRequest(
                "",
                Collections.emptyList(),
                groupId,
                System.currentTimeMillis(),
                false,
                audio,
                null
        );
    }

    public static SendFileRequest getDefaultSendFileMessageRequest(long groupId) {
        SendFileRequest.ResourceRef ref = new SendFileRequest.ResourceRef(
                VIDEO_ID, VIDEO_URL, null, null
        );
        SendFileRequest.File file = new SendFileRequest.File(
                FILE_FILE_NAME,
                FILE_LENGTH,
                FILE_MIME_TYPE,
                ref
        );
        return new SendFileRequest(
                "",
                Collections.emptyList(),
                groupId,
                System.currentTimeMillis(),
                false,
                file,
                null
        );
    }

    /**
     * Reuse pre-uploaded image from S3 for group/channel avatar in createGroup request.
     */
    public static CreateGroupRequest.Image getDefaultGroupImage() {
        return new CreateGroupRequest.Image(
                DEFAULT_PHOTO_ID,
                DEFAULT_PHOTO_URL,
                DEFAULT_PHOTO_KEY,
                TRANSFORMATION
        );
    }

    public static SendTextMessageRequest getDefaultSendTextMessageRequestScheduled(long groupId, String text) {
        return SendTextMessageRequest.scheduled(text, groupId, SCHEDULED_AT_MILLIS);
    }

    public static SendTextMessageRequest getDefaultSendTextMessageRequestScheduledWithReply(long groupId, String text, long replyMsgId) {
        return SendTextMessageRequest.scheduledWithReply(text, groupId, SCHEDULED_AT_MILLIS, replyMsgId);
    }
}