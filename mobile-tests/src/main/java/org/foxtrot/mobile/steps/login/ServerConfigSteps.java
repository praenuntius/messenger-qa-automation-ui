package org.foxtrot.mobile.steps.login;

import io.qameta.allure.Step;
import org.foxtrot.mobile.screens.login.ServerConfigScreen;
import org.foxtrot.mobile.steps.modals.ModalSteps;
import org.foxtrot.mobile.utils.LoaderWaiter;
import org.foxtrot.utils.Waits;
import org.junit.jupiter.api.Assertions;

public class ServerConfigSteps {

    ServerConfigScreen serverConfigScreen = new ServerConfigScreen();
    ModalSteps modalSteps = new ModalSteps();

    @Step("Заполняю страницу смены конфигурации сервера")
    public ServerConfigSteps changeServerConfig(String stageName, String domain) {
        Assertions.assertTrue(serverConfigScreen.isScreenTitleDisplayed(15L), "Страница смены настроек конфигурации " +
                "сервера не отображается");
        serverConfigScreen.fillDomain(domain);
        serverConfigScreen.clickEnvironmentInput();
        serverConfigScreen.clickStageOption(stageName);
        serverConfigScreen.clickSaveBtn();
        modalSteps.clickActionButton();
        LoaderWaiter.waitForNoLoader();
        Waits.sleepSeconds(5); // тк появляется страница логина и потом пропадает
        return this;
    }
}
