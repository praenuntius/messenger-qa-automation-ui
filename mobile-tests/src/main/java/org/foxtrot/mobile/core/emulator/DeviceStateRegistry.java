package org.foxtrot.mobile.core.emulator;

import java.util.ArrayList;
import java.util.List;

public class DeviceStateRegistry {

    private static final ThreadLocal<List<String>> ANDROID_CONTACT_IDS = ThreadLocal.withInitial(ArrayList::new);

    private static final ThreadLocal<List<String>> ANDROID_MEDIA_PATHS = ThreadLocal.withInitial(ArrayList::new);

    private static final ThreadLocal<Boolean> IOS_DIRTY = ThreadLocal.withInitial(() -> false);

    public static void addAndroidContact(String rawContactId) {
        ANDROID_CONTACT_IDS.get().add(rawContactId);
    }

    public static void addAndroidMedia(String deviceFilePath) {
        ANDROID_MEDIA_PATHS.get().add(deviceFilePath);
    }

    public static void markIosDirty() {
        IOS_DIRTY.set(true);
    }

    public static List<String> androidContactIds() {
        return List.copyOf(ANDROID_CONTACT_IDS.get());
    }

    public static List<String> androidMediaPaths() {
        return List.copyOf(ANDROID_MEDIA_PATHS.get());
    }

    public static boolean isIosDirty() {
        return IOS_DIRTY.get();
    }

    public static boolean isDirty() {
        return !ANDROID_CONTACT_IDS.get().isEmpty() || !ANDROID_MEDIA_PATHS.get().isEmpty() || IOS_DIRTY.get();
    }

    public static void clear() {
        ANDROID_CONTACT_IDS.remove();
        ANDROID_MEDIA_PATHS.remove();
        IOS_DIRTY.remove();
    }
}
