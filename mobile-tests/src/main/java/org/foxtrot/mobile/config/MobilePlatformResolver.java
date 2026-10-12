package org.foxtrot.mobile.config;

import org.foxtrot.mobile.core.application.PlatformName;
import org.foxtrot.mobile.core.application.devices.DeviceManager;

import java.util.Locale;

public class MobilePlatformResolver {

    private MobilePlatformResolver() {
    }

    public static String getPlatform() {
        return DeviceManager.getEnabledDevice()
                .getPlatform()
                .trim()
                .toUpperCase(Locale.ROOT);
    }

    public static boolean isAndroid() {
        return PlatformName.ANDROID.name().equalsIgnoreCase(getPlatform());
    }

    public static boolean isIos() {
        return PlatformName.IOS.name().equalsIgnoreCase(getPlatform());
    }
}
