package org.foxtrot.mobile.testdata.dictionary;

import static org.foxtrot.mobile.config.MobilePlatformResolver.isAndroid;

public final class AddSubscribersDictionary {

    private AddSubscribersDictionary() {}

    public static final String SCREEN_TITLE_CHANNEL = isAndroid() ? "Добавить подписчиков" : "Подписчики";
    public static final String SCREEN_TITLE_GROUP = isAndroid() ? "Добавить участников" : "Участники";
    public static final String SCREEN_TITLE_GROUP_BY_SETTINGS = "Добавить участников";
    public static final String SCREEN_TITLE_CHANNEL_BY_SETTINGS = "Добавить подписчиков";
}
