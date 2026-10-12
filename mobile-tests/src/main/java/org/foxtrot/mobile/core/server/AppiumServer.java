package org.foxtrot.mobile.core.server;

import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.appium.java_client.service.local.flags.GeneralServerFlag;
import org.aeonbits.owner.ConfigFactory;
import org.foxtrot.mobile.config.MobileConfig;
import org.foxtrot.mobile.core.application.devices.DeviceManager;
import org.foxtrot.utils.Waits;
import org.openqa.selenium.os.ExecutableFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.Objects;
import java.util.regex.Pattern;

import static org.foxtrot.mobile.config.MobilePlatformResolver.isAndroid;

public class AppiumServer {

    private final AppiumDriverLocalService server;
    private static AppiumServer instance;
    private static final String PATH = "PATH";
    private static final String SERVER_LOG_LEVEL = "warn";
    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(3))
            .build();
    private static final Logger logger = LoggerFactory.getLogger(AppiumServer.class);
    private static final MobileConfig config = ConfigFactory.create(MobileConfig.class);

    private AppiumServer() {
        String appiumServerIp = config.appiumServerIp();
        int port = ServerUtils.getAvailablePort();
        ExecutableFinder executableFinder = new ExecutableFinder();
        HashMap<String, String> environment = new HashMap<>();
        environment.put(PATH, System.getenv(PATH));
        AppiumServiceBuilder serviceBuilder = new AppiumServiceBuilder();
        serviceBuilder
                .usingDriverExecutable(new File(executableFinder.find("node")))
                .withAppiumJS(new File(executableFinder.find("appium")))
                .withIPAddress(appiumServerIp)
                .usingPort(port)
                .withEnvironment(environment)
                .withArgument(GeneralServerFlag.LOG_LEVEL, SERVER_LOG_LEVEL)
                .withArgument(GeneralServerFlag.LOG_TIMESTAMP)
                .withArgument(GeneralServerFlag.LOG_NO_COLORS)
                .withArgument(GeneralServerFlag.STRICT_CAPS)
                .withArgument(GeneralServerFlag.USE_DRIVERS,
                        DeviceManager.getEnabledDevice().getDriverName().toLowerCase());
        if (isAndroid()) {
            serviceBuilder.withArgument(GeneralServerFlag.ALLOW_INSECURE, "uiautomator2:adb_shell");
        }
        this.server = AppiumDriverLocalService.buildService(serviceBuilder);
        this.server.start();
    }

    public static synchronized AppiumServer getInstance() {
        if (instance == null) {
            instance = new AppiumServer();
        }
        return instance;
    }

    public static synchronized void restart() {
        stopInstance();
        instance = new AppiumServer();
    }

    public static synchronized void stopInstance() {
        if (instance == null) {
            return;
        }
        instance.stopServer();
        instance = null;
    }

    private void stopServer() {
        if (server != null && server.isRunning()) {
            server.stop();
        }
    }

    public AppiumDriverLocalService get() {
        return this.server;
    }

    /**
     * Ожидает, пока Appium Server станет доступен и начнет возвращать готовый статус.
     *
     * @param baseUrl базовый URI Appium Server
     */
    public void waitForAppiumServer(URI baseUrl) {
        Objects.requireNonNull(baseUrl, "Appium Server URL must not be null");
        int maxAttempts = 5;
        for (int i = 1; i <= maxAttempts; i++) {
            if (isAppiumReady(baseUrl)) {
                logger.info("Appium Server status check прошел успешно: {}", baseUrl);
                return;
            }
            logger.debug("Appium Server status check не пройден: {}. Попытка {}/{})", baseUrl, i, maxAttempts);
            if (i < maxAttempts) {
                Waits.sleepSeconds(5); //Ожидание перед повторным status check
            }
        }
        throw new IllegalStateException("Appium Server status check не пройден за %s попыток".formatted(maxAttempts));
    }

    /**
     * Проверяет, что Appium Server отвечает по одному из status endpoint'ов
     * и возвращает JSON с полем {@code ready : true}.
     *
     * @param baseUrl базовый URI Appium Server
     * @return {@code true}, если сервер доступен и готов; иначе {@code false}
     */
    private boolean isAppiumReady(URI baseUrl) {
        for (URI statusUri : buildStatusUris(baseUrl)) {
            try {
                HttpRequest request = HttpRequest.newBuilder(statusUri)
                        .timeout(Duration.ofSeconds(5))
                        .GET()
                        .build();
                HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200 &&
                        Pattern
                                .compile("\"ready\"\\s*:\\s*true")
                                .matcher(response.body()).find()) {
                    return true;
                }
            } catch (Exception e) {
                logger.debug("Appium Server status check не пройден. URL: {}. Info: {}", statusUri, e.getMessage());
            }
        }

        return false;
    }

    /**
     * Формирует список status endpoint'ов для Appium Server.
     * <p>
     * Стандартный вариант {@code /status}, затем legacy-вариант {@code /wd/hub/status}
     *
     * @param baseUrl базовый URI Appium Server
     * @return массив URI для проверки статуса сервера
     */
    private URI[] buildStatusUris(URI baseUrl) {
        String base = baseUrl.toString().replaceAll("/+$", "");
        return new URI[]{
                URI.create(base + "/status"),
                URI.create(base + "/wd/hub/status")
        };
    }
}
