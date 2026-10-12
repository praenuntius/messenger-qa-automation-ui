package org.foxtrot.desktop.build;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.foxtrot.desktop.config.DesktopPlatform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Optional;

/**
 * Управляет кеш-директорией с артефактами.
 * Структура: {cacheDir}/{platform}/tdm-{version}.{ext}
 *                                  metadata.json
 */
public class BuildCache {

    private static final Logger logger = LoggerFactory.getLogger(BuildCache.class);
    private static final String METADATA_FILE = "metadata.json";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final Path cacheRoot;

    public BuildCache(String cacheDir) {
        this.cacheRoot = Path.of(cacheDir.replace("${user.home}", System.getProperty("user.home")));
    }

    public Path platformDir(DesktopPlatform platform) {
        return cacheRoot.resolve(platform.getValue());
    }

    public Optional<BuildMetadata> loadMetadata(DesktopPlatform platform) {
        Path metaFile = platformDir(platform).resolve(METADATA_FILE);
        if (!Files.exists(metaFile)) {
            return Optional.empty();
        }
        try {
            BuildMetadata meta = objectMapper.readValue(metaFile.toFile(), BuildMetadata.class);
            logger.info("Прочитан кеш: version={}, file={}", meta.getResolvedVersion(), meta.getFilename());
            return Optional.of(meta);
        } catch (IOException e) {
            logger.warn("Не удалось прочитать metadata.json: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public void saveMetadata(DesktopPlatform platform, BuildMetadata metadata) throws IOException {
        Path dir = platformDir(platform);
        Files.createDirectories(dir);
        objectMapper.writerWithDefaultPrettyPrinter()
                .writeValue(dir.resolve(METADATA_FILE).toFile(), metadata);
        logger.info("Метаданные сохранены: {}", metadata.getFilename());
    }

    public Path artifactPath(DesktopPlatform platform, String filename) {
        return platformDir(platform).resolve(filename);
    }

    public boolean artifactExists(DesktopPlatform platform, String filename) {
        return Files.exists(artifactPath(platform, filename));
    }

    /** Проверяет, что кешированный артефакт актуален для запрошенной версии. */
    public boolean isCacheValid(DesktopPlatform platform, String requestedVersion, String remoteEtag) {
        Optional<BuildMetadata> meta = loadMetadata(platform);
        if (meta.isEmpty()) return false;

        BuildMetadata m = meta.get();
        if (!artifactExists(platform, m.getFilename())) {
            logger.info("Файл артефакта не найден на диске: {}", m.getFilename());
            return false;
        }
        if (!"latest".equalsIgnoreCase(requestedVersion)) {
            // Для конкретной версии — просто проверяем что version совпадает
            return requestedVersion.equals(m.getResolvedVersion());
        }
        // Для latest — сравниваем ETag, если он известен
        if (remoteEtag != null && !remoteEtag.isBlank()) {
            return remoteEtag.equals(m.getEtag());
        }
        // ETag недоступен — считаем кеш устаревшим и качаем заново
        logger.info("ETag для latest недоступен — кеш будет обновлён");
        return false;
    }

    public BuildMetadata buildMetadata(DesktopPlatform platform, String requestedVersion,
                                       String resolvedVersion, String filename,
                                       Path localPath, String etag) throws Exception {
        String sha256 = sha256(localPath);
        return BuildMetadata.builder()
                .platform(platform.getValue())
                .requestedVersion(requestedVersion)
                .resolvedVersion(resolvedVersion)
                .filename(filename)
                .sha256(sha256)
                .etag(etag != null ? etag : "")
                .downloadedAt(Instant.now().toString())
                .localPath(localPath.toString())
                .build();
    }

    private static String sha256(Path file) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.update(Files.readAllBytes(file));
        return String.format("%064x", new BigInteger(1, digest.digest()));
    }
}
