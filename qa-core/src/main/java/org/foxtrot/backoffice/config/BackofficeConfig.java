package org.foxtrot.backoffice.config;

import org.aeonbits.owner.Config;

@Config.LoadPolicy(Config.LoadType.MERGE)
@Config.Sources({
        "system:properties",
        "system:env",
        "classpath:backoffice.properties",
        "classpath:config.properties"
})
public interface BackofficeConfig extends Config {

    @Key("backofficeApiUrl")
    @DefaultValue("https://backoffice-preprod.domain.net")
    String backofficeApiUrl();

    /** Опционально: ручной JSESSIONID. Если пусто — логин суперадмина по phone/otp. */
    @Key("backofficeSessionId")
    String backofficeSessionId();

    @Key("backofficeSuperAdminPhone")
    @DefaultValue("9101234567")
    String backofficeSuperAdminPhone();

    @Key("backofficeSuperAdminOtp")
    @DefaultValue("111111")
    String backofficeSuperAdminOtp();

    @Key("backofficeOrganizationId")
    @DefaultValue("128644")
    long backofficeOrganizationId();

    @Key("backofficeWhitelistInitiator")
    @DefaultValue("qa-automation")
    String backofficeWhitelistInitiator();
}
