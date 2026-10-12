package org.foxtrot.desktop.playwright;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.foxtrot.desktop.session.DesktopSession;
import org.foxtrot.desktop.session.DesktopSessionManager;
import org.foxtrot.utils.Waits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Управляет жизненным циклом Playwright для одного потока.
 * Подключается к запущенному Electron по CDP.
 * <p>
 * Playwright создаётся один раз на поток (ThreadLocal).
 * Page переоткрывается/обновляется в каждом beforeEach.
 */
public class PlaywrightManager {

    private static final Logger logger = LoggerFactory.getLogger(PlaywrightManager.class);
    private static final int CDP_CONNECT_RETRIES = 5;

    private static final ThreadLocal<Playwright> PLAYWRIGHT = new ThreadLocal<>();
    private static final ThreadLocal<Browser> BROWSER = new ThreadLocal<>();
    private static final ThreadLocal<Page> PAGE = new ThreadLocal<>();

    private PlaywrightManager() {
    }

    /**
     * Подключается к Electron CDP текущего потока.
     * Вызывается в beforeEach после успешного запуска Electron.
     */
    public static Page connect() {
        DesktopSession session = DesktopSessionManager.currentSession();
        logger.info("Подключаюсь к CDP: {}", session.getCdpUrl());

        Playwright playwright = Playwright.create();
        PLAYWRIGHT.set(playwright);

        // Retry на случай кратковременной недоступности CDP
        Browser browser = null;
        Exception lastException = null;
        for (int attempt = 1; attempt <= CDP_CONNECT_RETRIES; attempt++) {
            try {
                browser = playwright.chromium().connectOverCDP(session.getCdpUrl());
                break;
            } catch (Exception e) {
                lastException = e;
                logger.warn("CDP connect попытка {}/{} не удалась: {}", attempt, CDP_CONNECT_RETRIES, e.getMessage());
                Waits.sleepMillis(1000);
            }
        }

        if (browser == null) {
            throw new RuntimeException(
                    "Не удалось подключиться к Electron CDP за " + CDP_CONNECT_RETRIES + " попыток: "
                    + session.getCdpUrl(), lastException);
        }

        BROWSER.set(browser);

        // Используем уже открытый контекст Electron, не создаём новый
        BrowserContext context = browser.contexts().isEmpty()
                ? browser.newContext()
                : browser.contexts().get(0);

        Page page = context.pages().isEmpty() ? context.newPage() : context.pages().get(0);
        PAGE.set(page);
        logger.info("Playwright подключён: {} страниц в контексте", context.pages().size());
        return page;
    }

    /** Возвращает Page текущего потока. */
    public static Page currentPage() {
        Page page = PAGE.get();
        if (page == null) {
            throw new IllegalStateException(
                    "Playwright Page не инициализирован. Убедитесь, что connect() вызван.");
        }
        return page;
    }

    /** Закрывает Playwright-сессию текущего потока. Вызывается в afterEach. */
    public static void disconnect() {
        try {
            Browser browser = BROWSER.get();
            if (browser != null) {
                browser.close();
            }
        } catch (Exception e) {
            logger.warn("Ошибка при закрытии Browser: {}", e.getMessage());
        } finally {
            try {
                Playwright playwright = PLAYWRIGHT.get();
                if (playwright != null) {
                    playwright.close();
                }
            } catch (Exception e) {
                logger.warn("Ошибка при закрытии Playwright: {}", e.getMessage());
            } finally {
                PLAYWRIGHT.remove();
                BROWSER.remove();
                PAGE.remove();
            }
        }
    }
}
