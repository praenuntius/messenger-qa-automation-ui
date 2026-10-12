package org.foxtrot.allure;

import org.foxtrot.config.RunChannelResolver;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * Подставляет Allure ID в зависимости от {@code run.channel} (web / desktop).
 */
public class ChannelAllureExtension implements ExecutionCondition, BeforeEachCallback {

    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
        if (context.getTestMethod().isEmpty()) {
            return ConditionEvaluationResult.enabled("Class-level context");
        }
        if (!ChannelAllureHelper.hasAllureIdForCurrentChannel(context)) {
            return ConditionEvaluationResult.disabled(
                    "Тест пропущен: AllureId не задан для канала " + RunChannelResolver.getChannel().getValue());
        }
        return ConditionEvaluationResult.enabled(
                "Тест доступен для канала " + RunChannelResolver.getChannel().getValue());
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        ChannelAllureHelper.addAllureLabels(context);
    }
}
