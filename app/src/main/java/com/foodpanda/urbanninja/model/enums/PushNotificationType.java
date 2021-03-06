package com.foodpanda.urbanninja.model.enums;

import android.text.TextUtils;
import android.util.Log;

/**
 * Depend on type of push notification different action would be done and
 * different API call would be executed
 * <p/>
 * for more details you can check doc
 * https://github.com/foodpanda/logistics-core/blob/master/docs/push_notification.md
 */
public enum PushNotificationType {
    ROUTE_UPDATED,
    ROUTE_CANCELED,
    SCHEDULE_UPDATED;

    /**
     * Purpose: To prevent crashes if the next version of our app has new or different enum types.
     *
     * @param value sting key from push notification
     * @return priority type of it existing one null if not
     */
    public static PushNotificationType getValueOf(String value) {
        if (TextUtils.isEmpty(value)) {
            return null;
        }
        try {
            return valueOf(value);
        } catch (IllegalArgumentException e) {
            Log.e(PushNotificationType.class.getSimpleName(), e.getMessage());

            return null;
        }
    }
}
