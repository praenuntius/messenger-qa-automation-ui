package org.foxtrot.pages.left_sidebar.vcs_tab.elements;

import lombok.Getter;
import org.openqa.selenium.By;

import static org.openqa.selenium.By.cssSelector;

@Getter
public class NewVideoMeetingModal {

    private final By newVideoMeetingModalCloseButtonSelector = cssSelector("[data-cy='createMeetLinkCloseButton']");
    private final By newVideoMeetingModalCopyLinkButtonSelector = cssSelector("[data-cy='createMeetLinkCopyButton']");
    private final By newVideoMeetingModalLinkInputFieldSelector = cssSelector("[data-cy='copyVideoConferenceLinkInput']");
    private final By newVideoMeetingModalJoinButtonSelector = cssSelector("[data-cy='createItemNextButton']");
}
