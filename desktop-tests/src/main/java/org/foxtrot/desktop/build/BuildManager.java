package org.foxtrot.desktop.build;

import org.aeonbits.owner.ConfigFactory;
import org.foxtrot.desktop.config.DesktopConfig;
import org.foxtrot.desktop.config.DesktopPlatform;
import org.foxtrot.desktop.config.DesktopPlatformResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;

/**
 * Точка входа для управления билдами.
 * Сначала проверяет кеш (пункты 8, 9), при необходимости скачивает (пункт 1).
 * После ensureBuild() гарантируется существование актуального артефакта.
 */
public class BuildManager {

    private static final Logger logger = LoggerFactory.getLogger(BuildManager.class);
    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(30))
            .followRedirects(HttpClient.Redirect.ALWAYS)
            .build();

    private static final DesktopConfig config = ConfigFactory.create(DesktopConfig.class);
    private static final BuildCache cache = new BuildCache(config.buildCacheDir());

    private BuildManager() {
    }

    /**
     * Обеспечивает наличие актуального артефакта в кеше.
     * Возвращает путь к артефакту.
     */
    public static synchronized Path ensureBuild(DesktopPlatform platform, String requestedVersion) {
        String url = BuildUrls.forVersion(config.buildBaseUrl(), requestedVersion, platform);
        logger.info("Проверяю артефакт для platform={}, version={}, url={}", platform, requestedVersion, url);

        // Для latest — сначала HEAD-запрос, чтобы получить ETag (пункт 9)
        String remoteEtag = "latest".equalsIgnoreCase(requestedVersion)
                ? fetchEtag(url)
                : null;

        if (cache.isCacheValid(platform, requestedVersion, remoteEtag)) {
            BuildMetadata meta = cache.loadMetadata(platform).orElseThrow();
            Path cached = Path.of(meta.getLocalPath());
            logger.info("Кеш актуален: {}", cached);
            return cached;
        }

        return download(platform, requestedVersion, url, remoteEtag);
    }

    /** Удобная перегрузка — использует платформу и версию из конфига. */
    public static Path ensureBuild() {
        DesktopPlatform platform = DesktopPlatformResolver.getPlatform();
        String version = config.appVersion();
        return ensureBuild(platform, version);
    }

    /**
     * Возвращает версию кешированного билда или null, если кеш пуст.
     * Используется для вывода в логах/Allure (пункт 2).
     */
    public static String resolvedVersion(DesktopPlatform platform) {
        return cache.loadMetadata(platform)
                .map(BuildMetadata::getResolvedVersion)
                .orElse(null);
    }

    // -------------------------------------------------------------------------

    private static Path download(DesktopPlatform platform, String requestedVersion,
                                 String url, String knownEtag) {
        logger.info("Скачиваю артефакт: {}", url);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofMinutes(10))
                .GET()
                .build();

        HttpResponse<InputStream> response;
        try {
            response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofInputStream());
        } catch (Exception e) {
            throw new RuntimeException("Ошибка скачивания артефакта: " + url, e);
        }

        if (response.statusCode() != 200) {
            throw new RuntimeException(
                    "Скачивание вернуло HTTP " + response.statusCode() + " для: " + url);
        }

        String etag = knownEtag != null ? knownEtag
                : response.headers().firstValue("ETag").orElse("");
        String filename = filenameFromUrl(url);
        String resolvedVersion = resolveVersionFromFilename(filename, requestedVersion);

        Path localPath = cache.artifactPath(platform, filename);
        try {
            Files.createDirectories(localPath.getParent());
            Files.copy(response.body(), localPath, StandardCopyOption.REPLACE_EXISTING);
            logger.info("Артефакт сохранён: {} ({} байт)", localPath, Files.size(localPath));

            // Для AppImage на Linux — сразу делаем исполняемым
            if (platform == DesktopPlatform.LINUX) {
                localPath.toFile().setExecutable(true);
            }

            BuildMetadata metadata = cache.buildMetadata(
                    platform, requestedVersion, resolvedVersion, filename, localPath, etag);
            cache.saveMetadata(platform, metadata);

        } catch (Exception e) {
            throw new RuntimeException("Ошибка при сохранении артефакта", e);
        }

        return localPath;
    }

    private static String fetchEtag(String url) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .method("HEAD", HttpRequest.BodyPublishers.noBody())
                    .timeout(Duration.ofSeconds(15))
                    .build();
            HttpResponse<Void> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.discarding());
            String etag = response.headers().firstValue("ETag").orElse(null);
            logger.debug("HEAD {} → ETag: {}", url, etag);
            return etag;
        } catch (Exception e) {
            logger.warn("HEAD-запрос не удался для {}: {}", url, e.getMessage());
            return null;
        }
    }

    private static String filenameFromUrl(String url) {
        return url.substring(url.lastIndexOf('/') + 1);
    }

    /**
     * Пытается извлечь версию из имени файла.
     * Пример: tdm-3.52.27.beta.AppImage → 3.52.27
     *         tdm.latest.AppImage        → latest
     */
    private static String resolveVersionFromFilename(String filename, String requestedVersion) {
        if ("latest".equalsIgnoreCase(requestedVersion)) {
            return "latest";
        }
        // tdm-{version}.beta.{ext}
        if (filename.matches("tdm-\\d+\\.\\d+\\.\\d+\\.beta\\..*")) {
            String[] parts = filename.split("-");
            if (parts.length >= 2) {
                return parts[1].split("\\.beta\\.")[0];
            }
        }
        return requestedVersion;
    }
}
