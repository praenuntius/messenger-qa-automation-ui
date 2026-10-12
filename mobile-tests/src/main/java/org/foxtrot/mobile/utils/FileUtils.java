package org.foxtrot.mobile.utils;

import org.foxtrot.enums.DocumentFileExtension;
import org.foxtrot.enums.MediaFileExtension;
import org.foxtrot.mobile.core.application.ApplicationUtils;
import org.foxtrot.mobile.core.emulator.DeviceStateRegistry;
import org.foxtrot.mobile.steps.native_screens.FilesSteps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.stream.Stream;

import static org.foxtrot.mobile.config.MobilePlatformResolver.isAndroid;
import static org.foxtrot.mobile.config.MobilePlatformResolver.isIos;

public class FileUtils {

    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);
    private static final Path TEST_FILES_PATH = Path.of(System.getProperty("user.dir"),
            "../qa-core/src/main/resources/test-files").toAbsolutePath().normalize();
    private static final String ANDROID_DOCUMENTS_DEVICE_FOLDER = "/sdcard/Download";

    private FileUtils() {
    }

    public static void addMediaFile(MediaFileExtension mediaFileExtension) {
        Path mediaPath = resolveFileByExtension(
                TEST_FILES_PATH.resolve(mediaFileExtension.getResourceFolder()),
                mediaFileExtension.name().toLowerCase(Locale.ROOT));
        if (isAndroid()) {
            pushAndroidFile(mediaPath, mediaFileExtension.getAndroidDeviceFolder());
        } else if (isIos()) {
            CommandLineManager.startCMDProcessWithResponse("xcrun simctl addmedia booted \"" + mediaPath + "\"");
            DeviceStateRegistry.markIosDirty();
            logger.info("Файл добавлен в iOS медиатеку: {}", mediaPath);
        }
    }

    public static void addFile(DocumentFileExtension documentFileExtension) {
        Path filePath = resolveDocument(documentFileExtension);
        if (isAndroid()) {
            pushAndroidFile(filePath, ANDROID_DOCUMENTS_DEVICE_FOLDER);
        } else if (isIos()) {
            String fileUrl = filePath.toUri().toString();
            CommandLineManager.startCMDProcessWithResponse("xcrun simctl openurl booted \"" + fileUrl + "\"");
            new FilesSteps().clickSaveBtn();
            DeviceStateRegistry.markIosDirty();
            ApplicationUtils.launchApplication();
            logger.info("Файл добавлен в iOS Documents: {}", filePath.getFileName());
        }
    }

    private static Path resolveDocument(DocumentFileExtension extension) {
        Path filePath = TEST_FILES_PATH.resolve("documents").resolve(extension.getFileName());
        if (!Files.isRegularFile(filePath)) {
            throw new RuntimeException("Файл не найден: " + filePath);
        }
        return filePath.toAbsolutePath().normalize();
    }

    private static void pushAndroidFile(Path localPath, String deviceFolder) {
        String deviceFilePath = deviceFolder + "/" + localPath.getFileName();
        CommandLineManager.startCMDProcessWithResponse("adb shell mkdir -p \"" + deviceFolder + "\"");
        CommandLineManager.startCMDProcessWithResponse("adb push \"" + localPath + "\" \"" + deviceFilePath + "\"");
        CommandLineManager.startCMDProcessWithResponse("adb shell touch \"" + deviceFilePath + "\"");
        CommandLineManager.startCMDProcessIgnoreError(
                "adb shell am broadcast -a android.intent.action.MEDIA_SCANNER_SCAN_FILE -d file://" + deviceFilePath);
        DeviceStateRegistry.addAndroidMedia(deviceFilePath);
        logger.info("Файл добавлен на Android: {}", deviceFilePath);
    }

    private static Path resolveFileByExtension(Path folder, String extension) {
        try (Stream<Path> files = Files.list(folder)) {
            return files.filter(Files::isRegularFile)
                    .filter(path -> path.toString().toLowerCase(Locale.ROOT).endsWith("." + extension))
                    .findFirst()
                    .map(Path::toAbsolutePath)
                    .map(Path::normalize)
                    .orElseThrow(() -> new RuntimeException(
                            "Файл с расширением ." + extension + " не найден в папке: " + folder));
        } catch (IOException e) {
            throw new RuntimeException("Не удалось прочитать папку с файлами: " + folder, e);
        }
    }
}
