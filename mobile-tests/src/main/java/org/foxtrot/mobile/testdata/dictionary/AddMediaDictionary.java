package org.foxtrot.mobile.testdata.dictionary;

import static org.foxtrot.mobile.config.MobilePlatformResolver.isIos;

public final class AddMediaDictionary {

    private AddMediaDictionary() {
    }

    public static final String TAKE_PHOTO = "Сфотографировать";
    public static final String CHOOSE_FROM_GALLERY = "Выбрать из галереи";
    public static final String ATTACH_FILE = isIos() ? "Выбрать из документов" : "Прикрепить файл";
    public static final String ACCEPT_FULL = "Разрешить полный доступ";
    public static final String DELETE = isIos() ? "Удалить" : "Удалить фото";
}
