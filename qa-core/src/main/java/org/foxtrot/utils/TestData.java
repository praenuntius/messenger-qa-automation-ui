package org.foxtrot.utils;

import net.datafaker.Faker;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.UUID;

import static org.foxtrot.api.constants.TestConstants.SMS_CODE;
import static org.foxtrot.api.constants.TestConstants.TFA_CODE;

public class TestData {

    private static final Faker FAKER_RU = new Faker(new Locale("ru"));
    private static final Faker FAKER_EN = new Faker(Locale.ENGLISH);
    private int count = 0;

    public static @NotNull String generateHomespaceGroupName() {
        return "HomespaceGroup_" + UUID.randomUUID().toString().substring(0, 8);
    }

    public static @NotNull String generateHomespaceChannelName() {
        return "HomespaceChannel_" + UUID.randomUUID().toString().substring(0, 8);
    }

    public static @NotNull String generateWorkspaceName() {
        return "Workspace_" + UUID.randomUUID().toString().substring(0, 8);
    }

    public static @NotNull String generateWorkspacePublicGroupName() {
        return "WorkspacePublicGroup_" + DateTimeUtil.getCurrentDateTime("ddMMHHmmss");
    }

    public static @NotNull String generateWorkspacePrivateGroupName() {
        return "WorkspacePrivateGroup_" + DateTimeUtil.getCurrentDateTime("ddMMHHmmss");
    }

    public static @NotNull String generateWorkspacePublicChannelName() {
        return "WorkspacePublicChannel_" + DateTimeUtil.getCurrentDateTime("ddMMHHmmss");
    }

    public static @NotNull String generateWorkspacePrivateChannelName() {
        return "WorkspacePrivateChannel_" + DateTimeUtil.getCurrentDateTime("ddMMHHmmss");
    }

    public static @NotNull String generateAnyEntityName() {
        return "Entity" + DateTimeUtil.getCurrentDateTime("ddMMHHmmss");
    }

    public static String randomCode() {
        String code;
        do {
            code = FAKER_EN.number().digits(6);
        } while (SMS_CODE.contains(code) || TFA_CODE.contains(code));
        return code;
    }

    public static String randomFirstName() {
        return FAKER_RU.name().firstName();
    }

    public static String randomLastName() {
        return FAKER_RU.name().lastName();
    }

    public static String randomText(int length) {
        return FAKER_EN.text().text(length);
    }

    public static String randomSentence(int wordsCount) {
        return FAKER_RU.lorem().sentence(wordsCount);
    }
}

