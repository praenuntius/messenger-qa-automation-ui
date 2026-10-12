package org.foxtrot.mobile.testdata.dictionary;

import static org.foxtrot.mobile.config.MobilePlatformResolver.isIos;

public final class LoginWelcomeDictionary {

    private LoginWelcomeDictionary() {}

    /**
     * Preprod (tango_preprod.apk). Новый RC уже Tango; старые сборки ещё TDM.
     */
    public static final String SCREEN_TITLE = isIos()
            ? "Добро пожаловать в %Tango%"
            : "Добро пожаловать в Tango";

    /**
     * Stage14 / foxtrot debug builds: Android уже Foxtrot; iOS Messenger.app на stage
     * тоже Foxtrot (раньше был {@code <Tango>}).
     */
    public static final String SCREEN_TITLE_STAGE = isIos()
            ? "Добро пожаловать в <Foxtrot>"
            : "Добро пожаловать в %Foxtrot%";

    /**
     * Допустимые варианты заголовка welcome: бренд и плейсхолдеры в UI
     * (Tango / TDM / Foxtrot, {@code %}/{@code <>}).
     */
    public static final String[] SCREEN_TITLE_CANDIDATES = {
            "Добро пожаловать в Tango",
            "Добро пожаловать в %Tango%",
            "Добро пожаловать в <Tango>",
            "Добро пожаловать в TDM",
            "Добро пожаловать в %TDM%",
            "Добро пожаловать в <TDM>",
            "Добро пожаловать в Foxtrot",
            "Добро пожаловать в %Foxtrot%",
            "Добро пожаловать в <Foxtrot>"
    };

    /** @see #SCREEN_TITLE_CANDIDATES */
    public static final String[] SCREEN_TITLE_STAGE_CANDIDATES = SCREEN_TITLE_CANDIDATES;
}
