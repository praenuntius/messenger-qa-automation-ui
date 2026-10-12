package org.foxtrot.mobile.network;

import io.appium.mitmproxy.InterceptedMessage;
import io.appium.mitmproxy.MitmproxyJava;
import org.aeonbits.owner.ConfigFactory;
import org.foxtrot.mobile.config.MobileConfig;
import org.foxtrot.mobile.core.emulator.EmulatorUtils;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.foxtrot.mobile.config.MobilePlatformResolver.isAndroid;
import static org.foxtrot.mobile.utils.CommandLineManager.startCMDProcessWithResponse;

public final class MitmProxySession {

    private static final MobileConfig CONFIG = ConfigFactory.create(MobileConfig.class);
    private static final Path MITM_CA = Path.of(System.getProperty("user.home"), ".mitmproxy", "mitmproxy-ca-cert.pem");
    private static final int BODY_LIMIT = 8_192;
    private static final Pattern HARDWARE_PORT = Pattern.compile("Hardware Port:\\s*(.+)");
    private static final Pattern DEVICE = Pattern.compile("Device:\\s*(\\S+)");

    private static final CopyOnWriteArrayList<NetworkCaptureEntry> CAPTURED = new CopyOnWriteArrayList<>();
    private static final CopyOnWriteArrayList<Mock> MOCKS = new CopyOnWriteArrayList<>();

    private static volatile MitmproxyJava proxy;
    private static volatile boolean deviceProxyApplied;
    private static volatile String appliedMacosService;

    private MitmProxySession() {
    }

    public static synchronized void start() {
        if (isAndroid()) {
            throw new IllegalStateException("Network capture поддерживается только на iOS");
        }
        if (isRunning()) {
            return;
        }
        if (!Files.isRegularFile(MITM_CA)) {
            throw new IllegalStateException("mitmproxy CA не найден: " + MITM_CA);
        }
        int port = CONFIG.networkProxyPort();
        try {
            proxy = new MitmproxyJava(resolveMitmdumpPath(), MitmProxySession::onMessage, port, null);
            proxy.start();
            applyIosProxy(port);
            deviceProxyApplied = true;
        } catch (Exception e) {
            rollbackStart();
            throw new IllegalStateException("Не удалось стартовать network capture: " + e.getMessage(), e);
        }
    }

    public static synchronized void stop() {
        clearMocks();
        clearCaptured();
        Exception firstError = null;
        if (deviceProxyApplied) {
            try {
                clearIosProxy();
            } catch (Exception e) {
                firstError = e;
            }
            deviceProxyApplied = false;
        }
        if (proxy != null) {
            try {
                proxy.stop();
            } catch (Exception e) {
                if (firstError == null) {
                    firstError = e;
                }
            }
            proxy = null;
        }
        if (firstError != null) {
            throw new IllegalStateException("Ошибка при остановке network capture: " + firstError.getMessage(),
                    firstError);
        }
    }

    public static boolean isRunning() {
        return proxy != null;
    }

    public static void mockResponse(String urlContains, int statusCode, String body) {
        MOCKS.add(new Mock(urlContains, statusCode, body));
    }

    public static void clearMocks() {
        MOCKS.clear();
    }

    public static List<NetworkCaptureEntry> getCaptured() {
        return List.copyOf(CAPTURED);
    }

    public static void clearCaptured() {
        CAPTURED.clear();
    }

    private static void rollbackStart() {
        try {
            if (deviceProxyApplied || appliedMacosService != null) {
                clearIosProxy();
            }
        } catch (Exception ignored) {
        }
        deviceProxyApplied = false;
        if (proxy != null) {
            try {
                proxy.stop();
            } catch (Exception ignored) {
            }
            proxy = null;
        }
    }

    private static InterceptedMessage onMessage(InterceptedMessage message) {
        boolean mocked = applyMock(message);
        CAPTURED.add(toEntry(message, mocked));
        return message;
    }

    private static boolean applyMock(InterceptedMessage message) {
        if (message.getRequest() == null || message.getResponse() == null) {
            return false;
        }
        String url = message.getRequest().getUrl();
        for (Mock mock : MOCKS) {
            if (url == null || !url.contains(mock.urlContains())) {
                continue;
            }
            InterceptedMessage.Response response = message.getResponse();
            response.setStatusCode(mock.statusCode());
            byte[] bytes = mock.body().getBytes(StandardCharsets.UTF_8);
            response.setBody(bytes);
            setHeader(response, "Content-Type", "application/json");
            setHeader(response, "Content-Length", String.valueOf(bytes.length));
            return true;
        }
        return false;
    }

    private static void setHeader(InterceptedMessage.Response response, String name, String value) {
        List<String[]> headers = response.getHeaders();
        if (headers == null) {
            headers = new ArrayList<>();
            response.setHeaders(headers);
        }
        for (int i = 0; i < headers.size(); i++) {
            String[] pair = headers.get(i);
            if (pair != null && pair.length >= 2 && name.equalsIgnoreCase(pair[0])) {
                headers.set(i, new String[]{pair[0], value});
                return;
            }
        }
        headers.add(new String[]{name, value});
    }

    private static NetworkCaptureEntry toEntry(InterceptedMessage message, boolean mocked) {
        InterceptedMessage.Request request = message.getRequest();
        InterceptedMessage.Response response = message.getResponse();
        return new NetworkCaptureEntry(
                request == null ? null : request.getMethod(),
                request == null ? null : request.getUrl(),
                truncate(asText(request == null ? null : request.getBody())),
                response == null ? -1 : response.getStatusCode(),
                truncate(asText(response == null ? null : response.getBody())),
                mocked);
    }

    private static String asText(byte[] body) {
        return body == null || body.length == 0 ? "" : new String(body, StandardCharsets.UTF_8);
    }

    private static String truncate(String value) {
        if (value == null) {
            return "";
        }
        return value.length() <= BODY_LIMIT ? value : value.substring(0, BODY_LIMIT) + "...[truncated]";
    }

    private static String resolveMitmdumpPath() {
        String configured = CONFIG.networkMitmdumpPath();
        Path path = configured == null || configured.isBlank() ? null : Path.of(configured.trim());
        if (path == null || !Files.isExecutable(path)) {
            throw new IllegalStateException("Некорректный mobile.network.mitmdump.path: " + configured);
        }
        return path.toAbsolutePath().toString();
    }

    private static void applyIosProxy(int port) {
        installIosCa();
        String service = resolveMacosService();
        appliedMacosService = service;
        startCMDProcessWithResponse("networksetup -setproxybypassdomains \"" + service
                + "\" 127.0.0.1 localhost ::1 \"*.local\" 169.254/16");
        startCMDProcessWithResponse("networksetup -setwebproxy \"" + service + "\" 127.0.0.1 " + port);
        startCMDProcessWithResponse("networksetup -setsecurewebproxy \"" + service + "\" 127.0.0.1 " + port);
        startCMDProcessWithResponse("networksetup -setwebproxystate \"" + service + "\" on");
        startCMDProcessWithResponse("networksetup -setsecurewebproxystate \"" + service + "\" on");
    }

    private static void clearIosProxy() {
        String service = appliedMacosService != null ? appliedMacosService : resolveMacosService();
        startCMDProcessWithResponse("networksetup -setwebproxystate \"" + service + "\" off");
        startCMDProcessWithResponse("networksetup -setsecurewebproxystate \"" + service + "\" off");
        startCMDProcessWithResponse(
                "networksetup -setproxybypassdomains \"" + service + "\" \"*.local\" 169.254/16");
        appliedMacosService = null;
    }

    private static void installIosCa() {
        String udid = EmulatorUtils.getIosSimulatorUdid();
        startCMDProcessWithResponse("xcrun simctl keychain " + udid + " add-root-cert \"" + MITM_CA + "\"");
    }

    private static String resolveMacosService() {
        String iface = startCMDProcessWithResponse("route -n get default | awk '/interface:/{print $2}'").trim();
        if (iface.isBlank()) {
            throw new IllegalStateException("Не удалось получить default route interface");
        }
        String service = findServiceForDevice(iface);
        if (service == null) {
            throw new IllegalStateException("Не найден macOS network service для " + iface);
        }
        return service;
    }

    private static String findServiceForDevice(String device) {
        String output = startCMDProcessWithResponse("networksetup -listallhardwareports");
        String currentPort = null;
        for (String raw : output.split("\\R")) {
            String line = raw.trim();
            Matcher port = HARDWARE_PORT.matcher(line);
            if (port.matches()) {
                currentPort = port.group(1).trim();
                continue;
            }
            Matcher dev = DEVICE.matcher(line);
            if (dev.matches() && device.equalsIgnoreCase(dev.group(1)) && currentPort != null) {
                return currentPort;
            }
        }
        for (String preferred : List.of("Wi-Fi", "Ethernet")) {
            for (String line : startCMDProcessWithResponse("networksetup -listallnetworkservices").split("\\R")) {
                String name = line.trim();
                if (!name.isEmpty() && !name.startsWith("*") && !name.toLowerCase(Locale.ROOT).contains("asterisk")
                        && preferred.equalsIgnoreCase(name)) {
                    return name;
                }
            }
        }
        return null;
    }
}
