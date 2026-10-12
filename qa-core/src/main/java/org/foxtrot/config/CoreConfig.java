package org.foxtrot.config;

import org.aeonbits.owner.Config;

@Config.LoadPolicy(Config.LoadType.MERGE)
@Config.Sources({
        "system:properties",
        "system:env",
        "classpath:config.properties"
})
public interface CoreConfig extends Config {

    @Key("baseApiUrl")
    String baseApiUrl();

    @Key("pusherBaseUrl")
    @DefaultValue("https://pusher-preprod.domain.net")
    String pusherBaseUrl();
}