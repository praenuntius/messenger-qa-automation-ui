package org.foxtrot.test.in_chat_context_menu.forward_message;

import org.foxtrot.api.dto.group.AddUsersRequest;
import org.foxtrot.api.dto.group.CreateGroupRequest;
import org.foxtrot.api.dto.group.DeleteGroupRequest;
import org.foxtrot.api.dto.group.EditAdminRequest;
import org.foxtrot.api.dto.group.response.CreateGroupResponse;
import org.foxtrot.api.dto.message.SendTextMessageRequest;
import org.foxtrot.api.steps.GroupApiSteps;
import org.foxtrot.api.steps.MessageApiSteps;
import org.foxtrot.enums.UsersEnum;
import org.foxtrot.test.BaseTest;
import org.foxtrot.utils.LoginHelper;
import org.foxtrot.utils.TestData;
import org.foxtrot.utils.UserPoolManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;

import static org.foxtrot.enums.GroupTypesEnum.GROUP;
@Disabled
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GoToMessageTest extends BaseTest {
    private LoginHelper loginHelper;
    private UsersEnum owner;
    private UsersEnum admin;
    private UsersEnum participant;
    private GroupApiSteps groupApiSteps;
    private MessageApiSteps messageApiStepsOwner;
    private MessageApiSteps messageApiStepsAdmin;
    private MessageApiSteps messageApiStepsParticipant;
    private static final Logger logger = LoggerFactory.getLogger(GoToMessageTest.class);
    private static String homespaceGroupName = TestData.generateHomespaceGroupName();
    private CreateGroupResponse createGroupResponse;

    @BeforeAll
    public void beforeAll() {
        loginHelper = new LoginHelper();
        UserPoolManager userPoolManager = new UserPoolManager();
        owner = userPoolManager.getFreeUser();
        admin = userPoolManager.getFreeUser();
        participant = userPoolManager.getFreeUser();
        groupApiSteps = new GroupApiSteps(owner);
        messageApiStepsOwner = new MessageApiSteps(owner);
        messageApiStepsAdmin = new MessageApiSteps(admin);
        messageApiStepsParticipant = new MessageApiSteps(participant);
        prepareTestData();
    }

    @AfterAll
    public void afterAll() {
        groupApiSteps.deleteGroup(new DeleteGroupRequest(createGroupResponse.getGroupId()));
        UserPoolManager.releaseCurrentUser();
    }

    //TODO: добавить методы для апи форварда сообщения
    //TODO: добавить UI тест перехода

    private void prepareTestData() {
        createGroupResponse = groupApiSteps.createGroup(new CreateGroupRequest(homespaceGroupName,
                GROUP.name(), false));

        logger.info("Group created with ID: {}", createGroupResponse.getGroupId());

        groupApiSteps.addUsers(new AddUsersRequest(createGroupResponse.getGroupId(),
                new ArrayList<>(Arrays.asList(admin.getId()))));

        groupApiSteps.editAdmin(new EditAdminRequest(createGroupResponse.getGroupId(), admin.getId(), true));

        groupApiSteps.addUsers(new AddUsersRequest(createGroupResponse.getGroupId(),
                new ArrayList<>(Arrays.asList(participant.getId()))));

        logger.info("Admin and Participant added to group with ID: {}", createGroupResponse.getGroupId());

        SendTextMessageRequest ownerMessageRequest = new SendTextMessageRequest(
                "Owner text via API",
                0,
                createGroupResponse.getGroupId()
        );
        messageApiStepsOwner.sendTextMessage(ownerMessageRequest, "-1");
        logger.info("Message 'Owner text via API' sent via API from OWNER to group with ID: {}", createGroupResponse.getGroupId());

        SendTextMessageRequest adminMessageRequest = new SendTextMessageRequest(
                "Admin text via API",
                0,
                createGroupResponse.getGroupId()
        );
        messageApiStepsAdmin.sendTextMessage(adminMessageRequest, "-1");
        logger.info("Message 'Admin text via API' sent via API from ADMIN to group with ID: {}", createGroupResponse.getGroupId());

        SendTextMessageRequest participantMessageRequest = new SendTextMessageRequest(
                "Participant text via API",
                0,
                createGroupResponse.getGroupId()
        );
        messageApiStepsParticipant.sendTextMessage(participantMessageRequest, "-1");
        logger.info("Message 'Participant text via API' sent via API from PARTICIPANT to group with ID: {}", createGroupResponse.getGroupId());
    }
}
