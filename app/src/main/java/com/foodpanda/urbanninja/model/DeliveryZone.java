package com.foodpanda.urbanninja.model;

import android.os.Parcel;

import java.util.List;

public class DeliveryZone implements ParcelableModel {
    private int id;
    private String name;
    private StartingPoint startingPoint;
    private City city;
    private List<GeoCoordinate> polygon;
    private String timezone;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeParcelable(this.startingPoint, flags);
        dest.writeParcelable(this.city, flags);
        dest.writeTypedList(this.polygon);
        dest.writeString(this.timezone);
    }

    public DeliveryZone() {
    }

    protected DeliveryZone(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.startingPoint = in.readParcelable(StartingPoint.class.getClassLoader());
        this.city = in.readParcelable(City.class.getClassLoader());
        this.polygon = in.createTypedArrayList(GeoCoordinate.CREATOR);
        this.timezone = in.readString();
    }

    public static final Creator<DeliveryZone> CREATOR = new Creator<DeliveryZone>() {
        @Override
        public DeliveryZone createFromParcel(Parcel source) {
            return new DeliveryZone(source);
        }

        @Override
        public DeliveryZone[] newArray(int size) {
            return new DeliveryZone[size];
        }
    };

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public StartingPoint getStartingPoint() {
        return startingPoint;
    }

    public List<GeoCoordinate> getPolygon() {
        return polygon;
    }
}
