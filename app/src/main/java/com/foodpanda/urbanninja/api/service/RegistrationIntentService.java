package com.foodpanda.urbanninja.api.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.foodpanda.urbanninja.App;
import com.foodpanda.urbanninja.R;
import com.foodpanda.urbanninja.manager.ApiManager;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

public class RegistrationIntentService extends IntentService {
    private ApiManager apiManager;
    private static final String TAG = RegistrationIntentService.class.getSimpleName();

    public RegistrationIntentService() {
        super(TAG);
        apiManager = App.API_MANAGER;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

            apiManager.registerDeviceId(token);
            Log.i(TAG, "GCM Registration Token: " + token);
        } catch (Exception e) {
            Log.e(TAG, "Failed to complete token refresh", e);
        }
    }

}
