package org.foxtrot.mobile.core.application.capabilities;

import org.foxtrot.mobile.core.application.PlatformName;
import org.foxtrot.mobile.core.application.devices.DeviceManager;
import org.foxtrot.mobile.pojo.DeviceCapabilities;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

public class CapabilitiesCreator {

    private static final DeviceCapabilities enabledDevice = DeviceManager.getEnabledDevice();
    public static final String AVD_ARGS_EMULATOR_CAPS = "-no-audio " // отключить звук
            + "-no-boot-anim " // отключить boot-анимацию
            + "-netfast " // быстрый network режим
            + "-camera-back none " // выключить заднюю камеру
            + "-camera-front none " // выключить фронт. камеру
            + "-no-snapshot " // не использовать snapshot
            + "-no-snapshot-save " // не сохранять snapshot
            + "-no-snapshot-load " // не загружать snapshot
            + "-gpu swiftshader_indirect " // включение программного рендеринга вместо графического
            + "-no-skin "; // отключить скин эмулятора

    private CapabilitiesCreator() {
    }

    public static DesiredCapabilities getCapabilities() {
        DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
        desiredCapabilities.setCapability(CapabilityType.PLATFORM_NAME, enabledDevice.getPlatform().toLowerCase());
        desiredCapabilities.setCapability(Capability.PLATFORM_VERSION.getName(), enabledDevice.getPlatformVersion());
        desiredCapabilities.setCapability(Capability.AUTOMATION_NAME.getName(), enabledDevice.getDriverName());
        desiredCapabilities.setCapability(Capability.LANGUAGE.getName(), enabledDevice.getLanguage());
        desiredCapabilities.setCapability(Capability.LOCALE.getName(), enabledDevice.getLocale());
        desiredCapabilities.setCapability(Capability.FULL_RESET.getName(), false);
        desiredCapabilities.setCapability(Capability.NO_RESET.getName(), true);
        desiredCapabilities.setCapability(Capability.CLEAR_SYSTEM_FILES.getName(), true);
        desiredCapabilities.merge(platformSpecificCapabilities());
        return desiredCapabilities;
    }

    private static DesiredCapabilities platformSpecificCapabilities() {
        DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
        if (enabledDevice.getPlatform().equalsIgnoreCase(PlatformName.ANDROID.name())) {
            desiredCapabilities.setCapability(Capability.ALLOW_ALL_PERMISSIONS_ANDROID.getName(), true);
            desiredCapabilities.setCapability(Capability.TIMEZONE.getName(), "Europe/Moscow");
            desiredCapabilities.setCapability(Capability.DISABLE_WINDOW_ANIMATION.getName(), true);
            desiredCapabilities.setCapability(Capability.SKIP_LOGCAT_CAPTURE.getName(), true);
            desiredCapabilities.setCapability(Capability.WEBVIEW_TIMEOUT.getName(), 15000);
            desiredCapabilities.setCapability(Capability.REMOTE_APPS_CACHE_LIMIT.getName(), 20);
            desiredCapabilities.setCapability(Capability.ADB_EXECUTION_TIMEOUT.getName(), 60000);
            desiredCapabilities.setCapability(Capability.ENSURE_WEBVIEW_HAVE_PAGE.getName(), true);
            desiredCapabilities.setCapability(Capability.RECREATE_CHROME_DRIVER_SESSIONS.getName(), true);
            desiredCapabilities.setCapability(Capability.SHOW_CHROME_DRIVER_LOG.getName(), true);
            desiredCapabilities.setCapability(Capability.DISABLE_ID_LOCATOR_AUTOCOMPLETE.getName(), true);
            desiredCapabilities.setCapability(Capability.CLEAR_DEVICE_LOGS_ON_START.getName(), true);
        }
        if (enabledDevice.getPlatform().equalsIgnoreCase(PlatformName.IOS.name())) {
            String deviceName = enabledDevice.getDeviceName();
            desiredCapabilities.setCapability(Capability.DEVICE_NAME.getName(), deviceName);
            desiredCapabilities.setCapability(Capability.AUTO_FILL_PASSWORDS.getName(), false);
            desiredCapabilities.setCapability(Capability.REDUCE_MOTION.getName(), true);
            desiredCapabilities.setCapability(Capability.REDUCE_TRANSPARENCY.getName(), true);
            desiredCapabilities.setCapability(Capability.APPIUM_SETTINGS_MAX_DEPTH.getName(), 60);
            desiredCapabilities.setCapability(Capability.CONNECT_HARDWARE_KEYBOARD.getName(), false);
            desiredCapabilities.setCapability(Capability.WDA_LAUNCH_TIMEOUT.getName(), 300000);
            desiredCapabilities.setCapability(Capability.WDA_CONNECTION_TIMEOUT.getName(), 300000);
            desiredCapabilities.setCapability(Capability.USE_NEW_WDA.getName(), true);
            desiredCapabilities.setCapability(Capability.USE_PREBUILT_WDA.getName(), false);
        }
        return desiredCapabilities;
    }
}
