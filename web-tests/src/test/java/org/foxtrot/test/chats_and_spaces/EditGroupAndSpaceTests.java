package org.foxtrot.test.chats_and_spaces;

import org.foxtrot.allure.ChannelAllureIds;
import org.foxtrot.test.BaseTest;
import org.foxtrot.utils.LoginHelper;
import org.foxtrot.utils.factories.core.GroupFactory;
import org.foxtrot.utils.factories.core.WorkspaceFactory;
import org.junit.jupiter.api.*;

import static com.codeborne.selenide.Selenide.sleep;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EditGroupAndSpaceTests extends BaseTest {

    private final String changedName = "Редактированное имя";
    private final String changedDescription = "Редактированное описание";
    private final String changedImages = "images/tinyJPG.jpg";

    private LoginHelper loginHelper = new LoginHelper();
    private WorkspaceFactory wsFactory = new WorkspaceFactory();
    private GroupFactory groupFactory = new GroupFactory();

    @AfterAll
    public void afterAll() {
    }

    @ChannelAllureIds(web = "52498", desktop = "96227")
    @Test
    @Order(1)
    @DisplayName("Редактирование группы в домашнем пространстве")
    public void editHomespaceGroup() {
        var ct = groupFactory.privateGroup();
        loginHelper.performUILogin(ct.getOwner());
        pages.homespaceSteps.clickChatByName(ct.getGroupName());
        pages.topbarSteps.clickGroupHeader();
        pages.groupMenuSteps.clickOpenSetingsButton();
        pages.groupAndChannelEditModalSteps
                .uploadPhoto(changedImages)
                .editName(changedName)
                .editDescription(changedDescription);
        pages.groupAndChannelEditModalSteps.saveButton();
        pages.groupMenuSteps.verifyEditGroup(changedName, changedDescription);
    }

    @ChannelAllureIds(web = "52556", desktop = "96228")
    @Test
    @Order(2)
    @DisplayName("Редактирование канала в домашнем пространстве")
    public void editHomespaceChannel() {
        var ct = groupFactory.privateChannel();
        loginHelper.performUILogin(ct.getOwner());
        pages.homespaceSteps.clickChatByName(ct.getGroupName());
        pages.topbarSteps.clickChannelHeader();
        pages.channelMenuSteps.openChannelSettings();
        pages.groupAndChannelEditModalSteps
                .uploadPhoto(changedImages)
                .editName(changedName)
                .editDescription(changedDescription)
                .clickPublicChannelCheckbox();
        pages.groupAndChannelEditModalSteps.saveButton();
        pages.channelMenuSteps.verifyEditChannel(changedName, changedDescription,
                "Открытый канал");
    }

    @ChannelAllureIds(web = "52326", desktop = "96229")
    @Test
    @Order(3)
    @DisplayName("Редактирование Рабочего пространства")
    public void editWorkspace() {
        var ct = wsFactory.createWorkspace();
        loginHelper.performUILogin(ct.getOwner());
        pages.leftSidebarSteps.clickSpaceByName(ct.getWsName());
        pages.spaceSteps.openSpaceMenu();
        pages.spaceMenuSteps.editButton();
        pages.spaceEditModalSteps
                .uploadPhoto(changedImages)
                .editName(changedName)
                .editDescription(changedDescription);
        pages.spaceEditModalSteps.saveButton();
        pages.spaceMenuSteps.verifyEditSpace(changedName, changedDescription);
    }

    //TODO:Добавить в кейсы ниже - опциональность для реализации автономности и атомарности (т.к зависимость на обновленное пространство в предыдущем шаге)

    @ChannelAllureIds(web = "52501", desktop = "96230")
    @Test
    @Order(4)
    @DisplayName("Редактирование группы в рабочем пространстве")
    public void editWorkspaceGroup() {
        var ct = groupFactory.privateGroupInWs();
        loginHelper.performUILogin(ct.getOwner());
        pages.leftSidebarSteps.clickSpaceByName(ct.getWorkspaceName());
        pages.chatListSteps.clickChatByName(ct.getGroupName());
        pages.topbarSteps.clickGroupHeader();
        pages.groupMenuSteps.clickOpenSetingsButton();
        pages.groupAndChannelEditModalSteps
                .uploadPhoto(changedImages)
                .editName(changedName)
                .editDescription(changedDescription)
                .clickPublicGroupCheckbox();
        sleep(600);
        pages.groupAndChannelEditModalSteps.saveButton();
        pages.groupMenuSteps.verifyEditGroup(changedName, changedDescription, "Открытая группа");
    }

    @ChannelAllureIds(web = "52559", desktop = "96231")
    @Test
    @Order(5)
    @DisplayName("Редактирование канала в рабочем пространстве")
    public void editWorkspaceChannel() {
        var ct = groupFactory.privateChannelInWs();
        loginHelper.performUILogin(ct.getOwner());
        pages.leftSidebarSteps.clickSpaceByName(ct.getWorkspaceName());
        pages.chatListSteps.clickChatByName(ct.getGroupName());
        pages.topbarSteps.clickChannelHeader();
        pages.channelMenuSteps.openChannelSettings();
        pages.groupAndChannelEditModalSteps
                .uploadPhoto(changedImages)
                .editName(changedName)
                .editDescription(changedDescription)
                .clickPrivateChannelCheckbox();
        pages.groupAndChannelEditModalSteps.saveButton();
        pages.channelMenuSteps.verifyEditChannel(changedName, changedDescription, "Закрытый канал");
    }
}
