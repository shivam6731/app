package com.foodpanda.urbanninja.model.enums;

import android.text.TextUtils;
import android.util.Log;

/**
 * Depend on priority of push notification
 * we decide should we show notification in a status bar or
 * it's just background update of the app
 * <p/>
 * for more details you can check doc
 * https://github.com/foodpanda/logistics-core/blob/master/docs/push_notification.md
 */
public enum PushNotificationPriority {
    FOREGROUND_UPDATE,
    BACKGROUND_UPDATE;

    /**
     * Purpose: To prevent crashes if the next version of our app has new or different enum types.
     *
     * @param value sting key from push notification
     * @return priority type of it existing one null if not
     */
    public static PushNotificationPriority getValueOf(String value) {
        if (TextUtils.isEmpty(value)) {
            return PushNotificationPriority.FOREGROUND_UPDATE;
        }
        try {
            return valueOf(value);
        } catch (IllegalArgumentException e) {
            Log.e(PushNotificationPriority.class.getSimpleName(), e.getMessage());

            return PushNotificationPriority.FOREGROUND_UPDATE;
        }
    }
}
