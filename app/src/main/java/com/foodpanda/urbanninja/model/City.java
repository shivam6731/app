package com.foodpanda.urbanninja.model;

import android.os.Parcel;
import android.os.Parcelable;

public class City implements Parcelable {
    private long id;
    private String name;
    private String countryCode;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.name);
        dest.writeString(this.countryCode);
    }

    public City() {
    }

    protected City(Parcel in) {
        this.id = in.readLong();
        this.name = in.readString();
        this.countryCode = in.readString();
    }

    public static final Parcelable.Creator<City> CREATOR = new Parcelable.Creator<City>() {
        @Override
        public City createFromParcel(Parcel source) {
            return new City(source);
        }

        @Override
        public City[] newArray(int size) {
            return new City[size];
        }
    };

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCountryCode() {
        return countryCode;
    }
}
