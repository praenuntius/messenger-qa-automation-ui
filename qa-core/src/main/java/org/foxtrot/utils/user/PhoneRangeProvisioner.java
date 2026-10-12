package org.foxtrot.utils.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * CLI: провижн пользователей по диапазону телефонов.
 * <p>
 * Для каждого номера: backoffice exist → если свободен (или только whitelist) —
 * {@link UserCreationHelper#ensureUser} (whitelist + регистрация в мессенджере).
 * Уже зарегистрированные пропускаются.
 * <p>
 * Запуск из корня репозитория:
 * <pre>
 * mvn -pl qa-core exec:java \
 *   -Dexec.mainClass=org.foxtrot.utils.user.PhoneRangeProvisioner \
 *   -Dexec.args='--from 9774660051 --to 9774660100'
 *
 * # stage14:
 * mvn -pl qa-core -Pstage14 exec:java \
 *   -Dexec.mainClass=org.foxtrot.utils.user.PhoneRangeProvisioner \
 *   -Dexec.args='--from 9774660051 --to 9774660100'
 * </pre>
 * Либо: {@code -Dprovision.from=9774660051 -Dprovision.to=9774660100}
 */
public class PhoneRangeProvisioner {

    private static final Logger logger = LoggerFactory.getLogger(PhoneRangeProvisioner.class);

    public static void main(String[] args) {
        Range range = resolveRange(args);
        boolean setupRealEncryptionKeys = Boolean.parseBoolean(
                System.getProperty("provision.setupRealEncryptionKeys", "true")
        );
        String recoveryEmailOverride = System.getProperty("provision.recoveryEmail");

        logger.info("Провижн диапазона: {} … {} ({} номеров)",
                range.fromE164, range.toE164, range.toE164 - range.fromE164 + 1);

        UserCreationHelper creationHelper = new UserCreationHelper();
        creationHelper.ensureBackofficeLoggedIn();

        int created = 0;
        int skipped = 0;
        int failed = 0;
        List<String> enumSnippets = new ArrayList<>();

        for (long phoneNum = range.fromE164; phoneNum <= range.toE164; phoneNum++) {
            String phoneE164 = String.valueOf(phoneNum);
            String phoneForEnum = toUsersEnumPhone(phoneE164);
            String enumName = toEnumConstantName(phoneForEnum);
            String displayName = toDisplayName(phoneForEnum);

            try {
                // имя как в UsersEnum: "test 51" → firstName=test, lastName=51
                var dataBuilder = UserRegistrationData.defaultsForPhone(phoneE164).toBuilder()
                        .firstName("test")
                        .lastName(lastNameFromDisplay(displayName))
                        .setupRealEncryptionKeys(setupRealEncryptionKeys);
                if (recoveryEmailOverride != null && !recoveryEmailOverride.isBlank()) {
                    dataBuilder.recoveryEmail(recoveryEmailOverride.trim());
                }

                CreatedUser result = creationHelper.ensureUser(dataBuilder.build());
                if (result.getAccountId() != null) {
                    String processId = UUID.randomUUID().toString();
                    String snippet = formatUsersEnumLine(
                            enumName, phoneForEnum, result.getAccountId(), processId, displayName);
                    enumSnippets.add(snippet);
                    logger.info("CREATED {} phone={} accountId={}", enumName, phoneE164, result.getAccountId());
                    logger.info("UsersEnum: {}", snippet);
                    created++;
                } else {
                    logger.info("SKIP {} ({}) — уже зарегистрирован", enumName, phoneE164);
                    skipped++;
                }
            } catch (Exception e) {
                failed++;
                logger.error("FAILED {} ({}): {}", enumName, phoneE164, e.getMessage(), e);
            }
        }

        logger.info("Готово: created={}, skipped={}, failed={}", created, skipped, failed);
        if (!enumSnippets.isEmpty()) {
            logger.info("----- Вставьте в UsersEnum -----");
            for (int i = 0; i < enumSnippets.size(); i++) {
                boolean last = i == enumSnippets.size() - 1;
                logger.info("{}{}", enumSnippets.get(i), last ? ";" : ",");
            }
            logger.info("----- savedChatId=0 — проставьте позже при необходимости -----");
        }
        if (failed > 0) {
            System.exit(1);
        }
    }

    private static Range resolveRange(String[] args) {
        String from = System.getProperty("provision.from");
        String to = System.getProperty("provision.to");

        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                String a = args[i].trim();
                if ("--from".equals(a) && i + 1 < args.length) {
                    from = args[++i].trim();
                } else if ("--to".equals(a) && i + 1 < args.length) {
                    to = args[++i].trim();
                } else if (a.startsWith("--from=")) {
                    from = a.substring("--from=".length()).trim();
                } else if (a.startsWith("--to=")) {
                    to = a.substring("--to=".length()).trim();
                }
            }
        }

        if (from == null || from.isBlank() || to == null || to.isBlank()) {
            throw new IllegalArgumentException(
                    "Укажите диапазон: --from 9774660051 --to 9774660100 "
                            + "или -Dprovision.from=… -Dprovision.to=…"
            );
        }

        long fromE164 = Long.parseLong(UsersEnumProvisioner.toE164(from));
        long toE164 = Long.parseLong(UsersEnumProvisioner.toE164(to));
        if (toE164 < fromE164) {
            throw new IllegalArgumentException("to < from: " + fromE164 + " … " + toE164);
        }
        if (toE164 - fromE164 > 500) {
            throw new IllegalArgumentException(
                    "Слишком большой диапазон (" + (toE164 - fromE164 + 1)
                            + "). Максимум 500 номеров за запуск."
            );
        }
        return new Range(fromE164, toE164);
    }

    /** 79774660051 → 9774660051 (как в UsersEnum). */
    static String toUsersEnumPhone(String phoneE164) {
        String digits = phoneE164.replaceAll("\\D", "");
        if (digits.length() == 11 && digits.startsWith("7")) {
            return digits.substring(1);
        }
        return digits;
    }

    /** 9774660051 → TEST51; 9774660100 → TEST100 */
    static String toEnumConstantName(String phoneForEnum) {
        String digits = phoneForEnum.replaceAll("\\D", "");
        if (digits.startsWith("977466")) {
            String suffix = digits.substring("977466".length());
            int n = Integer.parseInt(suffix);
            return "TEST" + n;
        }
        return "TEST_" + digits;
    }

    /** 9774660051 → "test 51" */
    static String toDisplayName(String phoneForEnum) {
        String digits = phoneForEnum.replaceAll("\\D", "");
        if (digits.startsWith("977466")) {
            int n = Integer.parseInt(digits.substring("977466".length()));
            return "test " + n;
        }
        return "test " + digits;
    }

    private static String lastNameFromDisplay(String displayName) {
        String[] parts = displayName.trim().split("\\s+", 2);
        return parts.length > 1 ? parts[1] : "User";
    }

    static String formatUsersEnumLine(
            String enumName,
            String phoneForEnum,
            long accountId,
            String processId,
            String displayName
    ) {
        return String.format(
                Locale.ROOT,
                "%s(\"%s\", %dL, \"%s\", \"%s\", 0L)",
                enumName,
                phoneForEnum,
                accountId,
                processId,
                displayName
        );
    }

    private record Range(long fromE164, long toE164) {
    }
}
