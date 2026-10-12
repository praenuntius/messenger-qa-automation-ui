package org.foxtrot.pages.left_sidebar.vcs_tab.elements;

import lombok.Getter;
import org.openqa.selenium.By;

import static org.openqa.selenium.By.cssSelector;

@Getter
public class VideoMeetingsTab {

    private final By vcsTabCreateVideoMeetingButtonSelector = cssSelector("[data-cy='openVideoConferenceChatButton']");
}
