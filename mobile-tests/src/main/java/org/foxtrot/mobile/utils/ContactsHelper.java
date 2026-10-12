package org.foxtrot.mobile.utils;

import org.foxtrot.enums.UsersEnum;
import org.foxtrot.mobile.config.MobilePlatformResolver;
import org.foxtrot.mobile.core.application.ApplicationUtils;
import org.foxtrot.mobile.core.application.PlatformName;
import org.foxtrot.mobile.core.emulator.DeviceStateRegistry;
import org.foxtrot.mobile.steps.native_screens.ContactCreateSteps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.foxtrot.mobile.utils.CommandLineManager.startCMDProcessIgnoreError;
import static org.foxtrot.mobile.utils.CommandLineManager.startCMDProcessWithResponse;

public class ContactsHelper {

    private static final Logger logger = LoggerFactory.getLogger(ContactsHelper.class);

    public static void addContact(UsersEnum usersEnum) {
        String fullName = "%s %s".formatted(usersEnum.getFirstName(), usersEnum.getLastName());
        String phone = usersEnum.getFormattedPhone().replaceAll("[\\s+-]", "");
        addContact(fullName, phone);
    }

    public static void addContact(String name, String phone) {
        PlatformName platform = PlatformName.valueOf(MobilePlatformResolver.getPlatform());
        phone = phone.replaceAll("[\\s+-]", "");
        switch (platform) {
            case ANDROID -> addAndroidContact(name, phone);
            case IOS -> addIosContact(name, phone);
        }
    }

    private static void addAndroidContact(String name, String phone) {
        logger.info("Добавляю Android контакт. Name: {}, Phone: {}", name, phone);
        if (DeviceStateRegistry.androidContactIds().isEmpty()) {
            wipeAndroidContacts();
        }
        String rawContactId;
        try {
            startCMDProcessWithResponse("""
                    adb shell content insert \
                    --uri content://com.android.contacts/raw_contacts \
                    --bind account_type:s: \
                    --bind account_name:s:
                    """);
            rawContactId = startCMDProcessWithResponse("""
                    adb shell content query \
                    --uri content://com.android.contacts/raw_contacts \
                    --projection _id \
                    | tail -n 1 \
                    | sed 's/.*_id=//'
                    """).trim();
            if (rawContactId.isBlank()) {
                throw new RuntimeException("Не удалось получить raw_contact_id: " + rawContactId);
            }
            startCMDProcessWithResponse(String.format("""
                    adb shell "content insert \
                    --uri content://com.android.contacts/data \
                    --bind raw_contact_id:i:%s \
                    --bind mimetype:s:vnd.android.cursor.item/name \
                    --bind 'data1:s:%s'"
                    """, rawContactId, name));
            startCMDProcessWithResponse(String.format("""
                    adb shell content insert \
                    --uri content://com.android.contacts/data \
                    --bind raw_contact_id:i:%s \
                    --bind mimetype:s:vnd.android.cursor.item/phone_v2 \
                    --bind data1:s:%s \
                    --bind data2:i:2
                    """, rawContactId, phone));
        } catch (Exception e) {
            throw new RuntimeException("Не удалось добавить Android контакт", e);
        }
        DeviceStateRegistry.addAndroidContact(rawContactId);
        logger.info("Android контакт добавлен. rawContactId: {}, Name: {}, Phone: {}", rawContactId, name, phone);
    }

    private static void wipeAndroidContacts() {
        logger.info("Чищу Android контакты на эмуляторе перед добавлением тестовых");
        startCMDProcessIgnoreError("adb shell content delete --uri content://com.android.contacts/data");
        startCMDProcessIgnoreError("adb shell content delete --uri content://com.android.contacts/raw_contacts");
        // Google/device-аккаунты (на CI остаётся «Алёна Михайлова») content delete не снимает.
        startCMDProcessIgnoreError("adb shell pm clear com.android.providers.contacts");
        try {
            Thread.sleep(800L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static void addIosContact(String name, String phone) {
        logger.info("Добавляю iOS контакт через vCard. Name: {}, Phone: {}", name, phone);
        try {
            Path vCardPath = Files.createTempFile("autotest-contact-", ".vcf");
            String firstName = name;
            String lastName = "";
            int split = name.indexOf(' ');
            if (split > 0) {
                firstName = name.substring(0, split);
                lastName = name.substring(split + 1).trim();
            }
            String vCard = """
                    BEGIN:VCARD
                    VERSION:3.0
                    N:%s;%s;;;
                    FN:%s
                    TEL;TYPE=CELL:%s
                    END:VCARD
                    """.formatted(lastName, firstName, name, phone);
            Files.writeString(vCardPath, vCard);
            startCMDProcessWithResponse("xcrun simctl openurl booted \"" + vCardPath.toUri() + "\"");
            logger.info("vCard открыт на iOS Simulator: {}", vCardPath);
            Thread.sleep(1_200L);
            new ContactCreateSteps().clickSaveBtn();
            DeviceStateRegistry.markIosDirty();
            ApplicationUtils.launchApplication();
        } catch (Exception e) {
            throw new RuntimeException("Не удалось добавить Ios контакт", e);
        }
    }
}
