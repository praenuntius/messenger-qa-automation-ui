package org.foxtrot.mobile.core.emulator;

import org.foxtrot.mobile.config.MobilePlatformResolver;
import org.foxtrot.mobile.core.application.PlatformName;
import org.foxtrot.mobile.core.driver.DriverManager;
import org.foxtrot.mobile.core.server.AppiumServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.foxtrot.mobile.utils.CommandLineManager.startCMDProcessIgnoreError;

public class DeviceCleanupManager {

    private static final Logger logger = LoggerFactory.getLogger(DeviceCleanupManager.class);

    public static void cleanDeviceState() {
        try {
            if (!DeviceStateRegistry.isDirty()) {
                return;
            }
            PlatformName platform = PlatformName.valueOf(MobilePlatformResolver.getPlatform());
            switch (platform) {
                case ANDROID -> cleanAndroid();
                case IOS -> cleanIos();
            }
        } finally {
            DeviceStateRegistry.clear();
        }
    }

    private static void cleanAndroid() {
        try {
            cleanAndroidContacts();
            cleanAndroidMedia();
        } catch (Exception e) {
            logger.error("Не удалось точечно очистить Android. Делаю wipe", e);
            DriverManager.quitDriver();
            EmulatorUtils.restartWipedCurrentDevice();
            AppiumServer.restart();
        }
    }

    private static void cleanAndroidContacts() {
        for (String rawContactId : DeviceStateRegistry.androidContactIds()) {
            if (rawContactId == null || rawContactId.isBlank()) {
                continue;
            }
            startCMDProcessIgnoreError(String.format(
                    "adb shell content delete --uri content://com.android.contacts/data --where \"raw_contact_id=%s\"",
                    rawContactId));
            startCMDProcessIgnoreError(String.format(
                    "adb shell content delete --uri content://com.android.contacts/raw_contacts --where \"_id=%s\"",
                    rawContactId));
            logger.info("Android контакт удалён. rawContactId: {}", rawContactId);
        }
        startCMDProcessIgnoreError("adb shell pm clear com.android.providers.contacts");
    }

    private static void cleanAndroidMedia() {
        for (String deviceFilePath : DeviceStateRegistry.androidMediaPaths()) {
            if (deviceFilePath == null || deviceFilePath.isBlank()) {
                continue;
            }
            String escapedPath = deviceFilePath.replace("'", "''");
            startCMDProcessIgnoreError(String.format("adb shell rm -f \"%s\"", deviceFilePath));
            startCMDProcessIgnoreError(String.format(
                    "adb shell content delete --uri content://media/external/images/media --where \"_data=\\'%s\\'\"",
                    escapedPath));
            startCMDProcessIgnoreError(String.format(
                    "adb shell content delete --uri content://media/external/video/media --where \"_data=\\'%s\\'\"",
                    escapedPath));
            startCMDProcessIgnoreError(String.format(
                    "adb shell content delete --uri content://media/external/file --where \"_data=\\'%s\\'\"",
                    escapedPath));
            startCMDProcessIgnoreError(String.format(
                    "adb shell content delete --uri content://media/external/downloads --where \"_data=\\'%s\\'\"",
                    escapedPath));
            startCMDProcessIgnoreError(
                    "adb shell am broadcast -a android.intent.action.MEDIA_SCANNER_SCAN_FILE -d file://" + deviceFilePath);
            logger.info("Android media удалена: {}", deviceFilePath);
        }
    }

    private static void cleanIos() {
        if (!DeviceStateRegistry.isIosDirty()) {
            return;
        }
        DriverManager.quitDriver();
        EmulatorUtils.closeEmulator(PlatformName.IOS);
        EmulatorUtils.startWipedEmulator();
        AppiumServer.restart();
    }
}
