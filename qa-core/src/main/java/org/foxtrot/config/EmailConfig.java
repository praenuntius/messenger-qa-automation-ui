package org.foxtrot.config;

import org.aeonbits.owner.Config;

@Config.LoadPolicy(Config.LoadType.MERGE)
@Config.Sources({
        "system:properties",
        "system:env",
        "classpath:email.local.properties",
        "classpath:email.properties"
})
public interface EmailConfig extends Config {

    @Key("imap.host")
    @DefaultValue("imap.gmail.com")
    String imapHost();

    @Key("imap.port")
    @DefaultValue("993")
    int imapPort();

    @Key("imap.user")
    String imapUser();

    @Key("imap.password")
    String imapPassword();

    @Key("email.address")
    String emailAddress();
}