package com.foodpanda.urbanninja.model;

import android.os.Parcel;
import android.os.Parcelable;

public class DeliveryArea implements Parcelable {
    private int id;
    private String name;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
    }

    public DeliveryArea() {
    }

    protected DeliveryArea(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
    }

    public static final Parcelable.Creator<DeliveryArea> CREATOR = new Parcelable.Creator<DeliveryArea>() {
        public DeliveryArea createFromParcel(Parcel source) {
            return new DeliveryArea(source);
        }

        public DeliveryArea[] newArray(int size) {
            return new DeliveryArea[size];
        }
    };

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
