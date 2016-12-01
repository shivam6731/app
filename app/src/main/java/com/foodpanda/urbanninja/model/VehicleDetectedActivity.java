package com.foodpanda.urbanninja.model;

import com.foodpanda.urbanninja.model.enums.VehicleDetectedActivityType;

public class VehicleDetectedActivity implements Model {
    private VehicleDetectedActivityType activityType;
    private int confidence;

    public VehicleDetectedActivity(VehicleDetectedActivityType activityType, int confidence) {
        this.activityType = activityType;
        this.confidence = confidence;
    }
}
