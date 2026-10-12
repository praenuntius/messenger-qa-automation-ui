package org.foxtrot.utils;

import com.codeborne.selenide.Selenide;
import io.qameta.allure.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public final class FeatureFlagsHelper {

    private static final Logger logger = LoggerFactory.getLogger(FeatureFlagsHelper.class);

    private static final String REMOTE_PROPERTIES_FIXTURE =
            "/fixtures/configStore-remoteProperties-preprod.json";

    private static final String MERGE_REMOTE_PROPERTIES_SCRIPT = """
            var fixture = JSON.parse(arguments[0]);
            var raw = localStorage.getItem('configStore');
            var store = raw ? JSON.parse(raw) : {};
            if (!store.remoteProperties) {
                store.remoteProperties = {};
            }
            Object.assign(store.remoteProperties, fixture);
            store.remotePropertiesLoadComplete = true;
            localStorage.setItem('configStore', JSON.stringify(store));
            return Object.keys(store.remoteProperties).length;
            """;

    private static volatile String cachedRemotePropertiesJson;

    private FeatureFlagsHelper() {
    }

    @Step("Подставить feature flags preprod в configStore")
    public static void applyMenuFeatureFlags() {
        String fixtureJson = loadRemotePropertiesFixture();
        Object keysCount = Selenide.executeJavaScript(MERGE_REMOTE_PROPERTIES_SCRIPT, fixtureJson);
        logger.info("remoteProperties merged from fixture, total keys={}", keysCount);
        Selenide.refresh();
    }

    private static String loadRemotePropertiesFixture() {
        if (cachedRemotePropertiesJson != null) {
            return cachedRemotePropertiesJson;
        }
        try (InputStream input = FeatureFlagsHelper.class.getResourceAsStream(REMOTE_PROPERTIES_FIXTURE)) {
            if (input == null) {
                throw new IllegalStateException("Fixture not found on classpath: " + REMOTE_PROPERTIES_FIXTURE);
            }
            cachedRemotePropertiesJson = new String(input.readAllBytes(), StandardCharsets.UTF_8);
            return cachedRemotePropertiesJson;
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read " + REMOTE_PROPERTIES_FIXTURE, e);
        }
    }
}
