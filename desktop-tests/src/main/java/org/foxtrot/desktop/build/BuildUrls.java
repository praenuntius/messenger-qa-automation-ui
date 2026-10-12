package org.foxtrot.desktop.build;

import org.foxtrot.desktop.config.DesktopPlatform;

/**
 * Формирует URL для скачивания артефакта с S3.
 * <p>
 * Схемы:
 *   latest:    {base}/tdm.latest.{ext}
 *   versioned: {base}/tdm-{version}.beta.{ext}
 */
public class BuildUrls {

    private BuildUrls() {
    }

    public static String forVersion(String baseUrl, String version, DesktopPlatform platform) {
        String ext = platform.getExt();
        if ("latest".equalsIgnoreCase(version)) {
            return baseUrl + "/tdm.latest." + ext;
        }
        return baseUrl + "/tdm-" + version + ".beta." + ext;
    }
}
