package org.foxtrot.allure;

import io.qameta.allure.Allure;
import io.qameta.allure.AllureId;
import org.foxtrot.config.RunChannel;
import org.foxtrot.config.RunChannelResolver;
import org.junit.jupiter.api.extension.ExtensionContext;

import static io.qameta.allure.util.ResultsUtils.ALLURE_ID_LABEL_NAME;

public final class ChannelAllureHelper {

    private ChannelAllureHelper() {
    }

    public static boolean hasAllureIdForCurrentChannel(ExtensionContext context) {
        ChannelAllureIds channelIds = getChannelAllureIds(context);
        if (channelIds == null) {
            return true;
        }
        String allureId = resolveAllureId(channelIds);
        return allureId != null && !allureId.isBlank();
    }

    public static void addAllureLabels(ExtensionContext context) {
        RunChannel channel = RunChannelResolver.getChannel();
        Allure.label("channel", channel.getValue());

        ChannelAllureIds channelIds = getChannelAllureIds(context);
        if (channelIds != null) {
            String allureId = resolveAllureId(channelIds);
            if (allureId != null && !allureId.isBlank()) {
                Allure.label(ALLURE_ID_LABEL_NAME, allureId);
            }
            return;
        }

        if (channel == RunChannel.WEB) {
            context.getTestMethod()
                    .map(method -> method.getAnnotation(AllureId.class))
                    .ifPresent(legacy -> Allure.label(ALLURE_ID_LABEL_NAME, legacy.value()));
        }
    }

    private static ChannelAllureIds getChannelAllureIds(ExtensionContext context) {
        return context.getTestMethod()
                .map(method -> method.getAnnotation(ChannelAllureIds.class))
                .orElse(null);
    }

    private static String resolveAllureId(ChannelAllureIds annotation) {
        return RunChannelResolver.isDesktop() ? annotation.desktop() : annotation.web();
    }
}
