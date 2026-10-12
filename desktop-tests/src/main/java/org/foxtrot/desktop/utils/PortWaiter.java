package org.foxtrot.desktop.utils;

import org.foxtrot.utils.Waits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Socket;

/**
 * Ожидает открытия TCP-порта (CDP) после запуска Electron.
 */
public class PortWaiter {

    private static final Logger logger = LoggerFactory.getLogger(PortWaiter.class);

    private PortWaiter() {
    }

    public static void waitForPort(String host, int port, int timeoutSeconds) {
        logger.info("Жду открытия порта {}:{} (таймаут {}с)", host, port, timeoutSeconds);
        long deadline = System.currentTimeMillis() + timeoutSeconds * 1000L;
        while (System.currentTimeMillis() < deadline) {
            if (isPortOpen(host, port)) {
                logger.info("Порт {}:{} открыт", host, port);
                return;
            }
            Waits.sleepMillis(500);
        }
        throw new IllegalStateException(
                "Electron не открыл CDP-порт " + host + ":" + port + " за " + timeoutSeconds + "с");
    }

    private static boolean isPortOpen(String host, int port) {
        try (Socket ignored = new Socket(host, port)) {
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
