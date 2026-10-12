package org.foxtrot.utils.user;

import org.foxtrot.enums.UsersEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * CLI: создаёт на стенде пользователей из {@link UsersEnum}.
 * organizationId берётся из backofficeOrganizationId (config / -Pstage14).
 */
public class UsersEnumProvisioner {

    private static final Logger logger = LoggerFactory.getLogger(UsersEnumProvisioner.class);

    public static void main(String[] args) {
        UsersEnum[] targets = resolveTargets(args);
        boolean setupRealEncryptionKeys = Boolean.parseBoolean(
                System.getProperty("provision.setupRealEncryptionKeys", "true")
        );
        String recoveryEmailOverride = System.getProperty("provision.recoveryEmail");

        UserCreationHelper creationHelper = new UserCreationHelper();

        int created = 0;
        int skipped = 0;
        int failed = 0;

        for (UsersEnum user : targets) {
            String phone = toE164(user.getPhoneNumber());
            try {
                var dataBuilder = UserRegistrationData.defaultsForPhone(phone).toBuilder()
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName().isBlank() ? "User" : user.getLastName())
                        .setupRealEncryptionKeys(setupRealEncryptionKeys);
                if (recoveryEmailOverride != null && !recoveryEmailOverride.isBlank()) {
                    dataBuilder.recoveryEmail(recoveryEmailOverride.trim());
                }
                UserRegistrationData data = dataBuilder.build();

                CreatedUser result = creationHelper.ensureUser(data);
                if (result.getAccountId() != null) {
                    logger.info("CREATED {} phone={} accountId={}", user.name(), phone, result.getAccountId());
                    created++;
                } else {
                    logger.info("SKIP {} ({}) — уже зарегистрирован", user.name(), phone);
                    skipped++;
                }
            } catch (Exception e) {
                failed++;
                logger.error("FAILED {} ({}): {}", user.name(), phone, e.getMessage(), e);
            }
        }

        logger.info("Готово: created={}, skipped={}, failed={}", created, skipped, failed);
        if (failed > 0) {
            System.exit(1);
        }
    }

    private static UsersEnum[] resolveTargets(String[] args) {
        if (args == null || args.length == 0) {
            return UsersEnum.values();
        }
        return Arrays.stream(args)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(UsersEnumProvisioner::toUsersEnum)
                .toArray(UsersEnum[]::new);
    }

    private static UsersEnum toUsersEnum(String name) {
        try {
            return UsersEnum.valueOf(name);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Неизвестный UsersEnum: " + name, e);
        }
    }

    static String toE164(String phoneFromEnum) {
        String digits = phoneFromEnum.replaceAll("\\D", "");
        if (digits.length() == 10 && digits.startsWith("9")) {
            return "7" + digits;
        }
        if (digits.length() == 11 && digits.startsWith("7")) {
            return digits;
        }
        return digits;
    }
}
