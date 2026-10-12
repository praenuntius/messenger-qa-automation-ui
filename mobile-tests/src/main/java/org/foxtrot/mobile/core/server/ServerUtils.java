package org.foxtrot.mobile.core.server;

import org.aeonbits.owner.ConfigFactory;
import org.foxtrot.mobile.config.MobileConfig;
import org.foxtrot.utils.Waits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;

import static org.foxtrot.mobile.utils.CommandLineManager.startCMDProcessIgnoreError;

public final class ServerUtils {

    private static final Logger logger = LoggerFactory.getLogger(ServerUtils.class);
    private static final MobileConfig config = ConfigFactory.create(MobileConfig.class);

    public static int getAvailablePort() {
        int initialPort = config.appiumServerPort();
        int[] ports = {initialPort, initialPort + 1, initialPort + 2, initialPort + 3, initialPort + 4};
        for (int port : ports) {
            try {
                createServerSocket(port);
                return port;
            } catch (BindException e) {
                logger.error("Порт {} занят, пытаюсь очистить", port);
                try {
                    releasePort(port);
                    createServerSocket(port);
                    return port;
                } catch (IOException ex) {
                    logger.error("Ошибка при попытке очистить порт  {}. Использую следующий порт", port);
                }
            } catch (IOException e) {
                logger.error("Ошибка при использовании порта {}", port);
            }
        }
        throw new RuntimeException("Не удалось использовать ни одного порта из предложенных");
    }

    private static void releasePort(int portNumber) {
        logger.info("Очищаю порт для Appium");
        startCMDProcessIgnoreError(String.format("kill -9 $(lsof -t -i:%d)", portNumber));
        Waits.sleepSeconds(2);
    }

    private ServerUtils() {
    }

    private static void createServerSocket(int portNumber) throws IOException {
        ServerSocket serverSocket = new ServerSocket(portNumber);
        serverSocket.getLocalPort();
        serverSocket.close();
    }
}
