package org.foxtrot.mobile.allure;

import io.qameta.allure.Allure;
import org.foxtrot.mobile.config.MobilePlatformResolver;
import org.foxtrot.mobile.core.application.PlatformName;
import org.junit.jupiter.api.extension.ExtensionContext;

import static io.qameta.allure.util.ResultsUtils.ALLURE_ID_LABEL_NAME;

public final class MobileAllureHelper {

    private MobileAllureHelper() {
    }

    public static boolean hasAllureIdForCurrentPlatform(ExtensionContext context) {
        AllureIds annotation = getAllureIds(context);
        if (annotation == null) {
            return true;
        }
        PlatformName platform = PlatformName.valueOf(MobilePlatformResolver.getPlatform());
        String allureId = getAllureId(annotation, platform);
        return allureId != null && !allureId.isBlank();
    }

    public static void addAllureLabels(ExtensionContext context) {
        AllureIds annotation = getAllureIds(context);
        if (annotation == null) {
            return;
        }
        PlatformName platform = PlatformName.valueOf(MobilePlatformResolver.getPlatform());
        String allureId = getAllureId(annotation, platform);
        Allure.label(ALLURE_ID_LABEL_NAME, allureId);
        Allure.parameter("platform", platform);
    }

    private static AllureIds getAllureIds(ExtensionContext context) {
        return context.getRequiredTestMethod().getAnnotation(AllureIds.class);
    }

    private static String getAllureId(AllureIds annotation, PlatformName platform) {
        return switch (platform) {
            case ANDROID -> annotation.android();
            case IOS -> annotation.ios();
        };
    }
}