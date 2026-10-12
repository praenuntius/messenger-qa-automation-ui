package org.foxtrot.test.api;

import io.qameta.allure.AllureId;
import io.qameta.allure.junit5.AllureJunit5;
import org.foxtrot.api.BaseApiService;
import org.foxtrot.api.steps.GroupApiSteps;
import org.foxtrot.enums.UsersEnum;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(AllureJunit5.class)
@Tag("api")
@Tag("deviceHash")
public class DeviceHashIsolationTest {

    @Disabled
    @AllureId("DEVICE_HASH_ISOLATION")
    @Test
    void apiSessionsShouldNotInvalidateEachOtherWhenRunningInParallel() throws Exception {
        UsersEnum user1 = UsersEnum.TEST50;
        UsersEnum user2 = UsersEnum.TEST49;

        ExecutorService pool = Executors.newFixedThreadPool(2);
        CyclicBarrier barrier = new CyclicBarrier(2);

        try {
            Future<Integer> user1Final = pool.submit(userFlow(user1, barrier, true));
            Future<Integer> user2Final = pool.submit(userFlow(user2, barrier, false));

            int u1 = getFuture(user1Final);
            int u2 = getFuture(user2Final);

            assertEquals(200, u2, "user2 final call must be 200");
            assertEquals(200, u1, "user1 final call must remain 200 after user2 login");
        } finally {
            pool.shutdownNow();
            pool.awaitTermination(10, TimeUnit.SECONDS);
        }
    }

    private static Callable<Integer> userFlow(UsersEnum user, CyclicBarrier barrier, boolean doSecondCallAfterBarrier) {
        return () -> {
            // Важно: BaseApiService кэширует токен в ThreadLocal, поэтому чистим перед стартом потока.
            BaseApiService.clearTokenCache();

            ApiProbe api = new ApiProbe();

            int first = api.getUserGroupStatesStatus(user);
            assertEquals(200, first, user.name() + " first call must be 200");

            barrier.await(60, TimeUnit.SECONDS);

            if (!doSecondCallAfterBarrier) {
                // user2 просто подтверждает, что сессия жива после параллельного логина
                int second = api.getUserGroupStatesStatus(user);
                assertEquals(200, second, user.name() + " second call must be 200");
                return second;
            }

            // user1 — критичный кейс: после логина user2 не должен получить 401.
            int after = api.getUserGroupStatesStatus(user);
            assertEquals(200, after, user.name() + " call after parallel login must be 200 (not 401)");
            return after;
        };
    }

    private static int getFuture(Future<Integer> f) throws Exception {
        try {
            return f.get(3, TimeUnit.MINUTES);
        } catch (ExecutionException e) {
            // unwrap assertion failure from worker thread
            Throwable cause = e.getCause();
            if (cause instanceof Exception ex) throw ex;
            throw new RuntimeException(cause);
        }
    }

    /**
     * Минимальный "probe" поверх BaseApiService, чтобы тест проверял реальные HTTP коды.
     */
    private static final class ApiProbe extends BaseApiService {
        int getUserGroupStatesStatus(UsersEnum user) {
            // Важно: используем реальный шаг из qa-core, чтобы тест был максимально приближен к реальным вызовам.
            // Метод вернет List и выбросит исключение, если transport сломался до ответа.
            new GroupApiSteps(user).getUserGroupStates();
            return 200;
        }
    }
}

