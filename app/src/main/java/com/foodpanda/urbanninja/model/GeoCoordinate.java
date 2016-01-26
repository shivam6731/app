package com.foodpanda.urbanninja.model;

import android.os.Parcel;
import android.os.Parcelable;

public class GeoCoordinate implements ParcelableModel {
    private Double lat;

    private Double lon;

    public GeoCoordinate() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.lat);
        dest.writeValue(this.lon);
    }

    protected GeoCoordinate(Parcel in) {
        this.lat = (Double) in.readValue(Double.class.getClassLoader());
        this.lon = (Double) in.readValue(Double.class.getClassLoader());
    }

    public static final Parcelable.Creator<GeoCoordinate> CREATOR = new Parcelable.Creator<GeoCoordinate>() {
        public GeoCoordinate createFromParcel(Parcel source) {
            return new GeoCoordinate(source);
        }

        public GeoCoordinate[] newArray(int size) {
            return new GeoCoordinate[size];
        }
    };

    public Double getLat() {
        return lat;
    }

    public Double getLon() {
        return lon;
    }
}
