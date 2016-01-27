package com.foodpanda.urbanninja.api.model;

import com.foodpanda.urbanninja.model.Model;

public class PushNotificationRegistrationWrapper implements Model {
    private String deviceId;

    public PushNotificationRegistrationWrapper(String deviceId) {
        this.deviceId = deviceId;
    }
}
