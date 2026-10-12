package org.foxtrot.mobile.utils;

import org.foxtrot.mobile.steps.*;
import org.foxtrot.mobile.steps.homespace.ChatsSteps;
import org.foxtrot.mobile.steps.homespace.CreateNewMessageFromHomespaceSteps;
import org.foxtrot.mobile.steps.login.Login2FASteps;
import org.foxtrot.mobile.steps.login.LoginVerificationCodeSteps;
import org.foxtrot.mobile.steps.login.LoginWelcomeSteps;
import org.foxtrot.mobile.steps.bottomSheet.JoinChannelBottomSheetSteps;
import org.foxtrot.mobile.steps.bottomSheet.JoinGroupBottomSheetSteps;
import org.foxtrot.mobile.steps.modals.ModalSteps;
import org.foxtrot.mobile.steps.modals.OnboardingSteps;
import org.foxtrot.mobile.steps.workspace.CreateNewMessageFromWorkspaceSteps;
import org.foxtrot.mobile.steps.workspace.WorkspaceSteps;

public final class MobilePages {

    private MobileLoginHelper mobileLogin;
    private LoginWelcomeSteps loginWelcome;
    private LoginVerificationCodeSteps loginVerificationCode;
    private Login2FASteps login2FA;
    private CreateNewMessageFromWorkspaceSteps createNewMessageFromWorkspace;
    private CreateNewMessageFromHomespaceSteps createNewMessageFromHomespace;
    private CreateNewChannelSteps createNewChannel;
    private CreateNewGroupSteps createNewGroup;
    private WorkspaceSteps workspace;
    private ChatSteps chat;
    private ChatsSteps chats;
    private ChannelPreferencesSteps channelPreferences;
    private GroupPreferencesSteps groupPreferences;
    private GroupInviteByLinkSteps groupInviteByLink;
    private AddSubscribersSteps addSubscribers;
    private OnboardingSteps onboarding;
    private BottomMenuSteps bottomMenu;
    private AccountSteps account;
    private ModalSteps modal;
    private JoinGroupBottomSheetSteps joinGroupBottomSheet;
    private JoinChannelBottomSheetSteps joinChannelBottomSheet;
    private GroupMembersSteps groupMembers;
    private ChannelMembersSteps channelMembers;
    private ContactInformationSteps contactInformation;

    public MobileLoginHelper mobileLogin() {
        if (mobileLogin == null) {
            mobileLogin = new MobileLoginHelper();
        }
        return mobileLogin;
    }

    public LoginWelcomeSteps loginWelcome() {
        if (loginWelcome == null) {
            loginWelcome = new LoginWelcomeSteps();
        }
        return loginWelcome;
    }

    public LoginVerificationCodeSteps loginVerificationCode() {
        if (loginVerificationCode == null) {
            loginVerificationCode = new LoginVerificationCodeSteps();
        }
        return loginVerificationCode;
    }

    public Login2FASteps login2FA() {
        if (login2FA == null) {
            login2FA = new Login2FASteps();
        }
        return login2FA;
    }

    public CreateNewMessageFromWorkspaceSteps createNewMessageFromWorkspace() {
        if (createNewMessageFromWorkspace == null) {
            createNewMessageFromWorkspace =
                    new CreateNewMessageFromWorkspaceSteps();
        }
        return createNewMessageFromWorkspace;
    }

    public CreateNewMessageFromHomespaceSteps createNewMessageFromHomespace() {
        if (createNewMessageFromHomespace == null) {
            createNewMessageFromHomespace =
                    new CreateNewMessageFromHomespaceSteps();
        }
        return createNewMessageFromHomespace;
    }

    public CreateNewChannelSteps createNewChannel() {
        if (createNewChannel == null) {
            createNewChannel = new CreateNewChannelSteps();
        }
        return createNewChannel;
    }

    public CreateNewGroupSteps createNewGroup() {
        if (createNewGroup == null) {
            createNewGroup = new CreateNewGroupSteps();
        }
        return createNewGroup;
    }

    public WorkspaceSteps workspace() {
        if (workspace == null) {
            workspace = new WorkspaceSteps();
        }
        return workspace;
    }

    public ChatSteps chat() {
        if (chat == null) {
            chat = new ChatSteps();
        }
        return chat;
    }

    public ChatsSteps chats() {
        if (chats == null) {
            chats = new ChatsSteps();
        }
        return chats;
    }

    public ChannelPreferencesSteps channelPreferences() {
        if (channelPreferences == null) {
            channelPreferences = new ChannelPreferencesSteps();
        }
        return channelPreferences;
    }

    public GroupPreferencesSteps groupPreferences() {
        if (groupPreferences == null) {
            groupPreferences = new GroupPreferencesSteps();
        }
        return groupPreferences;
    }

    public GroupInviteByLinkSteps groupInviteByLink() {
        if (groupInviteByLink == null) {
            groupInviteByLink = new GroupInviteByLinkSteps();
        }
        return groupInviteByLink;
    }

    public AddSubscribersSteps addSubscribers() {
        if (addSubscribers == null) {
            addSubscribers = new AddSubscribersSteps();
        }
        return addSubscribers;
    }

    public OnboardingSteps onboarding() {
        if (onboarding == null) {
            onboarding = new OnboardingSteps();
        }
        return onboarding;
    }

    public BottomMenuSteps bottomMenu() {
        if (bottomMenu == null) {
            bottomMenu = new BottomMenuSteps();
        }
        return bottomMenu;
    }

    public AccountSteps account() {
        if (account == null) {
            account = new AccountSteps();
        }
        return account;
    }

    public ModalSteps modal() {
        if (modal == null) {
            modal = new ModalSteps();
        }
        return modal;
    }

    public JoinGroupBottomSheetSteps joinGroupBottomSheet() {
        if (joinGroupBottomSheet == null) {
            joinGroupBottomSheet = new JoinGroupBottomSheetSteps();
        }
        return joinGroupBottomSheet;
    }

    public JoinChannelBottomSheetSteps joinChannelBottomSheet() {
        if (joinChannelBottomSheet == null) {
            joinChannelBottomSheet = new JoinChannelBottomSheetSteps();
        }
        return joinChannelBottomSheet;
    }

    public GroupMembersSteps groupMembers() {
        if (groupMembers == null) {
            groupMembers = new GroupMembersSteps();
        }
        return groupMembers;
    }

    public ChannelMembersSteps channelMembers() {
        if (channelMembers == null) {
            channelMembers = new ChannelMembersSteps();
        }
        return channelMembers;
    }

    public ContactInformationSteps contactInformation() {
        if (contactInformation == null) {
            contactInformation = new ContactInformationSteps();
        }
        return contactInformation;
    }
}
