package com.foodpanda.urbanninja.api.model;

import com.foodpanda.urbanninja.model.GeoCoordinate;
import com.foodpanda.urbanninja.model.Model;

import org.joda.time.DateTime;

public class RiderLocation implements Model {
    private GeoCoordinate geoCoordinate;
    private int batteryLevel;
    private int speedInKmh;
    private int azimuth;
    private int accuracyInMeters;
    private DateTime dateTime;

    public void setGeoCoordinate(GeoCoordinate geoCoordinate) {
        this.geoCoordinate = geoCoordinate;
    }

    public void setBatteryLevel(int batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public void setSpeedInKmh(int speedInKmh) {
        this.speedInKmh = speedInKmh;
    }

    public void setAzimuth(int azimuth) {
        this.azimuth = azimuth;
    }

    public void setAccuracyInMeters(int accuracyInMeters) {
        this.accuracyInMeters = accuracyInMeters;
    }

    public void setDateTime(DateTime dateTime) {
        this.dateTime = dateTime;
    }
}
