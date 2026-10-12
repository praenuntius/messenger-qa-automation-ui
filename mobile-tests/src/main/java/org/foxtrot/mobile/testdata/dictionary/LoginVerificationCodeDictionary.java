package org.foxtrot.mobile.testdata.dictionary;

import static org.foxtrot.mobile.config.MobilePlatformResolver.isAndroid;

public final class LoginVerificationCodeDictionary {

    private LoginVerificationCodeDictionary() {}

    public static final String SCREEN_TITLE = isAndroid() ? "Проверочный код" : "Введите код";
}
