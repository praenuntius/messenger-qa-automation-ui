package org.foxtrot.mobile.core.application.capabilities;

public enum Capability {

    PLATFORM_VERSION("appium:platformVersion"),
    AUTOMATION_NAME("appium:automationName"),
    LANGUAGE("appium:language"),
    LOCALE("appium:locale"),
    FULL_RESET("appium:fullReset"),
    NO_RESET("appium:noReset"),
    DEVICE_NAME("appium:deviceName"),
    BUNDLE_ID("appium:bundleId"),
    AUTO_FILL_PASSWORDS("appium:autoFillPasswords"),
    REDUCE_MOTION("appium:reduceMotion"),
    REDUCE_TRANSPARENCY("appium:reduceTransparency"),
    TIMEZONE("appium:timeZone"),
    ALLOW_ALL_PERMISSIONS_ANDROID("appium:autoGrantPermissions"),
    APPIUM_SETTINGS_MAX_DEPTH("appium:settings[snapshotMaxDepth]"),
    DISABLE_ID_LOCATOR_AUTOCOMPLETE("appium:settings[disableIdLocatorAutocompletion]"),
    DISABLE_WINDOW_ANIMATION("appium:disableWindowAnimation"),
    CLEAR_SYSTEM_FILES("appium:clearSystemFiles"),
    SKIP_LOGCAT_CAPTURE("appium:skipLogcatCapture"),
    ADB_EXECUTION_TIMEOUT("appium:adbExecTimeout"),
    ENSURE_WEBVIEW_HAVE_PAGE("appium:ensureWebviewsHavePages"),
    WEBVIEW_TIMEOUT("appium:autoWebviewTimeout"),
    REMOTE_APPS_CACHE_LIMIT("appium:remoteAppsCacheLimit"),
    CONNECT_HARDWARE_KEYBOARD("appium:connectHardwareKeyboard"),
    RECREATE_CHROME_DRIVER_SESSIONS("appium:recreateChromeDriverSessions"),
    SHOW_CHROME_DRIVER_LOG("appium:showChromedriverLog"),
    WDA_LAUNCH_TIMEOUT("appium:wdaLaunchTimeout"),
    WDA_CONNECTION_TIMEOUT("appium:wdaConnectionTimeout"),
    USE_NEW_WDA("appium:useNewWDA"),
    USE_PREBUILT_WDA("appium:usePrebuiltWDA"),
    CLEAR_DEVICE_LOGS_ON_START("appium:clearDeviceLogsOnStart");

    private final String name;

    Capability(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
