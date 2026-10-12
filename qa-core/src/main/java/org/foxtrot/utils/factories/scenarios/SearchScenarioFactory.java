package org.foxtrot.utils.factories.scenarios;

import org.foxtrot.utils.factories.contexts.GroupContext;
import org.foxtrot.utils.factories.contexts.UserContext;
import org.foxtrot.utils.factories.core.GroupFactory;
import org.foxtrot.utils.factories.core.MessageFactory;
import org.foxtrot.utils.factories.core.UserFactory;
import org.foxtrot.utils.factories.core.WorkspaceFactory;

public class SearchScenarioFactory {
    private final UserFactory userFactory;
    private final WorkspaceFactory workspaceFactory;
    private final GroupFactory groupFactory;
    private final MessageFactory messageFactory;

    public SearchScenarioFactory() {
        this.userFactory = new UserFactory();
        this.workspaceFactory = new WorkspaceFactory();
        this.groupFactory = new GroupFactory();
        this.messageFactory = new MessageFactory();
    }

    public UserContext prepareUserForTest() {
        return UserContext.builder().user(userFactory.takeUser()).build();
    }

    public GroupContext prepareByString(String type) {
        GroupContext context = switch (type) {
            // --- ПОЗИТИВНЫЕ КЕЙСЫ (Участник ВНУТРИ) ---

            // WORKSPACE
            case "wsCloseChannel" -> groupFactory.privateChannelInWsWithParticipant();
            case "wsCloseGroup"   -> groupFactory.privateGroupInWsWithParticipant();
            case "wsOpenChannel"  -> groupFactory.publicChannelInWsWithParticipant();
            case "wsOpenGroup"    -> groupFactory.publicGroupInWsWithParticipant();

            // HOMESPACE
            case "homeCloseChannel" -> groupFactory.privateChannelWithParticipant();
            case "homeCloseGroup"   -> groupFactory.privateGroupWithParticipant();
            case "homeOpenChannel"  -> groupFactory.publicChannelWithParticipant();

            // --- НЕГАТИВНЫЕ КЕЙСЫ (Участник СНАРУЖИ) ---

            // WORKSPACE (Пользователь есть в системе, но не в этих группах/каналах)
            case "neg_wsCloseChannel" -> groupFactory.privateChannelInWs();
            case "neg_wsCloseGroup"   -> groupFactory.privateGroupInWs();
            case "neg_wsOpenChannel"  -> groupFactory.publicChannelInWs();
            case "neg_wsOpenGroup"    -> groupFactory.publicGroupInWs();

            // HOMESPACE (Пользователь не приглашен в эти сущности в личном пространстве)
            case "neg_homeCloseChannel" -> groupFactory.privateChannel();
            case "neg_homeCloseGroup"   -> groupFactory.privateGroup();
            case "neg_homeOpenChannel"  -> groupFactory.publicChannel();

            default -> throw new IllegalArgumentException("Неизвестный тип сценария: " + type);
        };

        return enrichWithSearchData(context);
    }


    //Докручиваем сценарий отправкой сообщения
    private GroupContext enrichWithSearchData(GroupContext baseCtx) {
        return messageFactory.sendMessage(baseCtx);
    }

    public void cleanUp() {
        userFactory.releaseAll();
    }
}