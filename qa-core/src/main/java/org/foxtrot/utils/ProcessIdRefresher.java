package org.foxtrot.utils;

import io.restassured.response.Response;
import org.foxtrot.api.dto.auth.SignInRequest;
import org.foxtrot.api.dto.auth.StartRequest;
import org.foxtrot.api.dto.auth.VerifyCodeRequest;
import org.foxtrot.api.dto.auth.VerifyPasswordRequest;
import org.foxtrot.api.service.AuthApiService;
import org.foxtrot.enums.UsersEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.apache.http.HttpStatus.SC_OK;

/**
 * Утилита для обновления processId пользователей.
 * 1) Выполняет полный флоу авторизации: start → verifyCode → verifyPassword → signIn.
 * 2) Возвращает полученный processId для использования во фреймворке.
 * 3) Может перезаписать processId в исходном файле UsersEnum.java.
 */
public class ProcessIdRefresher {

    private static final Logger logger = LoggerFactory.getLogger(ProcessIdRefresher.class);
    private static final Path USERS_ENUM_PATH = Path.of("src/main/java/org/foxtrot/enums/UsersEnum.java");

    private final AuthApiService authApiService = new AuthApiService();

    /**
     * Выполняет полный флоу авторизации для пользователя и возвращает актуальный processId.
     * Логика:
     * 1. start(phone) → processId
     * 2. verifyCode(processId)
     * 3. verifyPassword(processId)
     * 4. signIn(processId)
     */
    public String fetchFreshProcessId(UsersEnum user) {
        return fetchFreshProcessId(user, "hash", "deviceInfo");
    }

    public String fetchFreshProcessId(UsersEnum user, String deviceHash, String deviceInfo) {
        String rawPhone = user.getPhoneNumber();
        String phone = normalizePhone(rawPhone);
        logger.info("Запрос нового processId для пользователя {} (номер {})", user.name(), phone);

        StartRequest startRequest = new StartRequest(
                new StartRequest.AuthWay("PHONE", phone),
                deviceHash,
                "ru",
                deviceInfo
        );

        Response startResponse = authApiService.startAuth(startRequest);
        if (startResponse.statusCode() != SC_OK) {
            throw new RuntimeException("Не удалось выполнить auth/start для " + phone
                    + ": статус " + startResponse.statusCode());
        }
        String processId = startResponse.jsonPath().getString("processId");
        if (processId == null || processId.isBlank()) {
            throw new RuntimeException("Пустой processId в ответе auth/start для " + phone);
        }

        logger.debug("Получен processId={} для {}", processId, phone);

        authApiService.verifyCode(new VerifyCodeRequest(processId, "111111"));
        authApiService.verifyPassword(new VerifyPasswordRequest(processId, "111111"));

        Response signInResponse = authApiService.signIn(new SignInRequest(processId, deviceInfo));
        if (signInResponse.statusCode() != SC_OK) {
            String body = signInResponse.getBody() != null ? signInResponse.getBody().asString() : "";
            if (body.contains("WORKSPACE_DOES_NOT_EXIST")) {
                UserPoolManager.quarantine(user);
            }
            throw new RuntimeException("Не удалось выполнить signIn для " + phone
                    + ": статус " + signInResponse.statusCode()
                    + (body.isBlank() ? "" : ", body=" + body));
        }

        logger.info("Флоу авторизации успешно выполнен для {}, processId={}", phone, processId);
        return processId;
    }

    /**
     * То же что fetchFreshProcessId(...), но возвращает accessToken.
     * Нужен для параллельных API тестов, чтобы не зависеть от захардкоженных processId в enum.
     */
    public String fetchFreshToken(UsersEnum user, String deviceHash) {
        String deviceInfo = buildDeviceInfo(deviceHash);
        String processId = fetchFreshProcessId(user, deviceHash, deviceInfo);
        // signIn уже выполнен внутри fetchFreshProcessId, но accessToken проще взять отдельным вызовом:
        // этот вызов будет идти по свежему processId и с тем же deviceInfo/deviceHash.
        return authApiService.fetchNewTokenProcessId(processId, deviceHash);
    }

    /**
     * Обновляет processId указанных пользователей в исходном файле UsersEnum.java.
     * Для каждого пользователя:
     * 1. Получает fresh processId через fetchFreshProcessId.
     * 2. Находит соответствующий константе фрагмент в файле UsersEnum.
     * 3. Меняет старый processId на новый.
     */
    public void refreshProcessIdsInUsersEnum(UsersEnum... users) {
        List<UsersEnum> userList = Arrays.asList(users);
        if (userList.isEmpty()) {
            logger.info("Список пользователей пуст, обновлять нечего");
            return;
        }

        try {
            String content = Files.readString(USERS_ENUM_PATH);
            String updatedContent = content;

            for (UsersEnum user : userList) {
                String oldProcessId = user.getProcessId();
                String newProcessId = fetchFreshProcessId(user);

                String oldSegment = String.format(
                        "%s(\"%s\", %dL, \"%s\", \"%s\")",
                        user.name(),
                        user.getPhoneNumber(),
                        user.getId(),
                        oldProcessId,
                        user.getName()
                );

                String newSegment = String.format(
                        "%s(\"%s\", %dL, \"%s\", \"%s\")",
                        user.name(),
                        user.getPhoneNumber(),
                        user.getId(),
                        newProcessId,
                        user.getName()
                );

                if (!updatedContent.contains(oldSegment)) {
                    logger.warn("Не удалось найти запись enum для {} в UsersEnum.java, пропускаем", user.name());
                    continue;
                }

                updatedContent = updatedContent.replace(oldSegment, newSegment);
                logger.info("processId для {} будет обновлён: {} → {}", user.name(), oldProcessId, newProcessId);
            }

            if (!updatedContent.equals(content)) {
                Files.writeString(USERS_ENUM_PATH, updatedContent);
                logger.info("Файл UsersEnum.java обновлён: {}", USERS_ENUM_PATH.toAbsolutePath());
            } else {
                logger.info("Изменений для UsersEnum.java нет");
            }
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при обновлении UsersEnum.java: " + e.getMessage(), e);
        }
    }

    /**
     * CLI-режим.
     * Примеры:
     *  - без аргументов: обновить processId для всех пользователей из UsersEnum;
     *  - с аргументами: java ... ProcessIdRefresher TEST20 TEST21.
     */
    public static void main(String[] args) {
        ProcessIdRefresher refresher = new ProcessIdRefresher();

        UsersEnum[] targets;
        if (args.length == 0) {
            targets = UsersEnum.values();
        } else {
            targets = Arrays.stream(args)
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(name -> {
                        try {
                            return UsersEnum.valueOf(name);
                        } catch (IllegalArgumentException e) {
                            throw new RuntimeException("Неизвестный пользователь enum: " + name, e);
                        }
                    })
                    .toArray(UsersEnum[]::new);
        }

        refresher.refreshProcessIdsInUsersEnum(targets);
    }

    private String normalizePhone(String phone) {
        if (phone == null) {
            return null;
        }
        String digits = phone.replaceAll("\\D+", "");
        if (digits.length() == 10 && digits.startsWith("9")) {
            return "7" + digits;
        }
        return digits;
    }

    private static String buildDeviceInfo(String deviceHash) {
        if (deviceHash == null || deviceHash.isBlank()) {
            return "deviceInfo";
        }
        return "deviceInfo:" + deviceHash.trim();
    }
}

