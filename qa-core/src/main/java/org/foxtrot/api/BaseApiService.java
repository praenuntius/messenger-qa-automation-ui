package org.foxtrot.api;

import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.aeonbits.owner.ConfigFactory;
import org.foxtrot.api.auth.ApiAuthContext;
import org.foxtrot.api.auth.ApiAuthContextHolder;
import org.foxtrot.api.auth.DeviceHashProvider;
import org.foxtrot.api.service.AuthApiService;
import org.foxtrot.config.CoreConfig;
import org.foxtrot.enums.UsersEnum;
import org.foxtrot.utils.ProcessIdRefresher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import static io.restassured.RestAssured.given;

public class BaseApiService {
    private static final Logger log = LoggerFactory.getLogger(BaseApiService.class);
    private static final CoreConfig config = ConfigFactory.create(CoreConfig.class);
    private static final int MAX_401_RETRY_ATTEMPTS = 2;
    private static final int MAX_NETWORK_RETRY_ATTEMPTS = 3;

    private static final ThreadLocal<String> cachedToken = new ThreadLocal<>();
    private static final ThreadLocal<String> cachedUser = new ThreadLocal<>();

    protected RequestSpecification getRequestSpecWithOutAuth() {
        return given()
                .filter(new SmartLoggingFilter())
                .baseUri(config.baseApiUrl())
                .header("X-Disable-Encryption", true)
                .contentType("application/json");
    }

    protected RequestSpecification getBaseRequestSpec(String user) {
        RestAssuredConfig configWithTimeouts = RestAssured.config()
                .httpClient(HttpClientConfig.httpClientConfig()
                        .dontReuseHttpClientInstance()
                        .httpClientFactory(BaseApiService::createHttpClient)
                        .setParam("http.connection.timeout", 60000)
                        .setParam("http.socket.timeout", 60000)
                        .setParam("http.connection-manager.timeout", 60000)
                        .setParam("http.connection.stalecheck", true)
                        .setParam("http.protocol.expect-continue", false)
                );
        ApiAuthContext auth = getOrCreateAuthContext(user);
        String token = auth.getToken();

        return given()
                .config(configWithTimeouts)
                .filter(new SmartLoggingFilter())
                .filter((request, response, ctx) -> {
                    int attempts = 0;
                    Response res = null;

                    while (attempts < Math.max(MAX_401_RETRY_ATTEMPTS, MAX_NETWORK_RETRY_ATTEMPTS)) {
                        try {
                            res = ctx.next(request, response);
                        } catch (RuntimeException e) {
                            // Сетевые ошибки в RestAssured/HttpClient часто прилетают исключением.
                            // Даем 1-2 попытки на быстрый ретрай.
                            if (!shouldRetryNetwork(request)) {
                                throw e;
                            }

                            attempts++;
                            Throwable root = rootCause(e);
                            log.warn("🌐 API network exception (attempt {}/{}). {} {}. rootCause={}({})",
                                    attempts,
                                    MAX_NETWORK_RETRY_ATTEMPTS,
                                    request.getMethod(),
                                    request.getURI(),
                                    root.getClass().getName(),
                                    root.getMessage());

                            if (attempts >= MAX_NETWORK_RETRY_ATTEMPTS) {
                                throw e;
                            }

                            sleepSilently(networkBackoffMs(attempts));
                            continue;
                        }

                        if (res == null) {
                            if (!shouldRetryNetwork(request)) {
                                throw new RuntimeException("API returned empty response (null) - network timeout/connection reset");
                            }

                            attempts++;
                            log.warn("🌐 API null response (attempt {}/{}). {} {}",
                                    attempts,
                                    MAX_NETWORK_RETRY_ATTEMPTS,
                                    request.getMethod(),
                                    request.getURI());

                            if (attempts >= MAX_NETWORK_RETRY_ATTEMPTS) {
                                throw new RuntimeException("API returned empty response after retries (network timeout/connection reset). " +
                                        request.getMethod() + " " + request.getURI());
                            }

                            sleepSilently(networkBackoffMs(attempts));
                            continue;
                        }

                        if (res.statusCode() != 401) {
                            return res;
                        }

                        // 401 retry отдельно от сетевых ретраев
                        if (attempts >= MAX_401_RETRY_ATTEMPTS) {
                            return res;
                        }

                        ApiAuthContext current = getOrCreateAuthContext(user);
                        String newToken = new AuthApiService().fetchNewTokenProcessId(user, current.getDeviceHash());
                        current.setToken(newToken);
                        ApiAuthContextHolder.set(current);
                        cachedToken.set(newToken);
                        cachedUser.set(user);
                        request.removeHeader("Authorization");
                        request.header("Authorization", newToken);
                        attempts++;
                    }
                    return res;
                })
                .baseUri(config.baseApiUrl())
                .header("Authorization", token)
                .header("X-Disable-Encryption", true)
                .contentType("application/json");
    }

    private ApiAuthContext getOrCreateAuthContext(String user) {
        ApiAuthContext ctx = ApiAuthContextHolder.get();
        if (ctx != null && user.equals(ctx.getUserKey()) && ctx.getToken() != null) {
            return ctx;
        }

        String token = cachedToken.get();
        String userKey = cachedUser.get();
        String deviceHash = (ctx != null && user.equals(ctx.getUserKey()))
                ? ctx.getDeviceHash()
                : DeviceHashProvider.forUserKey(user);

        if (token != null && user.equals(userKey)) {
            ApiAuthContext newCtx = new ApiAuthContext(user, deviceHash, token);
            ApiAuthContextHolder.set(newCtx);
            return newCtx;
        }

        token = fetchTokenForUserKey(user, deviceHash);
        cachedToken.set(token);
        cachedUser.set(user);
        ApiAuthContext newCtx = new ApiAuthContext(user, deviceHash, token);
        ApiAuthContextHolder.set(newCtx);
        return newCtx;
    }

    public static void clearTokenCache() {
        cachedToken.remove();
        cachedUser.remove();
        ApiAuthContextHolder.clear();
    }

    private static void sleepSilently(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }

    private static long networkBackoffMs(int attempt) {
        // быстрый backoff: 200ms, 500ms, 1000ms
        return switch (attempt) {
            case 1 -> 200L;
            case 2 -> 500L;
            default -> 1000L;
        };
    }

    private static boolean shouldRetryNetwork(io.restassured.specification.FilterableRequestSpecification request) {
        Object body = request.getBody();
        return body == null || body.toString().isBlank();
    }

    private static Throwable rootCause(Throwable t) {
        Throwable cur = t;
        while (cur.getCause() != null && cur.getCause() != cur) {
            cur = cur.getCause();
        }
        return cur;
    }

    private static HttpClient createHttpClient() {
        // RestAssured 5.5.0 внутри RequestSpecificationImpl.groovy приводит HttpClient к AbstractHttpClient,
        // поэтому возвращаем DefaultHttpClient (наследник AbstractHttpClient), иначе будет GroovyCastException.
        PoolingClientConnectionManager cm = new PoolingClientConnectionManager();
        cm.setMaxTotal(50);
        cm.setDefaultMaxPerRoute(25);

        DefaultHttpClient client = new DefaultHttpClient(cm);
        HttpParams params = client.getParams();
        // базовые таймауты (дублируем на уровне клиента, чтобы работало даже если RestAssured params не подхватятся)
        HttpConnectionParams.setConnectionTimeout(params, 60000);
        HttpConnectionParams.setSoTimeout(params, 60000);
        params.setBooleanParameter("http.connection.stalecheck", true);
        params.setBooleanParameter("http.protocol.expect-continue", false);
        return client;
    }

    private static String fetchTokenForUserKey(String userKey, String deviceHash) {
        // Большая часть framework вызывает getBaseRequestSpec(user.getProcessId()).
        // Старые processId могли быть получены с одинаковым "hash", поэтому signIn(processId) может перетирать сессии в параллели.
        // Если userKey совпадает с UsersEnum.processId — получаем токен через свежий auth-flow по телефону и deviceHash.
        return UsersEnum.tryFindByProcessId(userKey)
                .map(u -> new ProcessIdRefresher().fetchFreshToken(u, deviceHash))
                .orElseGet(() -> new AuthApiService().fetchNewTokenProcessId(userKey, deviceHash));
    }
}
