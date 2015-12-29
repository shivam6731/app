package com.foodpanda.urbanninja.model;

import android.os.Parcel;
import android.os.Parcelable;

public class StartPoint implements Parcelable {
    private int id;
    private String name;
    private String description;
    private GeoCoordinate geoCoordinate;

    public StartPoint() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeString(this.description);
        dest.writeParcelable(this.geoCoordinate, 0);
    }

    protected StartPoint(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.description = in.readString();
        this.geoCoordinate = in.readParcelable(GeoCoordinate.class.getClassLoader());
    }

    public static final Parcelable.Creator<StartPoint> CREATOR = new Parcelable.Creator<StartPoint>() {
        public StartPoint createFromParcel(Parcel source) {
            return new StartPoint(source);
        }

        public StartPoint[] newArray(int size) {
            return new StartPoint[size];
        }
    };

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public GeoCoordinate getGeoCoordinate() {
        return geoCoordinate;
    }
}
