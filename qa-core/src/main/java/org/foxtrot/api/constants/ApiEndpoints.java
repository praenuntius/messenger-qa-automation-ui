package org.foxtrot.api.constants;

public class ApiEndpoints {

    /* auth endpoint */
    public final static String AUTH_START = "/api/v2/auth/start";
    public final static String VERIFY_CODE = "/api/v2/auth/verifyCode";
    public final static String VERIFY_PASSWORD = "/api/v2/auth/verifyPassword";
    public final static String SIGN_IN = "/api/v2/auth/signIn";
    public final static String REFRESH_TOKEN = "/api/v2/auth/refreshToken";
    /** Legacy one-step update (initial/legacy only; runtime rotation uses V2 + confirm). */
    public final static String UPDATE_SYMMETRIC_KEY = "/api/v2/auth/updateSymmetricKey";
    /** Runtime rotation step 1: register new key (encrypt with current, body { encryptionKeyId }). */
    public final static String UPDATE_SYMMETRIC_KEY_V2 = "/api/v2/auth/updateSymmetricKeyV2";
    /** Runtime rotation step 2: commit new key (no body; Encryption-Key-Id = new id). */
    public final static String CONFIRM_SYMMETRIC_KEY_UPDATE = "/api/v2/auth/confirmSymmetricKeyUpdate";
    public final static String SUBMIT_ACCOUNT = "/api/v2/auth/submitAccount";
    public final static String SEND_RECOVERY_EMAIL = "/api/v2/auth/sendRecoveryEmail";
    public final static String SIGN_UP = "/api/v2/auth/signUp";
    public final static String ASSETS = "/api/v1/assets";

    /* group endpoint */
    public final static String CREATE_GROUP = "/api/v1/groups/createGroup";
    public final static String ADD_USERS = "/api/v1/groups/addUsers";
    public final static String REMOVE_USER = "/api/v1/groups/removeUser";
    public final static String EDIT_ADMIN = "/api/v1/groups/editAdmin";
    public final static String DELETE_GROUP = "/api/v1/groups/deleteGroup";
    public final static String HIDE_GROUP = "/api/v1/groups/hideGroup";
    public final static String REGENERATE_INVITE_LINK = "/api/v1/groups/regenerateInviteLink";
    public final static String CREATE_P2P_GROUP = "/api/v1/groups/createP2PGroup";
    public final static String GET_USER_GROUP_STATES = "/api/v1/groups/getUserGroupStates";
    public final static String CREATE_COMMENT_GROUP = "/api/v1/groups/createCommentGroup";
    public final static String CREATE_SCHEDULED_GROUP = "/api/v1/groups/createScheduledGroup";
    public final static String UPDATE_MESSAGE_AUTO_DELETION = "/api/v1/groups/updateMessageAutoDeletion";
    public final static String UPDATE_GROUP_NOTIFICATION = "/api/v1/groups/updateGroupNotification";
    public final static String UPDATE_GROUP_UNREAD = "/api/v1/groups/updateGroupUnread";
    public final static String UPDATE_GROUP = "/api/v1/groups/updateGroup";

    /* account endpoint */
    public final static String GET_ALL_WORKSPACE = "/api/v1/account/getAllWorkspaces";
    public final static String CLEAR_HISTORY = "/api/v1/groups/clearHistory";
    public final static String UPDATE_USER_PRIVACY = "api/v1/account/updateUserPrivacy";
    public final static String UPDATE_PROFILE_PHOTO = "/api/v1/account/updateProfilePhoto";
    public final static String UPDATE_FULL_PROFILE = "api/v1/account/updateFullProfile";
    public final static String REMOVE_PHOTO_HISTORY_ITEM = "/api/v1/account/removePhotoHistoryItem";

    /* account settings endpoint */
    public final static String NON_CONTACT_PREVIEW = "/api/v1/account/settings/non-contact-preview";

    /* message endpoint */
    public final static String SEND_MASSAGE = "/api/v1/messages/sendTextMessage";

    /* contact endpoint */
    public final static String ADD_CONTACT = "/api/v1/contacts/createContact";
    public final static String DELETE_CONTACT = "/api/v1/contacts/deleteContact";
    public final static String GET_CONTACT = "/api/v1/contacts/getContacts";

    /* workspace endpoint */
    public final static String CREATE_WORKSPACE = "/api/v2/auth/createWorkspace";
    public final static String UPDATE_WORKSPACE_ROLE = "/api/v1/users/updateWorkspaceRole";
    public final static String ADD_USER_TO_WORKSPACE = "/api/v2/auth/addUsersToWorkspace";
    public final static String DELETE_WORKSPACE = "/api/v2/workspace/delete";
    public final static String GET_INVITE_LINK = "/api/v1/workspace/getInviteLink";
    public final static String DELETE_USER = "/api/v1/users/deleteUser";
    public final static String MARK_WORKSPACE_READ = "/api/v2/workspace/markWorkspaceRead";
    public final static String UPDATE_NOTIFICATION = "/api/v1/session/updateNotification";
    public final static String HIDE_ACTIVE_WORKSPACE = "/api/v1/account/settings/hide-active-workspace";
    public final static String SHOW_ACTIVE_WORKSPACE = "/api/v1/account/settings/show-active-workspace";

    /* message controller */
    public final static String SEND_TEXT_MESSAGE = "/api/v1/messages/sendTextMessage";
    public final static String UPDATE_TEXT_MESSAGE = "/api/v1/messages/updateTextMessage";

    public final static String SEND_IMAGE = "/api/v1/messages/sendImage";
    public final static String UPDATE_IMAGE = "/api/v1/messages/updateImage";

    public final static String SEND_STICKER = "/api/v1/messages/sendSticker";

    public final static String SEND_VIDEO = "/api/v1/messages/sendVideo";
    public final static String UPDATE_VIDEO = "/api/v1/messages/updateVideo";

    public final static String SEND_AUDIO = "/api/v1/messages/sendAudio";
    public final static String UPDATE_AUDIO = "/api/v1/messages/updateAudio";

    public final static String SEND_MULTIMEDIA = "/api/v1/messages/sendMultimedia";
    public final static String UPDATE_MULTIMEDIA = "/api/v1/messages/updateMultimedia";

    public final static String SEND_FILE = "/api/v1/messages/sendFile";
    public final static String UPDATE_FILE = "/api/v1/messages/updateFile";

    public final static String SEND_CONTACT = "/api/v1/messages/sendContact";

    public final static String DELETE_MESSAGES = "/api/v1/messages/deleteMessages";

    public final static String SEND_VOICE = "/api/v1/messages/sendVoice";
    public final static String RECORDING_VOICE = "/api/v1/messages/recordingVoice";

    public final static String PIN_MESSAGE = "/api/v1/messages/pinMessage";
    public final static String UNPIN_MESSAGE = "/api/v1/messages/unpinMessage";
    public final static String READ_PINNED_MESSAGE = "/api/v1/messages/readPinnedMessage";

    public final static String ADD_REACTION = "/api/v1/messages/addReaction";
    public final static String DELETE_REACTION = "/api/v1/messages/deleteReaction";

    public final static String GET_MESSAGE = "/api/v1/messages/getMessage";
    public final static String GET_MESSAGES = "/api/v1/messages/getMessages";
    public final static String GET_MESSAGES_AROUND = "/api/v1/messages/getMessagesAround";

    public final static String CONFIRM_MESSAGE = "/api/v1/messages/confirmMessage";
    public final static String TYPING = "/api/v1/messages/typing";
    public final static String MULTIFORWARD_MESSAGES = "/api/v1/messages/multiforwardMessages";

    /* poll controller */
    public final static String CREATE_POLL = "/api/v1/polls/createPoll";
    public final static String VOTE_POLL = "/api/v1/polls/vote";

    /* encryption controller */
    public final static String ENCRYPTION_GET_PUBLIC_KEY = "/api/v1/encryption/getPublicKey";
    public final static String ENCRYPTION_SAVE_SYMMETRIC_KEY = "/api/v1/encryption/saveSymmetricKey";

    /* pusher SSE */
    public final static String PUSHER_EVENTS = "/api/v3/events";
    /** Temporary/group SSE; headers: GroupId, WorkSpaceId (strings). Main /events uses WorkspaceIds. */
    public final static String PUSHER_EVENTS_GROUP = "/api/v3/events/group";

    /* users controller */
    public final static String BLOCK = "/api/v1/users/block";
    public final static String UN_BLOCK = "/api/v1/users/unblock";

    /* file upload */
    public static final String UPLOAD_ENDPOINT = "/api/v1/upload/secret/encryptable";
}
