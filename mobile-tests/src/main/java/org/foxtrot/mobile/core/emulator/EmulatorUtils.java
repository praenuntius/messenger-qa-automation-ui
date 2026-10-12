package org.foxtrot.mobile.core.emulator;

import org.foxtrot.mobile.config.MobilePlatformResolver;
import org.foxtrot.mobile.core.application.PlatformName;
import org.foxtrot.mobile.core.application.capabilities.CapabilitiesCreator;
import org.foxtrot.mobile.core.application.devices.DeviceManager;
import org.foxtrot.utils.Waits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.foxtrot.mobile.utils.CommandLineManager.startCMDProcessIgnoreError;
import static org.foxtrot.mobile.utils.CommandLineManager.startCMDProcessWithResponse;

public class EmulatorUtils {

    private static final Logger logger = LoggerFactory.getLogger(EmulatorUtils.class);

    public static void startWipedEmulator() {
        logger.info("Запускаю очищенный эмулятор/симулятор");
        PlatformName platform = PlatformName.valueOf(MobilePlatformResolver.getPlatform());
        switch (platform) {
            case ANDROID -> startWipedAndroidEmulator();
            case IOS -> startWipedIosSimulator();
        }
    }

    public static void restartWipedCurrentDevice() {
        PlatformName platform = PlatformName.valueOf(MobilePlatformResolver.getPlatform());
        closeEmulator(platform);
        Waits.sleepSeconds(2);
        startWipedEmulator();
    }

    private static void startWipedAndroidEmulator() {
        String avdName = DeviceManager.getEnabledDevice().getDeviceName();
        String command = String.format("emulator -avd \"%s\" -wipe-data %s > /tmp/android-emulator.log 2>&1 &", avdName,
                CapabilitiesCreator.AVD_ARGS_EMULATOR_CAPS.trim());
        startCMDProcessWithResponse(command);
        waitForEmulatorReady(PlatformName.ANDROID, avdName);
    }

    private static void startWipedIosSimulator() {
        String udid = getIosSimulatorUdid();
        startCMDProcessIgnoreError("xcrun simctl shutdown " + udid);
        startCMDProcessWithResponse("xcrun simctl erase " + udid);
        startCMDProcessWithResponse("xcrun simctl boot " + udid);
        waitForEmulatorReady(PlatformName.IOS, udid);
    }

    public static void closeEmulator(PlatformName platform) {
        logger.info("Закрываю эмулятор для {}", platform);
        String command = switch (platform) {
            case ANDROID -> "adb emu kill";
            case IOS -> "xcrun simctl shutdown all";
        };
        startCMDProcessIgnoreError(command);
    }

    private static void waitForEmulatorReady(PlatformName platform, String deviceNameOrUdid) {
        for (int attempt = 1; attempt <= 45; attempt++) {
            String output = startCMDProcessIgnoreError(
                    switch (platform) {
                        case ANDROID -> "adb shell getprop sys.boot_completed";
                        case IOS -> "xcrun simctl list devices booted | grep '" + deviceNameOrUdid + "'";
                    }
            ).trim();
            if (platform == PlatformName.ANDROID && output.equals("1")) {
                logger.info("Android эмулятор '{}' готов", deviceNameOrUdid);
                return;
            }
            if (platform == PlatformName.IOS && !output.isEmpty()) {
                logger.info("iOS Simulator '{}' готов", deviceNameOrUdid);
                return;
            }
            Waits.sleepSeconds(1);
        }
        throw new IllegalStateException("Эмулятор/симулятор '%s' не поднялся".formatted(deviceNameOrUdid));
    }

    public static String getIosSimulatorUdid() {
        String deviceName = DeviceManager.getEnabledDevice().getDeviceName();
        String udid = startCMDProcessWithResponse(String.format(
                "xcrun simctl list devices available | grep -m 1 '^    %s (' | grep -E -o '[0-9A-Fa-f-]{36}'",
                deviceName
        )).trim();
        if (udid.isBlank()) {
            throw new IllegalStateException("Не найден iOS Simulator с deviceName: " + deviceName);
        }
        return udid;
    }
}