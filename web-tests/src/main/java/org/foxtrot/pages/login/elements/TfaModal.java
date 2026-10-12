package org.foxtrot.pages.login.elements;

import lombok.Getter;
import org.openqa.selenium.By;

import static org.openqa.selenium.By.cssSelector;

@Getter
public class TfaModal {

    private final By forgotPasswordButton = cssSelector("[data-cy='welcomeLoginForgotPassword']");
    private final By tfaInput = cssSelector("[data-cy='inputLoginTFACode']");
}
