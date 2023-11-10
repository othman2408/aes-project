package assets.views.sharedComponents;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;

public class Notify {

    /**
     * Shows a notification message.
     *
     * @param msg      The message to show.
     * @param duration The duration of the notification.
     * @param type     The type of the notification.
     */
    public static void notify(String msg, int duration, String type) {
        Notification notification = new Notification(msg, duration, Notification.Position.TOP_CENTER);

        // Check for valid theme variants
        if ("success".equalsIgnoreCase(type)) {
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } else if ("error".equalsIgnoreCase(type)) {
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        } else if ("warning".equalsIgnoreCase(type)) {
            notification.addThemeVariants(NotificationVariant.LUMO_WARNING);
        } else if ("primary".equalsIgnoreCase(type)) {
            notification.addThemeVariants(NotificationVariant.LUMO_PRIMARY);
        } else {
            notification.addThemeVariants(NotificationVariant.LUMO_CONTRAST);
        }

        notification.open();
    }




}
