package org.foxtrot.enums;

public enum MediaFileExtension {
    PNG("images", "/sdcard/Pictures"),
    JPG("images", "/sdcard/Pictures"),
    GIF("images", "/sdcard/Pictures"),
    MP4("videos", "/sdcard/Movies"),
    MOV("videos", "/sdcard/Movies");

    private final String resourceFolder;
    private final String androidDeviceFolder;

    MediaFileExtension(String resourceFolder, String androidDeviceFolder) {
        this.resourceFolder = resourceFolder;
        this.androidDeviceFolder = androidDeviceFolder;
    }

    public String getResourceFolder() {
        return resourceFolder;
    }

    public String getAndroidDeviceFolder() {
        return androidDeviceFolder;
    }
}
