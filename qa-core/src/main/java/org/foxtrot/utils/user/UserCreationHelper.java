package org.foxtrot.utils.user;

import org.aeonbits.owner.ConfigFactory;
import org.foxtrot.api.dto.auth.response.SignInResponse;
import org.foxtrot.api.service.AuthApiService;
import org.foxtrot.api.service.RegistrationApiService;
import org.foxtrot.api.service.UserAlreadyRegisteredException;
import org.foxtrot.backoffice.config.BackofficeConfig;
import org.foxtrot.backoffice.service.BackofficeAuthApiService;
import org.foxtrot.backoffice.service.BackofficeUsersApiService;
import org.foxtrot.utils.phone.PhoneNumberPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Создание тестового пользователя: backoffice (exist → whitelist) + регистрация в мессенджере.
 * <p>
 * Использование перед UI-тестом регистрации или вместо пула UsersEnum.
 */
public class UserCreationHelper {

    private static final Logger logger = LoggerFactory.getLogger(UserCreationHelper.class);
    private static final long PROCESS_NOT_FOUND_RETRY_DELAY_MS = 2000L;

    private final BackofficeAuthApiService backofficeAuthApiService;
    private final BackofficeUsersApiService backofficeUsersApiService;
    private final RegistrationApiService registrationApiService;
    private final AuthApiService authApiService;
    private final PhoneNumberPool phoneNumberPool;
    private final BackofficeConfig backofficeConfig;

    public UserCreationHelper() {
        this(
                new BackofficeAuthApiService(),
                new BackofficeUsersApiService(),
                new RegistrationApiService(),
                new AuthApiService(),
                new PhoneNumberPool(),
                ConfigFactory.create(BackofficeConfig.class)
        );
    }

    public UserCreationHelper(
            BackofficeAuthApiService backofficeAuthApiService,
            BackofficeUsersApiService backofficeUsersApiService,
            RegistrationApiService registrationApiService,
            AuthApiService authApiService,
            PhoneNumberPool phoneNumberPool,
            BackofficeConfig backofficeConfig
    ) {
        this.backofficeAuthApiService = backofficeAuthApiService;
        this.backofficeUsersApiService = backofficeUsersApiService;
        this.registrationApiService = registrationApiService;
        this.authApiService = authApiService;
        this.phoneNumberPool = phoneNumberPool;
        this.backofficeConfig = backofficeConfig;
    }

    /** Логин суперадмина в backoffice (JSESSIONID для exist/whitelist). */
    public void ensureBackofficeLoggedIn() {
        backofficeAuthApiService.loginAsSuperAdmin();
    }

    /**
     * Гарантирует пользователя на стенде: whitelist в backoffice + регистрация в мессенджере.
     * <p>
     * Важно: не делаем предварительный checkRegistrationStatus (start+verify) —
     * он сжигает auth-process и на части номеров приводит к PROCESS_NOT_FOUND
     * при последующей регистрации. Путь как у «нового» whitelist: сразу register.
     */
    public CreatedUser ensureUser(UserRegistrationData data) {
        ensureBackofficeLoggedIn();

        String phone = data.getPhone();
        if (phone == null || phone.isBlank()) {
            phone = phoneNumberPool.allocateFreePhone();
        }

        if (!backofficeUsersApiService.userExistsByPhone(phone)) {
            addToWhitelist(phone, data);
        } else {
            logger.info("Номер {} уже в whitelist backoffice", phone);
        }

        return registerOrSkipIfExists(phone, data);
    }

    private CreatedUser registerOrSkipIfExists(String phone, UserRegistrationData data) {
        try {
            return registerInMessenger(phone, data);
        } catch (UserAlreadyRegisteredException e) {
            logger.info("Пользователь {} уже зарегистрирован в мессенджере", phone);
            return buildExistingUser(phone, data);
        } catch (RuntimeException first) {
            if (isProcessNotFound(first)) {
                logger.warn("PROCESS_NOT_FOUND для {} — повтор регистрации через {} мс",
                        phone, PROCESS_NOT_FOUND_RETRY_DELAY_MS);
                sleep(PROCESS_NOT_FOUND_RETRY_DELAY_MS);
                try {
                    return registerInMessenger(phone, data);
                } catch (UserAlreadyRegisteredException e) {
                    logger.info("Пользователь {} уже зарегистрирован после retry", phone);
                    return buildExistingUser(phone, data);
                } catch (RuntimeException retry) {
                    if (authApiService.canLoginWithPassword(phone)) {
                        logger.info("Пользователь {} доступен по паролю после PROCESS_NOT_FOUND", phone);
                        return buildExistingUser(phone, data);
                    }
                    throw retry;
                }
            }
            if (authApiService.canLoginWithPassword(phone)) {
                logger.info("Пользователь {} уже зарегистрирован (fallback canLogin)", phone);
                return buildExistingUser(phone, data);
            }
            throw first;
        }
    }

    private static boolean isProcessNotFound(Throwable e) {
        String message = e.getMessage();
        return message != null && message.contains("PROCESS_NOT_FOUND");
    }

    private static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }

    private CreatedUser buildExistingUser(String phone, UserRegistrationData data) {
        return CreatedUser.builder()
                .phone(phone)
                .firstName(data.getFirstName())
                .lastName(data.getLastName())
                .password(orDefault(data.getPassword(), RegistrationApiService.DEFAULT_PASSWORD))
                .build();
    }

    /**
     * Полный цикл: подобрать свободный номер → whitelist → регистрация в API мессенджера.
     */
    public CreatedUser createUser() {
        String phone = phoneNumberPool.allocateFreePhone();
        return createUser(UserRegistrationData.defaultsForPhone(phone));
    }

    /**
     * Регистрация с заданными данными. Если phone не указан — выделяется из пула.
     */
    public CreatedUser createUser(UserRegistrationData data) {
        ensureBackofficeLoggedIn();

        String phone = data.getPhone();
        if (phone == null || phone.isBlank()) {
            phone = phoneNumberPool.allocateFreePhone();
        } else if (backofficeUsersApiService.userExistsByPhone(phone)) {
            throw new IllegalArgumentException("Номер " + phone + " уже существует в системе");
        }

        addToWhitelist(phone, data);
        return registerInMessenger(phone, data);
    }

    private void addToWhitelist(String phone, UserRegistrationData data) {
        long organizationId = data.getOrganizationId() != null
                ? data.getOrganizationId()
                : backofficeConfig.backofficeOrganizationId();
        String initiator = data.getWhitelistInitiator() != null
                ? data.getWhitelistInitiator()
                : backofficeConfig.backofficeWhitelistInitiator();

        long phoneNumeric = Long.parseLong(phone.replaceAll("\\D", ""));
        logger.info("Добавляем {} в whitelist организации {}", phone, organizationId);
        backofficeUsersApiService.addPhoneToWhitelist(phoneNumeric, organizationId, initiator, "");
    }

    private CreatedUser registerInMessenger(String phone, UserRegistrationData data) {
        UserRegistrationData defaults = UserRegistrationData.defaultsForPhone(phone);
        UserRegistrationData effective = data.toBuilder()
                .phone(phone)
                .firstName(orDefault(data.getFirstName(), defaults.getFirstName()))
                .lastName(orDefault(data.getLastName(), defaults.getLastName()))
                .recoveryEmail(orDefault(data.getRecoveryEmail(), defaults.getRecoveryEmail()))
                .password(orDefault(data.getPassword(), defaults.getPassword()))
                .smsCode(orDefault(data.getSmsCode(), defaults.getSmsCode()))
                .emailVerificationCode(orDefault(data.getEmailVerificationCode(), defaults.getEmailVerificationCode()))
                .build();

        logger.info("Регистрируем пользователя {} через API мессенджера", phone);
        SignInResponse signInResponse = registrationApiService.performRegistration(
                phone,
                effective.getFirstName(),
                effective.getLastName(),
                effective.getRecoveryEmail(),
                effective.getPassword(),
                effective.getSmsCode(),
                effective.getEmailVerificationCode(),
                RegistrationApiService.DEFAULT_ENCRYPTION_KEY_ID,
                effective.isSetupRealEncryptionKeys()
        );

        Long accountId = signInResponse.getAccount() != null ? signInResponse.getAccount().getId() : null;
        logger.info("Пользователь создан: phone={}, accountId={}", phone, accountId);

        return CreatedUser.builder()
                .phone(phone)
                .firstName(effective.getFirstName())
                .lastName(effective.getLastName())
                .recoveryEmail(effective.getRecoveryEmail())
                .password(effective.getPassword() != null ? effective.getPassword() : RegistrationApiService.DEFAULT_PASSWORD)
                .accountId(accountId)
                .accessToken(signInResponse.getAccessToken())
                .signInResponse(signInResponse)
                .build();
    }

    private static String orDefault(String value, String defaultValue) {
        return value != null && !value.isBlank() ? value : defaultValue;
    }
}
