package org.foxtrot.desktop.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.foxtrot.utils.Waits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Ожидает готовности CDP HTTP API и определяет версию Chromium в Electron.
 */
public final class CdpBrowserVersionResolver {

    private static final Logger logger = LoggerFactory.getLogger(CdpBrowserVersionResolver.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Pattern BROWSER_VERSION = Pattern.compile("(?:Chrome|Chromium)/([\\d.]+)");
    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(3))
            .build();

    private CdpBrowserVersionResolver() {
    }

    /**
     * Ждёт ответа {@code /json/version} и возвращает версию Chromium.
     * TCP-порт может быть открыт раньше, чем CDP HTTP — поэтому опрашиваем в цикле.
     */
    public static String waitForReady(int cdpPort, int timeoutSeconds) {
        String url = versionUrl(cdpPort);
        logger.info("Жду готовности CDP HTTP: {} (таймаут {}с)", url, timeoutSeconds);

        long deadline = System.currentTimeMillis() + timeoutSeconds * 1000L;
        Exception lastError = null;
        while (System.currentTimeMillis() < deadline) {
            try {
                String version = fetchVersion(url);
                logger.info("CDP готов, Chromium: {}", version);
                return version;
            } catch (Exception e) {
                lastError = e;
                Waits.sleepMillis(500);
            }
        }
        throw new IllegalStateException(
                "CDP HTTP не ответил за " + timeoutSeconds + "с: " + url, lastError);
    }

    public static String resolve(int cdpPort) {
        String url = versionUrl(cdpPort);
        try {
            return fetchVersion(url);
        } catch (Exception e) {
            throw new IllegalStateException("Не удалось получить версию Chromium с " + url, e);
        }
    }

    public static String versionUrl(int cdpPort) {
        return "http://127.0.0.1:" + cdpPort + "/json/version";
    }

    private static String fetchVersion(String url) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(3))
                .GET()
                .build();
        HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new IllegalStateException("CDP /json/version вернул HTTP " + response.statusCode());
        }

        JsonNode root = MAPPER.readTree(response.body());
        String browser = root.path("Browser").asText("");
        Matcher matcher = BROWSER_VERSION.matcher(browser);
        if (!matcher.find()) {
            throw new IllegalStateException("Не удалось распознать версию Chromium в Browser=" + browser);
        }
        String version = matcher.group(1);
        logger.debug("CDP Browser: {} → version {}", browser, version);
        return version;
    }
}
