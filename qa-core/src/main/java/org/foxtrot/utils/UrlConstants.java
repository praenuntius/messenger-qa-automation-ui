package org.foxtrot.utils;

public class UrlConstants {
    // Базовый домен
    private static final String BASE_URL = "https://web-preprod.domain.net";

    //URL Ссылки-приглашения в группу
    public static final String HOMESPACE_GROUP_INVITE_URL = BASE_URL + "/group/invite/{inviteId}";
    public static final String WORKSPACE_GROUP_INVITE_URL = BASE_URL + "/group/invite/{inviteId}";
    public static final String WORKSPACE_INVITE_URL = BASE_URL + "/link/invite/{inviteId}";

    // URL для профилей пользователей (основаны на P2P чате)
    public static final String ADMIN_P2P_CHAT_URL = BASE_URL + "/im/home/-{adminId}";
    public static final String PARTICIPANT_P2P_CHAT_URL = BASE_URL + "/im/home/{participantId}";
    public static final String NON_PARTICIPANT_P2P_CHAT_URL = BASE_URL + "/im/home/{nonParticipantId}";

    // URL в Homespace
    public static final String HOMESPACE_GROUP_URL = BASE_URL + "/im/home/{groupId}";
    public static final String HOMESPACE_PRIVATE_CHANNEL_URL = BASE_URL + "/im/home/{channelId}"; // Если есть отдельные ID
    public static final String HOMESPACE_PUBLIC_CHANNEL_URL = BASE_URL + "/im/home/{channelId}";  // Если нет разделения

    // URL для пространств
    public static final String WORKSPACE_URL = BASE_URL + "/im/{workspaceId}/";

    // URL в рабочем пространстве
    public static final String WORKSPACE_PUBLIC_CHANNEL_URL = BASE_URL + "/im/{workspaceId}/{channelId}";
    public static final String WORKSPACE_PRIVATE_CHANNEL_URL = BASE_URL + "/im/{workspaceId}/{channelId}";
    public static final String WORKSPACE_PUBLIC_GROUP_URL = BASE_URL + "/im/{workspaceId}/{groupId}";
    public static final String WORKSPACE_PRIVATE_GROUP_URL = BASE_URL + "/im/{workspaceId}/{groupId}";

    // Приватный метод для формирования URL с подстановкой
    private static String formatUrl(String template, Object... params) {
        System.out.println("template = " + template);
        String result = template;
        for (Object param : params) {
            result = result.replaceFirst("\\{[^}]+\\}", String.valueOf(param));
        }
        return result;
    }

    public static String getHomespaceGroupInviteUrl(String inviteId) {
        return formatUrl(HOMESPACE_GROUP_INVITE_URL, inviteId);
    }

    public static String getWorkspaceGroupInviteUrl(String inviteId) {
        return formatUrl(WORKSPACE_GROUP_INVITE_URL, inviteId);
    }

    public static String getWorkspaceInviteUrl(String inviteId) {
        return formatUrl(WORKSPACE_INVITE_URL, inviteId);
    }

    public static String getP2pChatUrl(String adminId) {
        return formatUrl(ADMIN_P2P_CHAT_URL, adminId);
    }

    public static String getParticipantP2pChatUrl(String participantId) {
        return formatUrl(PARTICIPANT_P2P_CHAT_URL, participantId);
    }

    public static String getNonParticipantP2pChatUrl(String nonParticipantId) {
        return formatUrl(NON_PARTICIPANT_P2P_CHAT_URL, nonParticipantId);
    }

    public static String getHomespaceGroupUrl(String groupId) {
        return formatUrl(HOMESPACE_GROUP_URL, groupId);
    }

    public static String getHomespaceChannelUrl(String channelId) {
        return formatUrl(HOMESPACE_PUBLIC_CHANNEL_URL, channelId); // Или PRIVATE, если нужно разделение
    }

    public static String getHomespacePrivateChannelUrl(String channelId) {
        return formatUrl(HOMESPACE_PRIVATE_CHANNEL_URL, channelId);
    }

    public static String getWorkspaceUrl(String workspaceId) {
        return formatUrl(WORKSPACE_URL, workspaceId);
    }

    public static String getWorkspaceChannelUrl(String workspaceId, String channelId) {
        return formatUrl(WORKSPACE_PUBLIC_CHANNEL_URL, workspaceId, channelId);
    }

    public static String getWorkspacePrivateChannelUrl(String workspaceId, String channelId) {
        return formatUrl(WORKSPACE_PRIVATE_CHANNEL_URL, workspaceId, channelId);
    }

    public static String getWorkspacePublicGroupUrl(String workspaceId, String channelId) {
        return formatUrl(WORKSPACE_PUBLIC_GROUP_URL, workspaceId, channelId);
    }

    public static String getWorkspacePrivateGroupUrl(String workspaceId, String groupId) {
        return formatUrl(WORKSPACE_PRIVATE_GROUP_URL, workspaceId, groupId);
    }
}