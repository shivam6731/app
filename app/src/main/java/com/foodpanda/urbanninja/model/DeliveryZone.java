package com.foodpanda.urbanninja.model;

import android.os.Parcel;

public class DeliveryZone implements ParcelableModel {
    private int id;
    private String name;
    private StartingPoint startingPoint;

    public DeliveryZone() {
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeParcelable(this.startingPoint, flags);
    }

    protected DeliveryZone(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.startingPoint = in.readParcelable(StartingPoint.class.getClassLoader());
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
}
