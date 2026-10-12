package org.foxtrot.pages.chat.forward;

import org.foxtrot.pages.chat.forward.elements.AvailableWorkspacesDropDownList;
import org.openqa.selenium.By;

public class AvailableWorkspacesDropDownListSteps {

    private final AvailableWorkspacesDropDownList elements = new AvailableWorkspacesDropDownList();

    public By getAvailableWorkspace(String workspaceName) {
        return elements.getAvailableWorkspace(workspaceName);
    }
}
