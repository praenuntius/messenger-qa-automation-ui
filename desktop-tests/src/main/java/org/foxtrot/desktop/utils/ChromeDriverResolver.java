package org.foxtrot.desktop.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.nio.file.attribute.PosixFilePermission;
import java.time.Duration;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Скачивает и кеширует ChromeDriver из Chrome for Testing (milestones API).
 * Нужен, когда Selenium Manager не находит драйвер для Chromium внутри Electron.
 */
public final class ChromeDriverResolver {

    private static final Logger logger = LoggerFactory.getLogger(ChromeDriverResolver.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String MILESTONES_URL =
            "https://googlechromelabs.github.io/chrome-for-testing/latest-versions-per-milestone-with-downloads.json";
    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(30))
            .followRedirects(HttpClient.Redirect.ALWAYS)
            .build();

    private static final DesktopConfig config = ConfigFactory.create(DesktopConfig.class);

    private ChromeDriverResolver() {
    }

    public static Path resolveExecutable(String chromiumVersion) {
        String manualPath = config.chromeDriverPath();
        if (manualPath != null && !manualPath.isBlank()) {
            Path path = Path.of(manualPath.trim().replace("${user.home}", System.getProperty("user.home")));
            if (Files.isExecutable(path)) {
                logger.info("Использую ChromeDriver из desktop.chromedriver.path: {}", path);
                return path;
            }
            logger.warn(
                    "desktop.chromedriver.path задан ({}), но файл не найден или не исполняемый — скачаю автоматически",
                    path);
        }

        int milestone = majorVersion(chromiumVersion);
        String platform = chromeForTestingPlatform();
        Path cacheRoot = cacheRoot();
        Path cached = cacheRoot.resolve("milestone-" + milestone).resolve(platform).resolve("chromedriver");
        if (Files.isExecutable(cached)) {
            logger.info("ChromeDriver из кеша: {}", cached);
            return cached;
        }

        return downloadAndCache(chromiumVersion, milestone, platform, cacheRoot, cached);
    }

    private static Path downloadAndCache(String chromiumVersion, int milestone, String platform,
                                         Path cacheRoot, Path targetExecutable) {
        logger.info("Скачиваю ChromeDriver для milestone={}, platform={}, electron={}",
                milestone, platform, chromiumVersion);

        String downloadUrl = findDownloadUrl(milestone, platform);
        Path zipPath = cacheRoot.resolve("milestone-" + milestone).resolve(platform).resolve("chromedriver.zip");

        try {
            Files.createDirectories(zipPath.getParent());
            downloadFile(downloadUrl, zipPath);
            extractChromedriver(zipPath, targetExecutable);
            makeExecutable(targetExecutable);
            logger.info("ChromeDriver сохранён: {}", targetExecutable);
            return targetExecutable;
        } catch (IOException | InterruptedException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw new IllegalStateException(
                    "Не удалось скачать ChromeDriver для Chromium " + chromiumVersion
                            + " (milestone " + milestone + ", platform " + platform + ")", e);
        }
    }

    private static String findDownloadUrl(int milestone, String platform) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(MILESTONES_URL))
                    .timeout(Duration.ofSeconds(30))
                    .GET()
                    .build();
            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new IllegalStateException("Milestones API HTTP " + response.statusCode());
            }

            JsonNode milestoneNode = MAPPER.readTree(response.body())
                    .path("milestones")
                    .path(String.valueOf(milestone));
            if (milestoneNode.isMissingNode()) {
                throw new IllegalStateException(
                        "Chrome for Testing не содержит milestone " + milestone
                                + ". Задайте desktop.chromedriver.path вручную.");
            }

            for (JsonNode item : milestoneNode.path("downloads").path("chromedriver")) {
                if (platform.equals(item.path("platform").asText())) {
                    return item.path("url").asText();
                }
            }
            throw new IllegalStateException(
                    "Нет chromedriver для milestone " + milestone + " и platform " + platform);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Прерван запрос Chrome for Testing milestones API", e);
        } catch (IOException e) {
            throw new IllegalStateException("Ошибка запроса Chrome for Testing milestones API", e);
        }
    }

    private static void downloadFile(String url, Path destination) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofMinutes(5))
                .GET()
                .build();
        HttpResponse<InputStream> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofInputStream());
        if (response.statusCode() != 200) {
            throw new IOException("Скачивание ChromeDriver вернуло HTTP " + response.statusCode() + ": " + url);
        }
        Files.copy(response.body(), destination, StandardCopyOption.REPLACE_EXISTING);
    }

    private static void extractChromedriver(Path zipPath, Path targetExecutable) throws IOException {
        try (ZipInputStream zip = new ZipInputStream(Files.newInputStream(zipPath))) {
            ZipEntry entry;
            while ((entry = zip.getNextEntry()) != null) {
                if (!entry.getName().endsWith("/chromedriver") && !entry.getName().endsWith("\\chromedriver")
                        && !entry.getName().endsWith("chromedriver.exe")) {
                    continue;
                }
                Files.createDirectories(targetExecutable.getParent());
                Files.copy(zip, targetExecutable, StandardCopyOption.REPLACE_EXISTING);
                return;
            }
        }
        throw new IOException("В архиве ChromeDriver не найден бинарник chromedriver: " + zipPath);
    }

    private static void makeExecutable(Path executable) throws IOException {
        if (!Files.getFileStore(executable).supportsFileAttributeView("posix")) {
            return;
        }
        Files.setPosixFilePermissions(executable, Set.of(
                PosixFilePermission.OWNER_READ,
                PosixFilePermission.OWNER_WRITE,
                PosixFilePermission.OWNER_EXECUTE,
                PosixFilePermission.GROUP_READ,
                PosixFilePermission.GROUP_EXECUTE,
                PosixFilePermission.OTHERS_READ,
                PosixFilePermission.OTHERS_EXECUTE
        ));
    }

    private static int majorVersion(String chromiumVersion) {
        String[] parts = chromiumVersion.split("\\.");
        if (parts.length == 0 || parts[0].isBlank()) {
            throw new IllegalArgumentException("Некорректная версия Chromium: " + chromiumVersion);
        }
        return Integer.parseInt(parts[0]);
    }

    private static Path cacheRoot() {
        String configured = config.chromeDriverCacheDir();
        String dir = (configured == null || configured.isBlank())
                ? System.getProperty("user.home") + "/.qa-desktop-cache/chromedriver"
                : configured.replace("${user.home}", System.getProperty("user.home"));
        return Path.of(dir);
    }

    private static String chromeForTestingPlatform() {
        DesktopPlatform platform = DesktopPlatformResolver.getPlatform();
        String arch = System.getProperty("os.arch", "").toLowerCase();
        boolean arm = arch.contains("aarch64") || arch.contains("arm");

        return switch (platform) {
            case MAC -> arm ? "mac-arm64" : "mac-x64";
            case LINUX -> arm ? "linux-arm64" : "linux64";
            case WINDOWS -> arm ? "win-arm64" : "win64";
        };
    }
}
