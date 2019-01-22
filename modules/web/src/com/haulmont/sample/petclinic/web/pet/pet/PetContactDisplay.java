package com.haulmont.sample.petclinic.web.pet.pet;

import com.haulmont.sample.petclinic.contact.Contact;
import com.haulmont.cuba.gui.Notifications;
import com.haulmont.cuba.gui.Notifications.NotificationType;
import com.haulmont.cuba.gui.screen.FrameOwner;
import com.haulmont.cuba.gui.screen.UiControllerUtils;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component("petclinic_PetContactDisplay")
public class PetContactDisplay {



    public void displayContact(Optional<Contact> contactInformation, FrameOwner origin) {

        if (contactInformation.isPresent()) {
            showNotification(contactInformation.get().toString(), NotificationType.TRAY, origin);
        }
        else {
            showNotification("No contact information found", NotificationType.ERROR, origin);
        }

    }

    private void showNotification(String notificationMsg, NotificationType notificationType, FrameOwner origin) {
        Notifications notifications = UiControllerUtils.getScreenContext(origin)
            .getNotifications();

        notifications
            .create()
            .withCaption(notificationMsg)
            .withType(notificationType)
            .show();
    }
}
