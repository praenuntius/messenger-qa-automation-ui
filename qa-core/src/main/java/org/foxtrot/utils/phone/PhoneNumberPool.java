package org.foxtrot.utils.phone;

import org.foxtrot.backoffice.service.BackofficeUsersApiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Пул тестовых номеров 79774670000–79774679999.
 * Выбирает свободный номер через backoffice /users/exist.
 */
public class PhoneNumberPool {

    private static final Logger logger = LoggerFactory.getLogger(PhoneNumberPool.class);

    public static final long PHONE_RANGE_START = 79774670000L;
    public static final long PHONE_RANGE_END = 79774679999L;

    private static final AtomicLong nextCandidate = new AtomicLong(PHONE_RANGE_START);
    private static final Object LOCK = new Object();

    private final BackofficeUsersApiService backofficeUsersApiService;

    public PhoneNumberPool() {
        this(new BackofficeUsersApiService());
    }

    public PhoneNumberPool(BackofficeUsersApiService backofficeUsersApiService) {
        this.backofficeUsersApiService = backofficeUsersApiService;
    }

    /**
     * @return свободный номер (11 цифр, начинается с 7)
     */
    public String allocateFreePhone() {
        synchronized (LOCK) {
            long rangeSize = PHONE_RANGE_END - PHONE_RANGE_START + 1;
            for (int attempt = 0; attempt < rangeSize; attempt++) {
                long phone = nextCandidate.getAndUpdate(current -> {
                    long next = current + 1;
                    return next > PHONE_RANGE_END ? PHONE_RANGE_START : next;
                });
                String phoneStr = String.valueOf(phone);
                if (!backofficeUsersApiService.userExistsByPhone(phoneStr)) {
                    logger.info("Выбран свободный номер: {}", phoneStr);
                    return phoneStr;
                }
                logger.debug("Номер {} уже занят, пробуем следующий", phoneStr);
            }
            throw new IllegalStateException(
                    "Не удалось найти свободный номер в диапазоне " + PHONE_RANGE_START + "–" + PHONE_RANGE_END
            );
        }
    }
}
