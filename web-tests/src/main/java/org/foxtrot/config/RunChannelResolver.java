package org.foxtrot.config;

import org.aeonbits.owner.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class RunChannelResolver {

    private static final Logger logger = LoggerFactory.getLogger(RunChannelResolver.class);
    private static final WebConfig config = ConfigFactory.create(WebConfig.class);
    private static volatile boolean logged;

    private RunChannelResolver() {
    }

    public static RunChannel getChannel() {
        RunChannel channel = RunChannel.fromString(resolveRawChannel());
        logOnce(channel);
        return channel;
    }

    public static boolean isWeb() {
        return getChannel() == RunChannel.WEB;
    }

    public static boolean isDesktop() {
        return getChannel() == RunChannel.DESKTOP;
    }

    /**
     * Приоритет:
     * 1. {@code selenide.remote} задан — это Selenoid/CI, канал web.
     *    Иначе Surefire подставляет pom {@code run.channel=desktop} и Linux падает на hdiutil.
     *    Jenkinsfile.encryption часто грузится с main и не передаёт {@code -Drun.channel=web}.
     * 2. {@code -Drun.channel}
     * 3. {@code web.properties}
     */
    private static String resolveRawChannel() {
        String remote = System.getProperty("selenide.remote");
        if (remote != null && !remote.isBlank()) {
            return RunChannel.WEB.getValue();
        }
        String system = System.getProperty("run.channel");
        if (system != null && !system.isBlank()) {
            return system.trim();
        }
        return config.runChannel();
    }

    private static void logOnce(RunChannel channel) {
        if (logged) {
            return;
        }
        logged = true;
        logger.info(
                "Активный run.channel={} (System.getProperty={}, selenide.remote={}, web.properties={})",
                channel.getValue(),
                System.getProperty("run.channel"),
                System.getProperty("selenide.remote"),
                config.runChannel());
    }
}
