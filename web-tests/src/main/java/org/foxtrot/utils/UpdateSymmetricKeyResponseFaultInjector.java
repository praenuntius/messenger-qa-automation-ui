package org.foxtrot.utils;

import com.codeborne.selenide.Selenide;
import io.qameta.allure.Step;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Клиентский fault-injection для DF-554 rotation endpoints:
 * {@code updateSymmetricKeyV2} и/или {@code confirmSymmetricKeyUpdate}.
 * Запрос уходит на сервер; response клиенту либо уничтожается (TERMINATE),
 * либо удерживается дольше HTTP-timeout (THROTTLE).
 * <p>
 * Патчит {@code fetch} и {@code XMLHttpRequest}. Для XHR хук вешается в {@code open()}
 * (до {@code addEventListener}), иначе axios уже подписан к моменту {@code send()}.
 */
public final class UpdateSymmetricKeyResponseFaultInjector {

    public enum Mode {
        /** Первый matching response: дождаться сети, затем throw / status=0. */
        TERMINATE_FIRST_RESPONSE,
        /** Первый matching response: sleep(holdMs), затем отдать body. */
        THROTTLE_FIRST_RESPONSE
    }

    /** Какой URL матчить при fault. */
    public enum Target {
        /** Только confirmSymmetricKeyUpdate (после успешного V2). */
        CONFIRM,
        /** Только updateSymmetricKeyV2. */
        UPDATE_V2,
        /** Любой rotation-step (V2 или confirm); не legacy one-step alone. */
        ANY_ROTATION
    }

    private UpdateSymmetricKeyResponseFaultInjector() {
    }

    @Step("Install rotation response fault: {mode}, target={target}, holdMs={holdMs}")
    public static void install(Mode mode, Target target, long holdMs) {
        Selenide.executeJavaScript(INJECT_JS, mode.name(), target.name(), holdMs);
    }

    /** Backward-compatible: ANY_ROTATION. */
    @Step("Install updateSymmetricKey response fault: {mode}, holdMs={holdMs}")
    public static void install(Mode mode, long holdMs) {
        install(mode, Target.ANY_ROTATION, holdMs);
    }

    public static void uninstall() {
        Selenide.executeJavaScript("""
                if (window.__skrUpdateSkFault && window.__skrUpdateSkFault.restore) {
                  window.__skrUpdateSkFault.restore();
                }
                window.__skrUpdateSkFault = null;
                """);
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> stats() {
        Object raw = Selenide.executeJavaScript(
                "return window.__skrUpdateSkFault ? window.__skrUpdateSkFault.stats() : null;"
        );
        if (!(raw instanceof Map<?, ?> map)) {
            return Collections.emptyMap();
        }
        Map<String, Object> out = new HashMap<>();
        map.forEach((k, v) -> out.put(String.valueOf(k), v));
        return out;
    }

    public static int interceptedCount() {
        Object v = stats().get("intercepted");
        return v instanceof Number n ? n.intValue() : 0;
    }

    public static int passedCount() {
        Object v = stats().get("passed");
        return v instanceof Number n ? n.intValue() : 0;
    }

    public static int terminatedCount() {
        Object v = stats().get("terminated");
        return v instanceof Number n ? n.intValue() : 0;
    }

    public static int throttledCount() {
        Object v = stats().get("throttled");
        return v instanceof Number n ? n.intValue() : 0;
    }

    private static final String INJECT_JS = """
            (function(mode, target, holdMs) {
              if (window.__skrUpdateSkFault && window.__skrUpdateSkFault.restore) {
                window.__skrUpdateSkFault.restore();
              }
              const state = {
                mode: mode,
                target: target,
                holdMs: holdMs,
                intercepted: 0,
                passed: 0,
                terminated: 0,
                throttled: 0
              };
              const matchUrl = (url) => {
                if (!url) return false;
                const u = String(url);
                if (target === 'CONFIRM') {
                  return u.indexOf('confirmSymmetricKeyUpdate') !== -1;
                }
                if (target === 'UPDATE_V2') {
                  return u.indexOf('updateSymmetricKeyV2') !== -1;
                }
                // ANY_ROTATION: V2 or confirm (not bare legacy updateSymmetricKey alone)
                return u.indexOf('updateSymmetricKeyV2') !== -1
                    || u.indexOf('confirmSymmetricKeyUpdate') !== -1;
              };
              const sleep = (ms) => new Promise((r) => setTimeout(r, ms));

              const wrapFetch = async (send) => {
                state.intercepted += 1;
                if (state.intercepted === 1) {
                  if (mode === 'TERMINATE_FIRST_RESPONSE') {
                    try { await send(); } catch (e) { /* ignore */ }
                    state.terminated += 1;
                    throw new TypeError('SKR_FAULT: terminated rotation response');
                  }
                  if (mode === 'THROTTLE_FIRST_RESPONSE') {
                    const pending = send();
                    await sleep(holdMs);
                    state.throttled += 1;
                    return pending;
                  }
                }
                state.passed += 1;
                return send();
              };

              const origFetch = window.fetch ? window.fetch.bind(window) : null;
              if (origFetch) {
                window.fetch = function(input, init) {
                  const url = (typeof input === 'string')
                    ? input
                    : (input && input.url ? input.url : '');
                  if (!matchUrl(url)) {
                    return origFetch(input, init);
                  }
                  return wrapFetch(() => origFetch(input, init));
                };
              }

              const XHR = window.XMLHttpRequest;
              const origOpen = XHR.prototype.open;
              const origSend = XHR.prototype.send;

              const armXhrHooks = (xhr) => {
                if (xhr.__skrArmed) {
                  return;
                }
                xhr.__skrArmed = true;
                xhr.__skrDeferred = [];
                xhr.__skrOnReady = null;
                xhr.__skrOnLoad = null;
                xhr.__skrOnLoadEnd = null;
                xhr.__skrOnError = null;
                const nativeAdd = xhr.addEventListener.bind(xhr);
                xhr.__skrNativeAdd = nativeAdd;
                xhr.addEventListener = function(type, listener, opts) {
                  if (type === 'readystatechange' || type === 'load' || type === 'loadend'
                      || type === 'error' || type === 'abort' || type === 'timeout') {
                    xhr.__skrDeferred.push({type: type, listener: listener, opts: opts});
                    return;
                  }
                  return nativeAdd(type, listener, opts);
                };
                Object.defineProperty(xhr, 'onreadystatechange', {
                  configurable: true,
                  get: function() { return xhr.__skrOnReady; },
                  set: function(fn) { xhr.__skrOnReady = fn; }
                });
                Object.defineProperty(xhr, 'onload', {
                  configurable: true,
                  get: function() { return xhr.__skrOnLoad; },
                  set: function(fn) { xhr.__skrOnLoad = fn; }
                });
                Object.defineProperty(xhr, 'onloadend', {
                  configurable: true,
                  get: function() { return xhr.__skrOnLoadEnd; },
                  set: function(fn) { xhr.__skrOnLoadEnd = fn; }
                });
                Object.defineProperty(xhr, 'onerror', {
                  configurable: true,
                  get: function() { return xhr.__skrOnError; },
                  set: function(fn) { xhr.__skrOnError = fn; }
                });
              };

              const deliverXhr = (xhr, asTerminate) => {
                if (asTerminate) {
                  try {
                    Object.defineProperty(xhr, 'status', {configurable: true, get: () => 0});
                    Object.defineProperty(xhr, 'statusText', {configurable: true, get: () => ''});
                    Object.defineProperty(xhr, 'responseText', {configurable: true, get: () => ''});
                    Object.defineProperty(xhr, 'response', {configurable: true, get: () => ''});
                  } catch (e) {}
                }
                const fire = (fn) => {
                  if (typeof fn === 'function') {
                    try { fn.call(xhr); } catch (e) {}
                  }
                };
                fire(xhr.__skrOnReady);
                xhr.__skrDeferred.forEach((d) => {
                  if (d.type === 'readystatechange') {
                    try { d.listener.call(xhr); } catch (e) {}
                  }
                });
                if (!asTerminate) {
                  fire(xhr.__skrOnLoad);
                  xhr.__skrDeferred.forEach((d) => {
                    if (d.type === 'load') {
                      try { d.listener.call(xhr); } catch (e) {}
                    }
                  });
                } else {
                  fire(xhr.__skrOnError);
                  xhr.__skrDeferred.forEach((d) => {
                    if (d.type === 'error') {
                      try { d.listener.call(xhr); } catch (e) {}
                    }
                  });
                }
                fire(xhr.__skrOnLoadEnd);
                xhr.__skrDeferred.forEach((d) => {
                  if (d.type === 'loadend') {
                    try { d.listener.call(xhr); } catch (e) {}
                  }
                });
              };

              XHR.prototype.open = function(method, url) {
                this.__skrUrl = url;
                const result = origOpen.apply(this, arguments);
                if (matchUrl(url)) {
                  armXhrHooks(this);
                }
                return result;
              };

              XHR.prototype.send = function(body) {
                if (!matchUrl(this.__skrUrl)) {
                  return origSend.apply(this, arguments);
                }
                const xhr = this;
                const args = arguments;
                state.intercepted += 1;
                const faultFirst = state.intercepted === 1
                    && (mode === 'TERMINATE_FIRST_RESPONSE' || mode === 'THROTTLE_FIRST_RESPONSE');

                if (!faultFirst) {
                  state.passed += 1;
                  if (xhr.__skrArmed) {
                    xhr.addEventListener = xhr.__skrNativeAdd;
                    if (xhr.__skrOnReady) {
                      xhr.__skrNativeAdd('readystatechange', function() {
                        if (typeof xhr.__skrOnReady === 'function') {
                          xhr.__skrOnReady.call(xhr);
                        }
                      });
                    }
                    xhr.__skrDeferred.forEach((d) => xhr.__skrNativeAdd(d.type, d.listener, d.opts));
                    xhr.__skrDeferred = [];
                  }
                  return origSend.apply(xhr, args);
                }

                const asTerminate = mode === 'TERMINATE_FIRST_RESPONSE';
                xhr.__skrNativeAdd('readystatechange', function onNativeReady() {
                  if (xhr.readyState !== 4) {
                    return;
                  }
                  if (asTerminate) {
                    state.terminated += 1;
                    deliverXhr(xhr, true);
                    return;
                  }
                  sleep(holdMs).then(() => {
                    state.throttled += 1;
                    deliverXhr(xhr, false);
                  });
                });
                return origSend.apply(xhr, args);
              };

              window.__skrUpdateSkFault = {
                stats: () => Object.assign({}, state),
                restore: () => {
                  if (origFetch) window.fetch = origFetch;
                  XHR.prototype.open = origOpen;
                  XHR.prototype.send = origSend;
                }
              };
              return state;
            })(arguments[0], arguments[1], arguments[2]);
            """;
}
