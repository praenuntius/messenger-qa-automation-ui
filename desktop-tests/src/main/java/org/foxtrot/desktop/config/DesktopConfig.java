package org.foxtrot.desktop.config;

import org.aeonbits.owner.Config;

@Config.LoadPolicy(Config.LoadType.MERGE)
@Config.Sources({
        "system:properties",
        "system:env",
        "classpath:desktop.properties"
})
public interface DesktopConfig extends Config {

    @Key("desktop.platform")
    String platform();

    /**
     * UI-язык клиента (ru сейчас, en позже).
     * Не детектится с ОС — всегда берётся из конфига.
     */
    @Key("desktop.locale")
    @DefaultValue("ru")
    String locale();

    @Key("desktop.app.version")
    String appVersion();

    @Key("desktop.build.base.url")
    String buildBaseUrl();

    @Key("desktop.build.cache.dir")
    String buildCacheDir();

    @Key("desktop.app.startup.timeout.seconds")
    @DefaultValue("30")
    int appStartupTimeoutSeconds();

    @Key("desktop.cdp.port.base")
    @DefaultValue("9222")
    int cdpPortBase();

    @Key("desktop.app.base.url")
    String appBaseUrl();

    /**
     * Версия Chromium для подбора ChromeDriver при CDP-подключении.
     * Пусто — определяется автоматически через CDP /json/version.
     */
    @Key("desktop.chromedriver.browser.version")
    @DefaultValue("")
    String chromeDriverBrowserVersion();

    /** Путь к локальному chromedriver (если не задан — скачивается из Chrome for Testing). */
    @Key("desktop.chromedriver.path")
    @DefaultValue("")
    String chromeDriverPath();

    @Key("desktop.chromedriver.cache.dir")
    @DefaultValue("${user.home}/.qa-desktop-cache/chromedriver")
    String chromeDriverCacheDir();
}
