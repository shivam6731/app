package com.foodpanda.urbanninja.api.service;

import android.app.IntentService;
import android.content.Intent;

import com.foodpanda.urbanninja.App;
import com.foodpanda.urbanninja.manager.StorageManager;
import com.foodpanda.urbanninja.model.VehicleDetectedActivity;
import com.foodpanda.urbanninja.model.enums.VehicleDetectedActivityType;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.List;

import javax.inject.Inject;


public class VehicleDetectedActivityService extends IntentService {
    @Inject
    StorageManager storageManager;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public VehicleDetectedActivityService(String name) {
        super(name);
    }

    public VehicleDetectedActivityService() {
        super(VehicleDetectedActivityService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //injection
        App.get(getApplicationContext()).getMainComponent().inject(this);

        if (ActivityRecognitionResult.hasResult(intent)) {
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            saveDetectedActivities(result.getProbableActivities());
        }
    }

    /**
     * we have to store last rider activity and after send it to the server side
     * <p/>
     * to do so we just store last rider activity from the list (as far as I understood this list has always one item)
     *
     * @param probableActivities list of rider activities
     */
    private void saveDetectedActivities(List<DetectedActivity> probableActivities) {
        if (probableActivities != null && !probableActivities.isEmpty()) {
            DetectedActivity detectedActivity = probableActivities.get(0);
            storageManager.storeVehicleDetectedActivity(
                new VehicleDetectedActivity(
                    VehicleDetectedActivityType.fromInteger(detectedActivity.getType()),
                    detectedActivity.getConfidence())
            );
        }
    }
}
