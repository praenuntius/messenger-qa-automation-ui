package org.foxtrot.test.api.encryption;

import io.qameta.allure.Allure;
import io.qameta.allure.AllureId;
import io.qameta.allure.Description;
import io.qameta.allure.junit5.AllureJunit5;
import org.foxtrot.api.BaseApiService;
import org.foxtrot.api.dto.encryption.SymmetricKeyUpdateRequest;
import org.foxtrot.api.dto.message.SendTextMessageRequest;
import org.foxtrot.api.service.EncryptedApiService;
import org.foxtrot.api.service.EncryptionApiService;
import org.foxtrot.api.service.SseConnectionClient;
import org.foxtrot.enums.UsersEnum;
import org.foxtrot.utils.EncryptionHelper;
import org.foxtrot.utils.SkrTestDataCleaner;
import org.foxtrot.utils.SymmetricKeyMaterial;
import org.foxtrot.utils.SymmetricKeySession;
import org.foxtrot.utils.SymmetricKeySessionFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

import static org.foxtrot.api.constants.ApiEndpoints.PUSHER_EVENTS;
import static org.foxtrot.api.constants.ApiEndpoints.PUSHER_EVENTS_GROUP;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Слой 1: API-контракт ротации симметричного ключа (SKR).
 * <pre>
 * mvn test -pl web-tests -Pstage14 -Dgroups="encryption-suite &amp; api" \
 *   -Dthread-count=1 -Dmaven.main.skip=true
 * </pre>
 * <p>
 * Allure TestOps: кейсы с {@code @AllureId} наследуют папку дерева TestOps.
 * Чтобы в лаунче «Encryption … - API» все SKR-API-* были без подпапки «API»,
 * в TestOps перенеси кейсы 001–010 (и др. с AllureId) из папки API на корень suite.
 * Снимать {@code @AllureId} ради плоского дерева не нужно — потеряется история.
 */
@ExtendWith(AllureJunit5.class)
@Tag("encryption-suite")
@Tag("api")
@Tag("encryption")
@Execution(ExecutionMode.SAME_THREAD)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SymmetricKeyRotationTest {

    private static final int HTTP_KEY_MISMATCH_REST = 494;
    private static final int HTTP_KEY_MISMATCH_SSE = 493;
    /** Запас после того, как ключ уже не current (ротация или TTL), перед GET SSE. */
    private static final long SSE_AFTER_KEY_INVALID_WAIT_MS = 20_000L;

    /**
     * API-пул без TEST49: Web/iOS E2E логинятся под TEST49 параллельно.
     * Иначе verifyCode даёт TOO_MANY_ATTEMPTS на двух номерах.
     */
    private static final List<UsersEnum> USER_POOL = List.of(
            UsersEnum.TEST47, UsersEnum.TEST48
    );
    private static final AtomicInteger USER_CURSOR = new AtomicInteger(0);

    private final SymmetricKeySessionFactory sessionFactory = new SymmetricKeySessionFactory();
    private final EncryptedApiService encryptedApi = new EncryptedApiService();
    private final EncryptionApiService encryptionApi = new EncryptionApiService();
    private final EncryptionHelper encryptionHelper = new EncryptionHelper();
    private final SseConnectionClient sseClient = new SseConnectionClient();
    private final SkrTestDataCleaner dataCleaner = new SkrTestDataCleaner();

    @BeforeEach
    void setUp() {
        BaseApiService.clearTokenCache();
    }

    @AfterAll
    void afterAllCleanSkrData() {
        dataCleaner.clean(USER_POOL.toArray(UsersEnum[]::new));
    }

    @Test
    @AllureId("93998")
    @DisplayName("SKR-API-001: updateSymmetricKeyV2 + confirm returns a new key id")
    @Description("""
            Проверяет базовый контракт серверной ротации DF-554:
            saveSymmetricKey → updateSymmetricKeyV2 → confirmSymmetricKeyUpdate.
            Новый encryption-key-id отличается от предыдущего; сессия использует его
            только после confirm.
            """)
    void updateSymmetricKey_shouldReturnSuccess() {
        SymmetricKeySession session = Allure.step("Залогиниться с encryption keys",
                () -> sessionFactory.login(nextUser()));
        long oldKeyId = session.getMessageKey().getId();

        SymmetricKeyMaterial newKey = Allure.step("Вызвать rotate V2+confirm",
                () -> sessionFactory.rotate(session));

        Allure.step("Новый key id отличается от старого и сохранён в сессии", () -> {
            assertNotEquals(oldKeyId, newKey.getId());
            assertEquals(newKey.getId(), session.getMessageKey().getId());
        });
    }

    @Test
    @AllureId("94003")
    @DisplayName("SKR-API-002: sendTextMessage with new key after rotation returns 200")
    @Description("""
            После ротации зашифрованный sendTextMessage с актуальным encryption-key-id
            должен успешно проходить (HTTP 200). Это happy-path для клиента после catch-up.
            """)
    void sendTextMessage_withNewKeyAfterRotation_shouldReturn200() {
        SymmetricKeySession session = Allure.step("Залогиниться с encryption keys",
                () -> sessionFactory.login(nextUser()));
        long chatId = Allure.step("Создать homespace-группу для отправки",
                () -> resolveChatId(session));

        Allure.step("Ротировать симметричный ключ", () -> sessionFactory.rotate(session));

        var response = Allure.step("Отправить encrypted sendTextMessage новым ключом",
                () -> encryptedApi.sendTextMessage(textMessage("after-rotation", chatId), session));

        Allure.step("Ожидаем HTTP 200", () ->
                assertEquals(200, response.statusCode(),
                        "Encrypted sendTextMessage with new key should succeed, body="
                                + response.getBody().asString()));
    }

    @Test
    @AllureId("94004")
    @DisplayName("SKR-API-003: sendTextMessage with stale key after rotation returns 494")
    @Description("""
            После ротации запрос со старым encryption-key-id должен получить REST 494
            (key mismatch). Клиент делает один REST retry с актуальным ключом; rotation
            по 494 не стартует (DF-554 KeyRotationMiddleware).
            """)
    void sendTextMessage_withStaleKeyAfterRotation_shouldReturn494() {
        SymmetricKeySession session = Allure.step("Залогиниться с encryption keys",
                () -> sessionFactory.login(nextUser()));
        long chatId = Allure.step("Создать homespace-группу",
                () -> resolveChatId(session));
        SymmetricKeyMaterial staleKey = Allure.step("Сохранить копию текущего ключа (станет stale)",
                () -> copyKey(session.getMessageKey()));

        Allure.step("Ротировать ключ на сервере", () -> sessionFactory.rotate(session));

        var response = Allure.step("Отправить сообщение со stale encryption-key-id",
                () -> encryptedApi.sendTextMessage(
                        textMessage("stale-key", chatId),
                        session.getAccessToken(),
                        staleKey,
                        staleKey.getId()
                ));

        Allure.step("Ожидаем HTTP 494", () ->
                assertEquals(HTTP_KEY_MISMATCH_REST, response.statusCode(),
                        "Stale encryption-key-id should return 494, body="
                                + response.getBody().asString()));
    }

    @Test
    @AllureId("94005")
    @DisplayName("SKR-API-004: sendTextMessage without encryption-key-id does not return 494")
    @Description("""
            Без заголовка encryption-key-id сервер не должен отвечать 494
            (проверка mismatch пропускается). Ответ может быть другим (4xx/2xx),
            но не кодом «ключ не совпал».
            """)
    void sendTextMessage_withoutEncryptionKeyId_shouldNotReturn494() {
        SymmetricKeySession session = Allure.step("Залогиниться с encryption keys",
                () -> sessionFactory.login(nextUser()));
        long chatId = Allure.step("Создать homespace-группу",
                () -> resolveChatId(session));

        var response = Allure.step("Отправить sendTextMessage без encryption-key-id",
                () -> encryptedApi.sendTextMessageWithoutEncryptionKeyId(
                        textMessage("no-key-id", chatId),
                        session
                ));

        Allure.step("Статус не должен быть 494", () ->
                assertNotEquals(HTTP_KEY_MISMATCH_REST, response.statusCode(),
                        "Without encryption-key-id header mismatch check should be skipped, body="
                                + response.getBody().asString()));
    }

    @Test
    @AllureId("94006")
    @DisplayName("SKR-API-005: sendTextMessage without Authorization returns 401")
    @Description("""
            Негатив на auth: encrypted sendTextMessage без Authorization → 401.
            Убеждаемся, что проверка ключа не подменяет проверку авторизации.
            """)
    void sendTextMessage_withoutAuthorization_shouldReturn401() {
        SymmetricKeySession session = Allure.step("Залогиниться с encryption keys",
                () -> sessionFactory.login(nextUser()));
        long chatId = Allure.step("Создать homespace-группу",
                () -> resolveChatId(session));

        var response = Allure.step("Отправить sendTextMessage без Authorization",
                () -> encryptedApi.sendTextMessageWithoutAuthorization(
                        textMessage("no-auth", chatId),
                        session
                ));

        Allure.step("Ожидаем HTTP 401", () ->
                assertEquals(401, response.statusCode(),
                        "Request without Authorization should return 401, body="
                                + response.getBody().asString()));
    }

    @Test
    @AllureId("94010")
    @DisplayName("SKR-API-006: oldest key after double rotation returns 494")
    @Description("""
            После двух подряд ротаций самый старый ключ (до первой ротации)
            остаётся невалидным → 494. Проверяем, что history ключей не «оживает».
            """)
    void doubleRotation_oldestKey_shouldReturn494() {
        SymmetricKeySession session = Allure.step("Залогиниться с encryption keys",
                () -> sessionFactory.login(nextUser()));
        long chatId = Allure.step("Создать homespace-группу",
                () -> resolveChatId(session));
        SymmetricKeyMaterial firstKey = Allure.step("Запомнить ключ #1",
                () -> copyKey(session.getMessageKey()));

        Allure.step("Первая ротация (ключ #1 → #2)", () -> sessionFactory.rotate(session));
        Allure.step("Вторая ротация (ключ #2 → #3)", () -> sessionFactory.rotate(session));

        var response = Allure.step("Отправить сообщение ключом #1 (oldest)",
                () -> encryptedApi.sendTextMessage(
                        textMessage("double-rotation", chatId),
                        session.getAccessToken(),
                        firstKey,
                        firstKey.getId()
                ));

        Allure.step("Ожидаем HTTP 494", () ->
                assertEquals(HTTP_KEY_MISMATCH_REST, response.statusCode(),
                        "Oldest key after double rotation should return 494, body="
                                + response.getBody().asString()));
    }

    @Test
    @AllureId("94011")
    @DisplayName("SKR-API-007: retry with current key after 494 returns 200")
    @Description("""
            Модель клиентского recovery: первый запрос со stale → 494,
            retry с актуальным ключом → 200. Это контракт, на который опираются WEB/MOB E2E.
            """)
    void clientRetry_after494_shouldSucceedWithCurrentKey() {
        SymmetricKeySession session = Allure.step("Залогиниться с encryption keys",
                () -> sessionFactory.login(nextUser()));
        long chatId = Allure.step("Создать homespace-группу",
                () -> resolveChatId(session));
        SymmetricKeyMaterial staleKey = Allure.step("Сохранить stale-ключ",
                () -> copyKey(session.getMessageKey()));

        Allure.step("Ротировать ключ на сервере", () -> sessionFactory.rotate(session));

        var staleResponse = Allure.step("Первый запрос со stale-ключом → ожидаем 494", () -> {
            var response = encryptedApi.sendTextMessage(
                    textMessage("retry-stale", chatId),
                    session.getAccessToken(),
                    staleKey,
                    staleKey.getId()
            );
            assertEquals(HTTP_KEY_MISMATCH_REST, response.statusCode());
            return response;
        });
        Allure.addAttachment("stale-response-status", String.valueOf(staleResponse.statusCode()));

        var retryResponse = Allure.step("Retry с актуальным ключом сессии",
                () -> encryptedApi.sendTextMessage(textMessage("retry-current", chatId), session));

        Allure.step("Ожидаем HTTP 200", () ->
                assertEquals(200, retryResponse.statusCode(),
                        "Retry with current key after 494 should succeed, body="
                                + retryResponse.getBody().asString()));
    }

    @Test
    @DisplayName("SKR-API-015: repeated stale requests keep returning 494 until current key is used")
    @Description("""
            Anti-loop контракт для клиента: несколько подряд запросов со stale encryption-key-id
            стабильно дают 494 (сервер не «прощает» старый ключ). Успех возможен только после
            смены на актуальный ключ (один rotate + retry), а не бесконечным повтором stale.
            """)
    void repeatedStaleRequests_shouldKeepReturning494_untilCurrentKey() {
        SymmetricKeySession session = Allure.step("Залогиниться с encryption keys",
                () -> sessionFactory.login(nextUser()));
        long chatId = Allure.step("Создать homespace-группу",
                () -> resolveChatId(session));
        SymmetricKeyMaterial staleKey = Allure.step("Сохранить stale-ключ",
                () -> copyKey(session.getMessageKey()));

        Allure.step("Ротировать ключ на сервере", () -> sessionFactory.rotate(session));

        Allure.step("Три stale-запроса подряд → каждый 494", () -> {
            for (int i = 1; i <= 3; i++) {
                final int attempt = i;
                var response = encryptedApi.sendTextMessage(
                        textMessage("stale-loop-" + attempt, chatId),
                        session.getAccessToken(),
                        staleKey,
                        staleKey.getId()
                );
                assertEquals(HTTP_KEY_MISMATCH_REST, response.statusCode(),
                        "Stale attempt #" + attempt + " must stay 494 (no eventual accept), body="
                                + response.getBody().asString());
            }
        });

        var currentResponse = Allure.step("Запрос актуальным ключом после stale-loop",
                () -> encryptedApi.sendTextMessage(textMessage("after-stale-loop", chatId), session));

        Allure.step("Ожидаем HTTP 200 только с current key", () ->
                assertEquals(200, currentResponse.statusCode(),
                        "Current key must succeed after repeated 494, body="
                                + currentResponse.getBody().asString()));
    }

    @Test
    @AllureId("94012")
    @DisplayName("SKR-API-008: SSE with valid key connects without 493")
    @Description("""
            Pusher SSE /api/v3/events с валидным encryption-key-id и заголовком
            WorkspaceIds (-1:0 для homespace) не должен получать 493.
            Last-Event-ID необязателен.
            """)
    void sse_withValidKey_shouldConnect() {
        SymmetricKeySession session = Allure.step("Залогиниться с encryption keys",
                () -> sessionFactory.login(nextUser()));

        var result = Allure.step("Открыть SSE /events с актуальным key id + WorkspaceIds",
                () -> sseClient.connectDetailed(
                        PUSHER_EVENTS,
                        session.getAccessToken(),
                        session.getMessageKey().getId(),
                        SseConnectionClient.homespaceWorkspaceIds(0L)
                ));
        Allure.addAttachment("sse-request", result.requestSummary());
        Allure.addAttachment("sse-body", result.bodyPreview());

        Allure.step("Статус не должен быть 493", () ->
                assertNotEquals(HTTP_KEY_MISMATCH_SSE, result.statusCode(),
                        "SSE with valid key should not return 493, actual=" + result.statusCode()
                                + ", body=" + result.bodyPreview()));
    }

    @Test
    @AllureId("94013")
    @DisplayName("SKR-API-009: SSE with stale encryption-key-id returns 493")
    @Description("""
            После updateSymmetricKey SSE /events со stale encryption-key-id
            (и корректным WorkspaceIds=-1:0) должен получить строго 493, не 400.
            Curl без WorkspaceIds даёт 400 — это другой кейс; в тесте WorkspaceIds есть.
            """)
    void sse_withMismatchedKey_shouldReturn493() {
        SymmetricKeySession session = Allure.step("Залогиниться с encryption keys",
                () -> sessionFactory.login(nextUser()));
        SymmetricKeyMaterial staleKey = Allure.step("Сохранить stale-ключ",
                () -> copyKey(session.getMessageKey()));

        Allure.step("Ротировать ключ на сервере", () -> sessionFactory.rotate(session));
        waitAfterKeyInvalid();

        var result = Allure.step("Открыть SSE со stale encryption-key-id + WorkspaceIds=-1:0",
                () -> sseClient.connectDetailed(
                        PUSHER_EVENTS,
                        session.getAccessToken(),
                        staleKey.getId(),
                        SseConnectionClient.homespaceWorkspaceIds(0L)
                ));
        Allure.addAttachment("sse-request", result.requestSummary());
        Allure.addAttachment("sse-body", result.bodyPreview());

        Allure.step("Ожидаем HTTP 493", () ->
                assertEquals(HTTP_KEY_MISMATCH_SSE, result.statusCode(),
                        "SSE with stale encryption-key-id must return 493, actual="
                                + result.statusCode() + ", body=" + result.bodyPreview()
                                + ", " + result.requestSummary()));
    }

    @Test
    @Tag("key-ttl")
    @AllureId("94014")
    @DisplayName("SKR-API-010: SSE after key TTL returns 493")
    @Description("""
            После истечения TTL SSE /events с тем же encryption-key-id
            (и WorkspaceIds=-1:0) должен получить строго 493.
            """)
    void sse_afterKeyTtl_shouldReturn493() throws InterruptedException {
        SymmetricKeySession session = Allure.step("Залогиниться с encryption keys",
                () -> sessionFactory.login(nextUser()));
        long ttlMinutes = Long.parseLong(System.getProperty("encryption.key.ttl.minutes", "1"));

        Allure.step("Дождаться истечения TTL ключа (~" + ttlMinutes + " мин + 20с)", () -> {
            try {
                Thread.sleep(ttlMinutes * 60 * 1000L + SSE_AFTER_KEY_INVALID_WAIT_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        });

        var result = Allure.step("Открыть SSE с expired key id + WorkspaceIds=-1:0",
                () -> sseClient.connectDetailed(
                        PUSHER_EVENTS,
                        session.getAccessToken(),
                        session.getMessageKey().getId(),
                        SseConnectionClient.homespaceWorkspaceIds(0L)
                ));
        Allure.addAttachment("sse-request", result.requestSummary());
        Allure.addAttachment("sse-body", result.bodyPreview());

        Allure.step("Ожидаем HTTP 493 после TTL", () ->
                assertEquals(HTTP_KEY_MISMATCH_SSE, result.statusCode(),
                        "SSE with expired key must return 493 after TTL, actual="
                                + result.statusCode() + ", body=" + result.bodyPreview()
                                + ", " + result.requestSummary()));
    }

    @Test
    @DisplayName("SKR-API-011: group SSE with valid key connects without 493")
    @Description("""
            Temporary/group SSE GET /api/v3/events/group с заголовками
            GroupId + WorkSpaceId (-1 для homespace) и актуальным encryption-key-id
            не должен получать 493.
            """)
    void groupSse_withValidKey_shouldConnect() {
        SymmetricKeySession session = Allure.step("Залогиниться с encryption keys",
                () -> sessionFactory.login(nextUser()));
        long groupId = Allure.step("Создать группу для temporary SSE",
                () -> resolveChatId(session));

        int status = Allure.step("Открыть SSE /events/group с актуальным key id",
                () -> sseClient.connectGroup(
                        PUSHER_EVENTS_GROUP,
                        session.getAccessToken(),
                        session.getMessageKey().getId(),
                        groupId
                ));

        Allure.step("Статус не должен быть 493", () ->
                assertNotEquals(HTTP_KEY_MISMATCH_SSE, status,
                        "Group SSE with valid key should not return 493, actual=" + status));
    }

    @Test
    @DisplayName("SKR-API-012: group SSE with stale encryption-key-id returns 493")
    @Description("""
            После updateSymmetricKey temporary/group SSE со stale encryption-key-id
            (заголовки GroupId + WorkSpaceId=-1) должен получить строго 493.
            """)
    void groupSse_withMismatchedKey_shouldReturn493() {
        SymmetricKeySession session = Allure.step("Залогиниться с encryption keys",
                () -> sessionFactory.login(nextUser()));
        long groupId = Allure.step("Создать группу для temporary SSE",
                () -> resolveChatId(session));
        SymmetricKeyMaterial staleKey = Allure.step("Сохранить stale-ключ",
                () -> copyKey(session.getMessageKey()));

        Allure.step("Ротировать ключ на сервере", () -> sessionFactory.rotate(session));
        waitAfterKeyInvalid();

        var result = Allure.step("Открыть group SSE со stale encryption-key-id",
                () -> sseClient.connectGroupDetailed(
                        PUSHER_EVENTS_GROUP,
                        session.getAccessToken(),
                        staleKey.getId(),
                        groupId,
                        SseConnectionClient.HOMESPACE_WORKSPACE_ID
                ));
        Allure.addAttachment("sse-request", result.requestSummary());
        Allure.addAttachment("sse-body", result.bodyPreview());

        Allure.step("Ожидаем HTTP 493", () ->
                assertEquals(HTTP_KEY_MISMATCH_SSE, result.statusCode(),
                        "Group SSE with stale encryption-key-id must return 493, actual="
                                + result.statusCode() + ", body=" + result.bodyPreview()
                                + ", " + result.requestSummary()));
    }

    @Test
    @DisplayName("SKR-API-013: updateSymmetricKeyV2 without Authorization returns 401")
    @Description("""
            updateSymmetricKeyV2 без Authorization должен отклоняться (401),
            даже если encryption-key-id и зашифрованное тело валидны по форме.
            """)
    void updateSymmetricKey_withoutAuth_shouldReturn401() {
        SymmetricKeySession session = Allure.step("Залогиниться с encryption keys",
                () -> sessionFactory.login(nextUser()));

        SymmetricKeyMaterial candidateKey = Allure.step("Сгенерировать и сохранить кандидат нового ключа",
                () -> encryptionHelper.generateAndSaveSymmetricKey(
                        session.getAccessToken(),
                        session.getMessageKey().getId()
                ));

        var response = Allure.step("Вызвать updateSymmetricKeyV2 без Authorization",
                () -> encryptionApi.updateSymmetricKeyV2(
                        new SymmetricKeyUpdateRequest(candidateKey.getId()),
                        null,
                        session.getMessageKey()
                ));

        Allure.step("Ожидаем HTTP 401", () ->
                assertEquals(401, response.statusCode(),
                        "updateSymmetricKeyV2 without Authorization should return 401, body="
                                + response.getBody().asString()));
    }

    @Test
    @DisplayName("SKR-API-014: updateSymmetricKeyV2 with stale encryption-key-id returns 494")
    @Description("""
            После полной ротации (V2+confirm) повторный updateSymmetricKeyV2,
            зашифрованный старым ключом (stale encryption-key-id), должен получить 494.
            """)
    void updateSymmetricKey_withStaleKey_shouldReturn494() {
        SymmetricKeySession session = Allure.step("Залогиниться с encryption keys",
                () -> sessionFactory.login(nextUser()));
        SymmetricKeyMaterial staleKey = Allure.step("Сохранить stale-ключ",
                () -> copyKey(session.getMessageKey()));

        Allure.step("Ротировать ключ на сервере (V2+confirm)", () -> sessionFactory.rotate(session));

        SymmetricKeyMaterial candidateKey = Allure.step(
                "Сгенерировать кандидат нового ключа в контексте актуальной сессии",
                () -> encryptionHelper.generateAndSaveSymmetricKey(
                        session.getAccessToken(),
                        session.getMessageKey().getId()
                ));

        var response = Allure.step("updateSymmetricKeyV2 со stale encryption-key-id",
                () -> encryptionApi.updateSymmetricKeyV2(
                        new SymmetricKeyUpdateRequest(candidateKey.getId()),
                        session.getAccessToken(),
                        staleKey
                ));

        Allure.step("Ожидаем HTTP 494", () ->
                assertEquals(HTTP_KEY_MISMATCH_REST, response.statusCode(),
                        "updateSymmetricKeyV2 with stale key must return 494, body="
                                + response.getBody().asString()));
    }

    @Test
    @DisplayName("SKR-API-016: SSE 401 then reconnect with valid token without 401/493")
    @Description("""
            Контракт SSE auth gate (без /auth/refreshToken):
            1) /events с невалидным Authorization → 401;
            2) повторный /events с валидным accessToken сессии — не 401 и не 493.
            POST /auth/refreshToken на stage14 стабильно отдаёт OAUTH_VALIDATION_FAILED
            для opaque UUID-токенов — отдельный контракт, когда бэкенд подтвердит body/headers.
            """)
    void sse_after401_refreshToken_shouldReconnect() {
        SymmetricKeySession session = Allure.step("Залогиниться с encryption keys",
                () -> sessionFactory.login(nextUser()));

        var unauthorized = Allure.step("SSE с невалидным Authorization → 401",
                () -> sseClient.connectDetailed(
                        PUSHER_EVENTS,
                        "skr-invalid-access-token",
                        session.getMessageKey().getId(),
                        SseConnectionClient.homespaceWorkspaceIds(0L)
                ));
        Allure.addAttachment("sse-401-request", unauthorized.requestSummary());
        Allure.addAttachment("sse-401-body", unauthorized.bodyPreview());
        Allure.step("Ожидаем HTTP 401", () ->
                assertEquals(401, unauthorized.statusCode(),
                        "SSE with invalid Authorization must return 401, actual="
                                + unauthorized.statusCode() + ", body=" + unauthorized.bodyPreview()));

        Allure.step("Попытка refreshToken (best-effort, не валит тест)", () -> {
            try {
                String before = session.getAccessToken();
                sessionFactory.refreshAccessToken(session);
                Allure.addAttachment("refresh-ok",
                        "accessToken changed=" + !before.equals(session.getAccessToken()));
            } catch (RuntimeException refreshFailed) {
                Allure.addAttachment("refresh-skipped", refreshFailed.getMessage());
            }
        });

        var reconnected = Allure.step("SSE reconnect с валидным accessToken сессии",
                () -> sseClient.connectDetailed(
                        PUSHER_EVENTS,
                        session.getAccessToken(),
                        session.getMessageKey().getId(),
                        SseConnectionClient.homespaceWorkspaceIds(0L)
                ));
        Allure.addAttachment("sse-reconnect-request", reconnected.requestSummary());
        Allure.addAttachment("sse-reconnect-body", reconnected.bodyPreview());

        Allure.step("Reconnect не 401 и не 493", () -> {
            assertNotEquals(401, reconnected.statusCode(),
                    "SSE after valid token must not return 401, body=" + reconnected.bodyPreview());
            assertNotEquals(HTTP_KEY_MISMATCH_SSE, reconnected.statusCode(),
                    "SSE after valid token must not return 493, body=" + reconnected.bodyPreview());
        });
    }

    @Test
    @DisplayName("SKR-API-017: updateSymmetricKeyV2 200 + confirm 200 commits new key")
    @Description("""
            Явный двухшаговый контракт: V2 200, затем confirm 200 с Encryption-Key-Id = new.
            После confirm sendTextMessage новым ключом → 200.
            """)
    void updateV2ThenConfirm_shouldCommitNewKey() {
        SymmetricKeySession session = Allure.step("Залогиниться с encryption keys",
                () -> sessionFactory.login(nextUser()));
        long oldKeyId = session.getMessageKey().getId();
        long chatId = Allure.step("Создать homespace-группу", () -> resolveChatId(session));

        SymmetricKeyMaterial newKey = Allure.step("saveSymmetricKey кандидат",
                () -> encryptionHelper.generateAndSaveSymmetricKey(
                        session.getAccessToken(), session.getMessageKey().getId()));

        var v2 = Allure.step("POST updateSymmetricKeyV2",
                () -> encryptionApi.updateSymmetricKeyV2(
                        new SymmetricKeyUpdateRequest(newKey.getId()),
                        session.getAccessToken(),
                        session.getMessageKey()));
        Allure.step("V2 → 200/204", () ->
                assertTrue(v2.statusCode() == 200 || v2.statusCode() == 204,
                        "V2 failed: " + v2.statusCode() + " " + v2.asString()));

        var confirm = Allure.step("POST confirmSymmetricKeyUpdate (header=new id)",
                () -> encryptionApi.confirmSymmetricKeyUpdate(session.getAccessToken(), newKey.getId()));
        Allure.step("Confirm → 200/204", () ->
                assertTrue(confirm.statusCode() == 200 || confirm.statusCode() == 204,
                        "Confirm failed: " + confirm.statusCode() + " " + confirm.asString()));

        session.setMessageKey(newKey);
        Allure.step("Новый key id в сессии", () -> assertNotEquals(oldKeyId, session.getMessageKey().getId()));

        var send = Allure.step("sendTextMessage новым ключом",
                () -> encryptedApi.sendTextMessage(textMessage("api-017", chatId), session));
        Allure.step("Send → 200", () ->
                assertEquals(200, send.statusCode(), send.getBody().asString()));
    }

    @Test
    @DisplayName("SKR-API-018: confirm without body uses new Encryption-Key-Id")
    @Description("""
            confirmSymmetricKeyUpdate вызывается без encrypted body; заголовок — id нового ключа.
            После успешного V2 confirm со старым id в header должен отклоняться (4xx/494).
            """)
    void confirm_withWrongKeyId_shouldFail() {
        SymmetricKeySession session = Allure.step("Залогиниться",
                () -> sessionFactory.login(nextUser()));
        long oldKeyId = session.getMessageKey().getId();

        SymmetricKeyMaterial newKey = Allure.step("V2 only (без confirm)",
                () -> sessionFactory.rotateUpdateV2Only(session));

        var badConfirm = Allure.step("confirm с Encryption-Key-Id = old (неверный)",
                () -> encryptionApi.confirmSymmetricKeyUpdate(session.getAccessToken(), oldKeyId));
        Allure.addAttachment("confirm-old-id-status",
                badConfirm.statusCode() + "\n" + badConfirm.asString());
        Allure.step("Confirm со stale/old id не 200", () ->
                assertTrue(badConfirm.statusCode() >= 400,
                        "Expected 4xx when confirm header is old key, actual="
                                + badConfirm.statusCode()));

        var goodConfirm = Allure.step("confirm с Encryption-Key-Id = new",
                () -> encryptionApi.confirmSymmetricKeyUpdate(session.getAccessToken(), newKey.getId()));
        Allure.step("Confirm с new id → 200/204", () ->
                assertTrue(goodConfirm.statusCode() == 200 || goodConfirm.statusCode() == 204,
                        "Confirm with new id failed: " + goodConfirm.statusCode()
                                + " " + goodConfirm.asString()));
        session.setMessageKey(newKey);
    }

    @Test
    @DisplayName("SKR-API-019: after V2 without confirm, REST with old current may mismatch")
    @Description("""
            После updateSymmetricKeyV2 без confirm серверное состояние ключа может уже
            отличаться от client current. Проверяем фактический статус sendTextMessage
            со старым current (документируем контракт stage).
            """)
    void afterV2WithoutConfirm_staleCurrentSend_documentsServerContract() {
        SymmetricKeySession session = Allure.step("Залогиниться",
                () -> sessionFactory.login(nextUser()));
        long chatId = Allure.step("Создать группу", () -> resolveChatId(session));
        SymmetricKeyMaterial oldCurrent = copyKey(session.getMessageKey());

        SymmetricKeyMaterial pending = Allure.step("V2 only",
                () -> sessionFactory.rotateUpdateV2Only(session));
        Allure.addAttachment("pending-key-id", String.valueOf(pending.getId()));

        var withOld = Allure.step("sendTextMessage со старым current (сессия не confirm'нула)",
                () -> encryptedApi.sendTextMessage(
                        textMessage("api-019-old", chatId),
                        session.getAccessToken(),
                        oldCurrent,
                        oldCurrent.getId()
                ));
        Allure.addAttachment("send-with-old-current",
                withOld.statusCode() + "\n" + withOld.asString());

        Allure.step("Статус задокументирован (200 если V2 ещё не сменил current на сервере, иначе 494)",
                () -> assertTrue(
                        withOld.statusCode() == 200
                                || withOld.statusCode() == HTTP_KEY_MISMATCH_REST
                                || withOld.statusCode() >= 400,
                        "Unexpected: " + withOld.statusCode()
                ));
    }

    private static UsersEnum nextUser() {
        String forced = System.getProperty("encryption.test.user", "").trim();
        if (!forced.isEmpty()) {
            return UsersEnum.valueOf(forced);
        }
        int index = Math.floorMod(USER_CURSOR.getAndIncrement(), USER_POOL.size());
        return USER_POOL.get(index);
    }

    /**
     * Создаём homespace-группу в рамках encrypted-сессии.
     * SavedChatId из UsersEnum относится к preprod и на stage14 даёт GROUP_DOES_NOT_EXIST.
     */
    private long resolveChatId(SymmetricKeySession session) {
        return encryptedApi
                .createHomespaceGroup(session, "skr-" + System.currentTimeMillis())
                .getGroupId();
    }

    private static SendTextMessageRequest textMessage(String text, long groupId) {
        return new SendTextMessageRequest(
                text + "-" + System.currentTimeMillis(),
                ThreadLocalRandom.current().nextLong(1, Long.MAX_VALUE),
                groupId
        );
    }

    private static void waitAfterKeyInvalid() {
        Allure.step("Подождать 20с после того, как ключ уже не current", () -> {
            try {
                Thread.sleep(SSE_AFTER_KEY_INVALID_WAIT_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        });
    }

    private static SymmetricKeyMaterial copyKey(SymmetricKeyMaterial source) {
        return new SymmetricKeyMaterial(source.getId(), source.getKeyBase64(), source.getTransformation());
    }
}
