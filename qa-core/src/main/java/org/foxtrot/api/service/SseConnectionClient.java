package org.foxtrot.api.service;

import org.aeonbits.owner.ConfigFactory;
import org.foxtrot.config.CoreConfig;

import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Минимальный клиент для проверки HTTP-кода при подключении к SSE (pusher).
 *
 * <p>Контракт (бэкенд / браузер):
 * <ul>
 *   <li>Query: {@code ?version=...&platform=WEB|ANDROID|IOS} (как в браузере)</li>
 *   <li>{@code GET /api/v3/events} — {@code WorkspaceIds: wsId:ts,wsId:ts}</li>
 *   <li>{@code GET /api/v3/events/group} — {@code GroupId}, {@code WorkSpaceId}</li>
 * </ul>
 */
public class SseConnectionClient {

    /** Homespace: тот же контракт, что и у REST ({@code workspaceId: -1}). */
    public static final String HOMESPACE_WORKSPACE_ID = "-1";

    private static final CoreConfig config = ConfigFactory.create(CoreConfig.class);

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(30))
            .build();

    public record SseConnectResult(int statusCode, String bodyPreview, String requestSummary) {
    }

    /**
     * Основной SSE {@code /api/v3/events} с homespace {@code WorkspaceIds=-1:0}
     * (первый connect / проверка ключа без догона событий).
     */
    public int connect(String path, String accessToken, long encryptionKeyId) {
        return connectDetailed(path, accessToken, encryptionKeyId, homespaceWorkspaceIds(0L)).statusCode();
    }

    /**
     * Основной SSE с явным {@code WorkspaceIds} ({@code wsId:timestamp[,...]}).
     */
    public int connect(String path,
                       String accessToken,
                       long encryptionKeyId,
                       String workspaceIds) {
        return connectDetailed(path, accessToken, encryptionKeyId, workspaceIds).statusCode();
    }

    public SseConnectResult connectDetailed(String path,
                                            String accessToken,
                                            long encryptionKeyId,
                                            String workspaceIds) {
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("WorkspaceIds", workspaceIds);
        return connectDetailed(path, accessToken, encryptionKeyId, headers);
    }

    /**
     * Temporary/group SSE: {@code GET /api/v3/events/group}
     * с заголовками {@code GroupId}, {@code WorkSpaceId} (строки).
     */
    public int connectGroup(String path,
                            String accessToken,
                            long encryptionKeyId,
                            long groupId) {
        return connectGroupDetailed(path, accessToken, encryptionKeyId, groupId, HOMESPACE_WORKSPACE_ID)
                .statusCode();
    }

    public int connectGroup(String path,
                            String accessToken,
                            long encryptionKeyId,
                            long groupId,
                            String workspaceId) {
        return connectGroupDetailed(path, accessToken, encryptionKeyId, groupId, workspaceId).statusCode();
    }

    public SseConnectResult connectGroupDetailed(String path,
                                                 String accessToken,
                                                 long encryptionKeyId,
                                                 long groupId,
                                                 String workspaceId) {
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("GroupId", String.valueOf(groupId));
        headers.put("WorkSpaceId", workspaceId != null ? workspaceId : HOMESPACE_WORKSPACE_ID);
        return connectDetailed(path, accessToken, encryptionKeyId, headers);
    }

    public int connect(String path,
                       String accessToken,
                       long encryptionKeyId,
                       Map<String, String> extraHeaders) {
        return connectDetailed(path, accessToken, encryptionKeyId, extraHeaders).statusCode();
    }

    public SseConnectResult connectDetailed(String path,
                                            String accessToken,
                                            long encryptionKeyId,
                                            Map<String, String> extraHeaders) {
        try {
            String uri = config.pusherBaseUrl() + path + defaultQueryString(path);
            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(uri))
                    .timeout(Duration.ofSeconds(30))
                    .header("Accept", "text/event-stream")
                    .header("Authorization", accessToken)
                    .header("encryption-key-id", String.valueOf(encryptionKeyId))
                    .GET();

            extraHeaders.forEach(builder::header);

            String summary = "GET " + uri
                    + " encryption-key-id=" + encryptionKeyId
                    + " headers=" + extraHeaders;

            HttpResponse<InputStream> response = httpClient.send(
                    builder.build(),
                    HttpResponse.BodyHandlers.ofInputStream()
            );

            String bodyPreview = readPreview(response.body());
            return new SseConnectResult(response.statusCode(), bodyPreview, summary);
        } catch (Exception e) {
            throw new RuntimeException("SSE connect failed: " + path, e);
        }
    }

    /** Homespace cursor: {@code -1:<timestamp>}. */
    public static String homespaceWorkspaceIds(long timestamp) {
        return formatWorkspaceIds(Map.of(Long.parseLong(HOMESPACE_WORKSPACE_ID), timestamp));
    }

    /**
     * Формат бэкенда: {@code workspaceId:timestamp[,workspaceId:timestamp...]}.
     * Пример: {@code 1:444,23:333}.
     */
    public static String formatWorkspaceIds(Map<Long, Long> workspaceIdToTimestamp) {
        if (workspaceIdToTimestamp == null || workspaceIdToTimestamp.isEmpty()) {
            throw new IllegalArgumentException("WorkspaceIds map must not be empty");
        }
        return workspaceIdToTimestamp.entrySet().stream()
                .map(e -> e.getKey() + ":" + e.getValue())
                .collect(Collectors.joining(","));
    }

    /**
     * Как в браузере: {@code ?version=3.56&platform=WEB}.
     * Override: {@code -Dencryption.sse.version=} / {@code -Dencryption.sse.platform=}.
     */
    static String defaultQueryString(String path) {
        if (path != null && path.contains("?")) {
            return "";
        }
        String version = System.getProperty("encryption.sse.version", "3.56");
        String platform = System.getProperty("encryption.sse.platform", "WEB");
        return "?version=" + encode(version) + "&platform=" + encode(platform);
    }

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private static String readPreview(InputStream body) {
        if (body == null) {
            return "";
        }
        try (body) {
            byte[] buf = body.readNBytes(2048);
            return new String(buf, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return "<unreadable body: " + e.getMessage() + ">";
        }
    }
}
