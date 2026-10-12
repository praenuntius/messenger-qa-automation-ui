package org.foxtrot.utils;

import com.codeborne.selenide.WebDriverRunner;
import io.qameta.allure.Step;
import org.openqa.selenium.logging.LogType;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ChatHistoryExportArchiveSteps {
    private static final Pattern ZIP_URL = Pattern.compile("https://[^\"\\\\]*\\.zip[^\"\\\\]*");

    @Step("Проверить структуру скачанного архива экспорта истории чата")
    public void verifyLatestDownloadedArchiveStructure() throws IOException {
        Path archive = downloadArchiveFromLogs();
        Path unpackedArchive = unzipArchive(archive);
        assertArchiveContains(unpackedArchive, ".html", "css", "fonts", "static");
    }

    @Step("Скачать архив экспорта истории чата по URL из network logs")
    private Path downloadArchiveFromLogs() throws IOException {
        String archiveUrl = WebDriverRunner.getWebDriver().manage().logs().get(LogType.PERFORMANCE)
                .getAll()
                .stream()
                .map(log -> ZIP_URL.matcher(log.getMessage()))
                .filter(matcher -> matcher.find())
                .map(matcher -> matcher.group())
                .reduce((first, second) -> second)
                .orElseThrow(() -> new IOException("Не найден URL архива Messages.zip в network logs"));

        Path archive = Paths.get("target", "chat-history-export", "downloads", "Messages-" + System.currentTimeMillis() + ".zip");
        Files.createDirectories(archive.getParent());
        try {
            HttpClient.newHttpClient().send(
                    HttpRequest.newBuilder(URI.create(archiveUrl)).GET().build(),
                    HttpResponse.BodyHandlers.ofFile(archive)
            );
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Скачивание архива было прервано", e);
        }
        return archive;
    }

    @Step("Распаковать архив экспорта истории чата")
    private Path unzipArchive(Path archive) throws IOException {
        Path outputDir = Paths.get("target", "chat-history-export", "checkingUnpackedArchive-" + System.currentTimeMillis());
        Files.createDirectories(outputDir);

        try (ZipInputStream zipInputStream = new ZipInputStream(Files.newInputStream(archive))) {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                Path entryPath = outputDir.resolve(entry.getName()).normalize();
                if (!entryPath.startsWith(outputDir)) {
                    throw new IOException("Archive entry is outside target dir: " + entry.getName());
                }

                if (entry.isDirectory()) {
                    Files.createDirectories(entryPath);
                } else {
                    Files.createDirectories(entryPath.getParent());
                    Files.copy(zipInputStream, entryPath);
                }
                zipInputStream.closeEntry();
            }
        }

        return outputDir;
    }

    @Step("Проверить наличие элементов архива: {expectedItems}")
    private void assertArchiveContains(Path directory, String... expectedItems) throws IOException {
        for (String expectedItem : expectedItems) {
            try (Stream<Path> paths = Files.walk(directory)) {
                boolean exists = paths.anyMatch(path -> {
                    String fileName = path.getFileName().toString();
                    if (expectedItem.startsWith(".")) {
                        return Files.isRegularFile(path)
                                && fileName.toLowerCase(Locale.ROOT).endsWith(expectedItem);
                    }
                    return Files.isDirectory(path) && fileName.equalsIgnoreCase(expectedItem);
                });
                assertTrue(exists, "В распакованном архиве должен быть элемент: " + expectedItem);
            }
        }
    }
}
