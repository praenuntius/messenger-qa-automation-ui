package org.foxtrot.mobile.config;

import org.aeonbits.owner.Config;

@Config.LoadPolicy(Config.LoadType.MERGE)
@Config.Sources({
        "system:properties",
        "system:env",
        "classpath:mobile.properties"
})
public interface MobileConfig extends Config {

    @Key("explicit.wait.seconds")
    int explicitWaitSeconds();

    @Key("relative.devices.path")
    String relativeDevicesPath();

    @Key("relative.application.path")
    String relativeApplicationPath();

    @Key("ios.bundle.id")
    String iosBundleId();

    @Key("ios.app.file.name")
    String iosAppFileName();

    @Key("android.apk.file.name")
    String androidApkFileName();

    @Key("appium.server.ip")
    String appiumServerIp();

    @Key("appium.server.port")
    int appiumServerPort();

    @Key("mobile.network.proxy.port")
    int networkProxyPort();

    @Key("mobile.network.mitmdump.path")
    String networkMitmdumpPath();
}
