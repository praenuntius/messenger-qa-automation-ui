package org.foxtrot.pages.login.elements;

import lombok.Getter;
import org.openqa.selenium.By;

import static org.openqa.selenium.By.cssSelector;

@Getter
public class SmsCodeModal {

    private final By resendCodeButton = cssSelector("[data-cy='welcomeLoginResendCode']");
    private final By smsCodeFieldButton = cssSelector("[data-cy='signInSmsCodeInput']");
    private final By nextButton = cssSelector("[data-cy='signInSmsCodeDoneButton']");
}
