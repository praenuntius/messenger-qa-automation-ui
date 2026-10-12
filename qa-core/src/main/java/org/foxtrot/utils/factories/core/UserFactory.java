package org.foxtrot.utils.factories.core;

import org.foxtrot.api.BaseApiService;
import org.foxtrot.api.steps.AccountApiSteps;
import org.foxtrot.enums.UsersEnum;
import org.foxtrot.utils.UserPoolManager;
import org.foxtrot.utils.UserTestDataCleaner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class UserFactory {
    private static final Logger logger = LoggerFactory.getLogger(UserFactory.class);

    private static final int MAX_TAKE_ATTEMPTS = 12;
    private final UserTestDataCleaner cleaner = new UserTestDataCleaner();

    public UsersEnum takeUser() {
        RuntimeException lastError = null;
        for (int attempt = 1; attempt <= MAX_TAKE_ATTEMPTS; attempt++) {
            UsersEnum user = UserPoolManager.getFreeUser();
            try {
                new AccountApiSteps(user).getAllWorkspace();
                return user;
            } catch (RuntimeException error) {
                lastError = error;
                if (!UserTestDataCleaner.isUnusableAccount(error)) {
                    throw error;
                }
                UserPoolManager.quarantine(user);
                logger.warn("Пропускаю сломанный аккаунт {} (попытка {}/{}): {}",
                        user.name(), attempt, MAX_TAKE_ATTEMPTS, error.getMessage());
            }
        }
        throw lastError != null
                ? lastError
                : new RuntimeException("Не удалось взять пользователя из пула");
    }

    public void releaseAll() {
        Set<UsersEnum> users = UserPoolManager.getCurrentThreadUsers();
        if (!users.isEmpty()) {
            cleaner.clean(users.toArray(new UsersEnum[0]));
            UserPoolManager.releaseCurrentUser();
            BaseApiService.clearTokenCache();
        }
    }
}