package org.foxtrot.mobile.core.application;

import io.appium.java_client.InteractsWithApps;
import io.appium.java_client.appmanagement.ApplicationState;
import org.aeonbits.owner.ConfigFactory;
import org.foxtrot.mobile.config.MobileConfig;
import org.foxtrot.mobile.config.MobilePlatformResolver;
import org.foxtrot.mobile.core.driver.DriverManager;
import org.openqa.selenium.WebDriverException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;

import static org.foxtrot.mobile.config.MobilePlatformResolver.isAndroid;
import static org.foxtrot.mobile.config.MobilePlatformResolver.isIos;
import static org.foxtrot.mobile.utils.CommandLineManager.startCMDProcessIgnoreError;
import static org.foxtrot.mobile.utils.CommandLineManager.startCMDProcessWithResponse;

public class ApplicationUtils {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationUtils.class);
    private static final MobileConfig config = ConfigFactory.create(MobileConfig.class);

    public static void prepareApplication() {
        installApplicationIfNeeded();
        grantPermissions();
    }

    public static void launchApplication() {
        String appId = getApplicationId();
        if (getInteractsWithApps().queryAppState(appId) == ApplicationState.NOT_INSTALLED) {
            installApplication();
            grantPermissions();
        }
        try {
            getInteractsWithApps().activateApp(appId);
        } catch (WebDriverException exception) {
            logger.warn("Не удалось активировать приложение {}. Пробую переустановить", appId, exception);
            installApplication();
            grantPermissions();
            getInteractsWithApps().activateApp(appId);
        }
    }

    /**
     * Уводит приложение в background на {@code duration}, затем возвращает управление драйверу.
     * Для SKR lifecycle: SSE обычно рвётся, ключ может протухнуть по TTL.
     */
    public static void backgroundApplication(java.time.Duration duration) {
        long seconds = Math.max(1L, duration.getSeconds());
        logger.info("Увожу приложение в background на {} с", seconds);
        try {
            DriverManager.getDriver().executeScript("mobile: backgroundApp", Map.of("seconds", seconds));
        } catch (WebDriverException e) {
            logger.warn("mobile: backgroundApp failed, fallback HOME + sleep: {}", e.getMessage());
            try {
                if (isAndroid()) {
                    startCMDProcessIgnoreError("adb shell input keyevent KEYCODE_HOME");
                } else if (isIos()) {
                    DriverManager.getDriver().executeScript("mobile: pressButton", Map.of("name", "home"));
                }
            } catch (Exception homeError) {
                logger.warn("HOME fallback failed: {}", homeError.getMessage());
            }
            try {
                Thread.sleep(seconds * 1000L);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(ie);
            }
        }
    }

    /** Возвращает приложение на передний план (после background / HOME). */
    public static void foregroundApplication() {
        String appId = getApplicationId();
        logger.info("Возвращаю приложение на передний план: {}", appId);
        getInteractsWithApps().activateApp(appId);
    }

    /**
     * Terminate + activate без wipe. Снимает iOS overlay, который прячет TabBar
     * ({@code visible=false}), не разлогинивая пользователя.
     */
    public static void relaunchApplicationKeepingData() {
        logger.info("Перезапускаю приложение без очистки данных");
        closeApplication();
        try {
            Thread.sleep(800);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
        launchApplication();
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    public static void resetApplicationAfterTest() {
        closeApplication();
        clearApplicationData();
    }

    public static void resetApplicationSession() {
        resetApplicationAfterTest();
        prepareApplication();
        launchApplication();
    }

    private static void closeApplication() {
        if (!DriverManager.hasDriver()) {
            logger.info("Driver не создан, закрытие приложения пропущено");
            return;
        }
        try {
            getInteractsWithApps().terminateApp(getApplicationId());
        } catch (WebDriverException e) {
            logger.warn("Не удалось закрыть приложение", e);
        }
    }

    private static void installApplicationIfNeeded() {
        if (isApplicationInstalled()) {
            logger.info("Приложение уже установлено, пропускаю install");
            return;
        }
        installApplication();
    }

    private static void installApplication() {
        logger.info("Устанавливаю приложение");
        PlatformName platform = PlatformName.valueOf(MobilePlatformResolver.getPlatform());
        String fileName = switch (platform) {
            case ANDROID -> config.androidApkFileName();
            case IOS -> config.iosAppFileName();
        };
        Path applicationPath = Path.of(config.relativeApplicationPath(), fileName).toAbsolutePath().normalize();
        if (!Files.exists(applicationPath)) {
            throw new RuntimeException("Файл приложения не найден: " + applicationPath);
        }
        String command = switch (platform) {
            case ANDROID -> String.format("adb install \"%s\"", applicationPath);
            case IOS -> String.format("xcrun simctl install booted \"%s\"", applicationPath);
        };
        startCMDProcessWithResponse(command);
    }

    private static void deleteApplication() {
        logger.info("Удаляю приложение");
        PlatformName platform = PlatformName.valueOf(MobilePlatformResolver.getPlatform());
        String command = switch (platform) {
            case ANDROID -> String.format("adb uninstall %s", MobileAppInfo.getAndroidApplicationId());
            case IOS -> String.format("xcrun simctl uninstall booted %s", config.iosBundleId());
        };
        startCMDProcessWithResponse(command);
    }

    private static void clearApplicationData() {
        logger.info("Сбрасываю состояние приложения");
        PlatformName platform = PlatformName.valueOf(MobilePlatformResolver.getPlatform());
        switch (platform) {
            case ANDROID -> clearAndroidApplicationData();
            case IOS -> clearIosApplicationData();
        }
    }

    private static void clearAndroidApplicationData() {
        String appPackage = MobileAppInfo.getAndroidApplicationId();
        try {
            startCMDProcessIgnoreError("adb shell am force-stop " + appPackage);
            String output = startCMDProcessWithResponse("adb shell pm clear " + appPackage);
            if (!output.contains("Success")) {
                throw new RuntimeException("Не удалось очистить Android приложение. Output: " + output);
            }
            logger.info("Данные Android приложения очищены");
        } catch (Exception clearException) {
            logger.error("Не удалось очистить Android приложение. ", clearException);
            deleteApplication();
        }
    }

    private static void clearIosApplicationData() {
        String bundleId = config.iosBundleId();
        try {
            DriverManager.getDriver().executeScript("mobile: clearKeychains");
            DriverManager.getDriver().executeScript("mobile: clearApp", Map.of("bundleId", bundleId));
            logger.info("Данные iOS приложения очищены");
        } catch (Exception clearException) {
            logger.error("Не удалось очистить iOS приложение. ", clearException);
            deleteApplication();
        }
    }

    private static void grantPermissions() {
        if (isAndroid()) {
            String appPackage = MobileAppInfo.getAndroidApplicationId();
            startCMDProcessWithResponse("adb shell pm grant " + appPackage + " android.permission.READ_CONTACTS");
            startCMDProcessWithResponse("adb shell pm grant " + appPackage + " android.permission.WRITE_CONTACTS");
            startCMDProcessWithResponse("adb shell pm grant " + appPackage + " android.permission.GET_ACCOUNTS");
            startCMDProcessWithResponse("adb shell pm grant " + appPackage + " android.permission.POST_NOTIFICATIONS");
            startCMDProcessWithResponse("adb shell pm grant " + appPackage + " android.permission.READ_EXTERNAL_STORAGE");
            startCMDProcessWithResponse("adb shell pm grant " + appPackage + " android.permission.WRITE_EXTERNAL_STORAGE");
            startCMDProcessWithResponse("adb shell pm grant " + appPackage + " android.permission.READ_MEDIA_IMAGES");
            startCMDProcessWithResponse("adb shell pm grant " + appPackage + " android.permission.READ_MEDIA_VIDEO");
            startCMDProcessWithResponse("adb shell pm grant " + appPackage + " android.permission.READ_MEDIA_VISUAL_USER_SELECTED");
            startCMDProcessWithResponse("adb shell pm grant " + appPackage + " android.permission.CAMERA");
            startCMDProcessIgnoreError("adb shell settings put global heads_up_notifications_enabled 0");
            enableAppLinks(appPackage);
            logger.info("Разрешения Android выданы для {}", appPackage);
        } else if (isIos()) {
            String bundleId = config.iosBundleId();
            startCMDProcessIgnoreError("xcrun simctl terminate booted " + bundleId);
            startCMDProcessWithResponse("xcrun simctl privacy booted reset all " + bundleId);
            startCMDProcessWithResponse("xcrun simctl privacy booted grant contacts " + bundleId);
            startCMDProcessWithResponse("xcrun simctl privacy booted grant photos " + bundleId);
            startCMDProcessWithResponse("xcrun simctl privacy booted grant photos-add " + bundleId);
            startCMDProcessWithResponse("xcrun simctl privacy booted grant media-library " + bundleId);
            startCMDProcessWithResponse("xcrun simctl privacy booted grant camera " + bundleId);
            startCMDProcessIgnoreError("xcrun simctl privacy booted grant notifications " + bundleId);
            logger.info("Разрешения iOS выданы для {}", bundleId);
        }
    }

    private static void enableAppLinks(String appPackage) {
        startCMDProcessWithResponse(
                "adb shell pm set-app-links-allowed --user cur --package " + appPackage + " true");
        startCMDProcessWithResponse("adb shell pm set-app-links --package " + appPackage + " 0 all");
        logger.info("App Links включены для {}", appPackage);
    }

    private static boolean isApplicationInstalled() {
        if (isAndroid()) {
            String appPackage = MobileAppInfo.getAndroidApplicationId();
            String output = startCMDProcessIgnoreError("adb shell pm list packages " + appPackage);
            return output.contains("package:" + appPackage);
        }
        if (isIos()) {
            String bundleId = config.iosBundleId();
            String output = startCMDProcessIgnoreError("xcrun simctl get_app_container booted " + bundleId + " app");
            if (output.isBlank()) {
                return false;
            }
            String trimmedOutput = output.trim();
            String lowerOutput = trimmedOutput.toLowerCase(Locale.ROOT);
            return trimmedOutput.startsWith("/") && !lowerOutput.contains("no such file")
                    && !lowerOutput.contains("not found") && !lowerOutput.contains("error");
        }
        return false;
    }

    private static String getApplicationId() {
        PlatformName platform = PlatformName.valueOf(MobilePlatformResolver.getPlatform());
        return switch (platform) {
            case ANDROID -> MobileAppInfo.getAndroidApplicationId();
            case IOS -> config.iosBundleId();
        };
    }

    private static InteractsWithApps getInteractsWithApps() {
        return (InteractsWithApps) DriverManager.getDriver();
    }
}
