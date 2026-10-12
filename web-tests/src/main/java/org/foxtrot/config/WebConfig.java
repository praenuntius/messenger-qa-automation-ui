package org.foxtrot.config;

import org.aeonbits.owner.Config;

@Config.LoadPolicy(Config.LoadType.MERGE)
@Config.Sources({
        "system:properties",
        "system:env",
        "classpath:web.properties"
})
public interface WebConfig extends Config {

    @Key("baseUrl")
    String baseUrl();

    @Key("browser")
    String browser();

    @Key("browserVersion")
    String browserVersion();

    @Key("selenoidHubUrl")
    String selenoidHubUrl();

  /** Канал запуска: web (браузер) или desktop (Electron + CDP). См. docs/TEST_PARAMETERS.md */
    @Key("run.channel")
    @DefaultValue("web")
    String runChannel();
}