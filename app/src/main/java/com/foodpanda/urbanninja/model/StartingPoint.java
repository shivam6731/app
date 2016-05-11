package com.foodpanda.urbanninja.model;

import android.os.Parcel;
import android.os.Parcelable;

public class StartingPoint implements MapDetailsProvider {
    private int id;
    private String name;
    private String description;
    private GeoCoordinate geoCoordinate;

    public StartingPoint() {
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

    protected StartingPoint(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.description = in.readString();
        this.geoCoordinate = in.readParcelable(GeoCoordinate.class.getClassLoader());
    }

    public static final Parcelable.Creator<StartingPoint> CREATOR = new Parcelable.Creator<StartingPoint>() {
        public StartingPoint createFromParcel(Parcel source) {
            return new StartingPoint(source);
        }

        public StartingPoint[] newArray(int size) {
            return new StartingPoint[size];
        }
    };

    public int getId() {
        return id;
    }

    @Override
    public GeoCoordinate getCoordinate() {
        return geoCoordinate;
    }

    @Override
    public String getAddress() {
        return description;
    }

    @Override
    public String getComment() {
        return null;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getPhoneNumber() {
        return "";
    }

    @Override
    public String getVendorName() {
        return null;
    }

    @Override
    public boolean isDoneButtonVisible() {
        return false;
    }

}
