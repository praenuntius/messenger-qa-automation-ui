package org.foxtrot.utils;

import org.foxtrot.enums.UsersEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class UserPoolManager {

    private static final Logger logger = LoggerFactory.getLogger(UserPoolManager.class);
    private static final List<UsersEnum> allUsers = Arrays.asList(UsersEnum.values());
    private static final ConcurrentHashMap<UsersEnum, AtomicBoolean> userStatus = new ConcurrentHashMap<>();
    private static final Set<UsersEnum> quarantined = ConcurrentHashMap.newKeySet();
    private static final ThreadLocal<Set<UsersEnum>> currentUserHoldings = ThreadLocal.withInitial(HashSet::new);

    static {
        for (UsersEnum user : allUsers) {
            userStatus.put(user, new AtomicBoolean(false));
        }
    }

    public static UsersEnum getFreeUser() {
        for (UsersEnum user : allUsers) {
            if (quarantined.contains(user)) {
                continue;
            }
            AtomicBoolean status = userStatus.get(user);
            if (status.compareAndSet(false, true)) {
                currentUserHoldings.get().add(user);
                logger.info("Поток {}: Выделен пользователь {}", Thread.currentThread().getName(), user.getPhoneNumber());
                return user;
            }
        }
        throw new RuntimeException("Все пользователи заняты!");
    }

    /**
     * Не возвращать пользователя в пул до конца JVM: signIn/WORKSPACE_DOES_NOT_EXIST и т.п.
     */
    public static void quarantine(UsersEnum user) {
        if (user == null) {
            return;
        }
        quarantined.add(user);
        AtomicBoolean status = userStatus.get(user);
        if (status != null) {
            status.set(true);
        }
        Set<UsersEnum> holdings = currentUserHoldings.get();
        if (holdings != null) {
            holdings.remove(user);
        }
        logger.warn("Поток {}: {} исключён из пула (сломанный аккаунт)",
                Thread.currentThread().getName(), user.name());
    }

    public static void releaseCurrentUser() {
        Set<UsersEnum> users = currentUserHoldings.get();
        if (users != null) {
            for (UsersEnum user : users) {
                if (quarantined.contains(user)) {
                    continue;
                }
                userStatus.get(user).set(false);
                logger.info("Поток {}: Освобождён {}", Thread.currentThread().getName(), user.getPhoneNumber());
            }
            currentUserHoldings.remove();
        }
    }

    public static Set<UsersEnum> getCurrentThreadUsers() {
        return currentUserHoldings.get();
    }
}
