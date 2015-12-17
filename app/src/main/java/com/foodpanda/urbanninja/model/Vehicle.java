package com.foodpanda.urbanninja.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Vehicle implements Parcelable {
    private int id;
    private String status;
    private String vehicleType;
    private float capacityLimit;
    private GeoCoordinate geoCoordinate;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.status);
        dest.writeString(this.vehicleType);
        dest.writeFloat(this.capacityLimit);
        dest.writeParcelable(this.geoCoordinate, 0);
    }

    public Vehicle() {
    }

    protected Vehicle(Parcel in) {
        this.id = in.readInt();
        this.status = in.readString();
        this.vehicleType = in.readString();
        this.capacityLimit = in.readFloat();
        this.geoCoordinate = in.readParcelable(GeoCoordinate.class.getClassLoader());
    }

    public static final Parcelable.Creator<Vehicle> CREATOR = new Parcelable.Creator<Vehicle>() {
        public Vehicle createFromParcel(Parcel source) {
            return new Vehicle(source);
        }

        public Vehicle[] newArray(int size) {
            return new Vehicle[size];
        }
    };

    public int getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public float getCapacityLimit() {
        return capacityLimit;
    }

    public GeoCoordinate getGeoCoordinate() {
        return geoCoordinate;
    }
}
