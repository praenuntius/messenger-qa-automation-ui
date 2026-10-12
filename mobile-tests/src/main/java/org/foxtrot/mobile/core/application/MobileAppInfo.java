package org.foxtrot.mobile.core.application;

import org.aeonbits.owner.ConfigFactory;
import org.foxtrot.mobile.config.MobileConfig;

import java.nio.file.Files;
import java.nio.file.Path;

public final class MobileAppInfo {

    private static final MobileConfig CONFIG = ConfigFactory.create(MobileConfig.class);

    private static volatile String androidApplicationId;

    private MobileAppInfo() {
    }

    public static String getAndroidApplicationId() {
        if (androidApplicationId == null) {
            synchronized (MobileAppInfo.class) {
                if (androidApplicationId == null) {
                    androidApplicationId = ApkInfoReader.readApplicationId(getAndroidApkPath());
                }
            }
        }
        return androidApplicationId;
    }

    public static String androidResourceId(String shortId) {
        validateShortResourceId(shortId);
        return getAndroidApplicationId() + ":id/" + shortId;
    }

    private static Path getAndroidApkPath() {
        Path apkPath = Path.of(CONFIG.relativeApplicationPath(), CONFIG.androidApkFileName())
                .toAbsolutePath()
                .normalize();
        if (!Files.exists(apkPath)) {
            throw new IllegalStateException("APK файл не найден: " + apkPath);
        }
        return apkPath;
    }

    private static void validateShortResourceId(String shortId) {
        if (shortId == null || shortId.isBlank()) {
            throw new IllegalArgumentException("Resource id не должен быть пустым");
        }
        if (shortId.contains(":id/")) {
            throw new IllegalArgumentException("Передавай только короткий id без package: " + shortId);
        }
    }

    public static void resetCache() {
        androidApplicationId = null;
    }
}
