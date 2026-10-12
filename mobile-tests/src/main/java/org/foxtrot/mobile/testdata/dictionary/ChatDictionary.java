package org.foxtrot.mobile.testdata.dictionary;

import static org.foxtrot.mobile.config.MobilePlatformResolver.isIos;

public final class ChatDictionary {

    private ChatDictionary() {
    }

    public static final String CHANNEL_WAS_CREATED = "Создан канал «%s»";
    public static final String GROUP_WAS_CREATED = "Вы создали группу «%s»";
    public static final String YOU_JOINED_GROUP = isIos() ? "Вы вступили в группу" : "Вы присоединились к группе";
    public static final String YOU_INVITED = "Вы добавили";
    public static final String NUMBER_OF_SUBS = "%d подписчик";
    public static final String NUMBER_OF_PARTICIPANTS = "%d участник";
    public static final String GROUP_NOT_CREATED = "Группа не создана";
    public static final String LINK_NOT_ACTIVE = "Ссылка неактивна или у вас нет доступа к пространству";
    public static final String GROUP_CHANNEL_NOT_EXIST = "Эта группа (канал) больше не существует, поэтому доступ невозможен";
    public static final String NO_ACCESS_TO_GROUP_OR_CHANNEL = "Нет доступа к группе (каналу)";
    public static final String NO_ACCESS_NOT_PARTICIPANT = "У вас нет доступа к группе (каналу), так как вы не " +
            "состоите в этом пространстве";
    public static final String JOIN_GROUP = "Вступить в группу";
    public static final String JOIN = "Присоединиться";
    public static final String SUBSCRIBE_TO_CHANNEL = "Подписаться на канал";
    public static final String ENTER_WORKSPACE = "Войти в пространство";
    public static final String ENTER_GROUP_INACTIVE_WS = isIos()
            ? "Группа находится в неактивном пространстве. Войти в пространство?"
            : "Группа или канал находится в неактивном пространстве. Войти в пространство?";
    public static final String ENTER_GROUP_INACTIVE_WS_MSG = isIos()
            ? "Для того, чтобы войти в группу она должна находится в активном пространстве. "
            + "Для активации пространства вам необходимо просто в него войти."
            : "Для того, чтобы вступить в группу или канал, они должны находиться в активном пространстве. "
            + "Для активации пространства вам необходимо просто в него войти";
    public static final String ENTER_CHANNEL_INACTIVE_WS = isIos()
            ? "Канал находится в неактивном пространстве. Войти в пространство?"
            : "Группа или канал находится в неактивном пространстве. Войти в пространство?";
    public static final String ENTER_CHANNEL_INACTIVE_WS_MSG = isIos()
            ? "Для того, чтобы подписаться на канал, он должен находиться в активном пространстве. "
            + "Для активации пространства вам необходимо просто в него войти."
            : "Для того, чтобы вступить в группу или канал, они должны находиться в активном пространстве. "
            + "Для активации пространства вам необходимо просто в него войти";
}
