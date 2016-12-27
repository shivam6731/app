package com.foodpanda.urbanninja.api.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.foodpanda.urbanninja.App;
import com.foodpanda.urbanninja.Constants;
import com.foodpanda.urbanninja.R;
import com.foodpanda.urbanninja.manager.StorageManager;
import com.foodpanda.urbanninja.model.enums.PushNotificationPriority;
import com.foodpanda.urbanninja.model.enums.PushNotificationType;
import com.foodpanda.urbanninja.ui.activity.LoginActivity;
import com.foodpanda.urbanninja.ui.activity.MainActivity;

import javax.inject.Inject;

public class GcmListenerService extends com.google.android.gms.gcm.GcmListenerService {
    //Set interval update for LED light when notification received
    //See documentation link
    //http://developer.android.com/intl/ru/reference/android/app/Notification.Builder.html#setLights(int, int, int)
    private static final int LED_UPDATE_INTERVAL_IN_MILLISECONDS = 3000;
    //Set the vibration pattern to use. See vibrate(long[], int) for a discussion of the pattern parameter.
    //See documentation link
    //http://developer.android.com/reference/android/app/Notification.Builder.html#setVibrate(long[])
    private static final long[] VIBRATION_PATTERN = {1000, 200, 800, 200, 600, 200, 400, 200, 200, 1000, 100, 200, 50, 200, 50, 200, 50,
        200, 500, 1000, 200, 800, 200, 600, 200, 400, 200, 200, 1000};
    @Inject
    StorageManager storageManager;

    private static final String TAG = GcmListenerService.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        App.get(this).getMainComponent().inject(this);
    }

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    @Override
    public void onMessageReceived(String from, Bundle data) {
        Log.d(TAG, "From: " + from);

        /**
         * Production applications would usually process the message here.
         * Eg: - Syncing with server.
         *     - Store message in local database.
         *     - Update UI.
         */

        /**
         * In some cases it may be useful to show a notification indicating to the user
         * that a message was received.
         * however we have cases when notifications are restricted
         */
        PushNotificationType pushNotificationType = PushNotificationType.getValueOf(data.getString(Constants.PUSH_NOTIFICATION_TYPE));
        PushNotificationPriority pushNotificationPriority = PushNotificationPriority.getValueOf(data.getString(Constants.PUSH_NOTIFICATION_PRIORITY));

        if (shouldNotifyUser(pushNotificationPriority)) {
            showNotification(pushNotificationType);
        }

        sendPushNotificationContentToActivity(pushNotificationType, pushNotificationPriority);
    }

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param pushNotificationType type of notification.
     */
    private void showNotification(PushNotificationType pushNotificationType) {
        if (pushNotificationType == null) {
            return;
        }
        Intent intent;
        if (storageManager.getToken() != null) {
            intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.putExtra(Constants.BundleKeys.PUSH_NOTIFICATION_TYPE, pushNotificationType);
        } else {
            intent = new Intent(this, LoginActivity.class);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
            .setSmallIcon(R.mipmap.ic_notification)
            .setColor(ContextCompat.getColor(this, R.color.notification_bg_color))
            .setContentTitle(pushNotificationType.toString())
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent);

        // Vibration
        notificationBuilder.setVibrate(VIBRATION_PATTERN);

        // LED
        notificationBuilder.setLights(Color.RED, LED_UPDATE_INTERVAL_IN_MILLISECONDS, LED_UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sound
        Uri sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.sonidolargo);
        notificationBuilder.setSound(sound);

        NotificationManager notificationManager =
            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());
    }

    /**
     * BroadcastReceiver is only one good way to inform about notification
     * to the Activity without clicking to notification view in the bar
     *
     * @param pushNotificationType     type of notification
     * @param pushNotificationPriority priority of push notification
     */
    private void sendPushNotificationContentToActivity(
        PushNotificationType pushNotificationType,
        PushNotificationPriority pushNotificationPriority
    ) {
        if (pushNotificationType == null) {
            return;
        }
        Intent intent = new Intent(Constants.PUSH_NOTIFICATION_RECEIVED);
        intent.putExtra(Constants.BundleKeys.PUSH_NOTIFICATION_TYPE, pushNotificationType);
        intent.putExtra(Constants.BundleKeys.PUSH_NOTIFICATION_PRIORITY, pushNotificationPriority);

        sendBroadcast(intent);
    }

    /**
     * in case when rider route stop plan can be changed with each iteration of algo assignment
     * rider should see such updates during working day.
     * <p/>
     * FOREGROUND_UPDATE - means that this push is important to the rider and he has to know about this action
     * BACKGROUND_UPDATE - means that this push is caused by changes that riders shouldn't know about
     *
     * @param pushNotificationPriority type of push notification
     * @return true if this notification should be shown to the rider
     */
    private boolean shouldNotifyUser(PushNotificationPriority pushNotificationPriority) {
        return pushNotificationPriority != null && pushNotificationPriority == PushNotificationPriority.FOREGROUND_UPDATE;
    }
}

