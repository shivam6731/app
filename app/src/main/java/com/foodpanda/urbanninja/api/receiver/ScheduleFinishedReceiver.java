package com.foodpanda.urbanninja.api.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.foodpanda.urbanninja.api.service.LocationService;

/**
 * This is a result of {@link android.app.AlarmManager } that would trigger
 * when schedule of current rider would be finished
 * We need in to unsubscribe for all location updates
 */
public class ScheduleFinishedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        /**
         * stop {@link LocationService} to stop sending riderLocation to the server
         * as soon as working day is over
         */
        Intent closeServiceIntent = new Intent(context, LocationService.class);
        context.stopService(closeServiceIntent);
    }
}
