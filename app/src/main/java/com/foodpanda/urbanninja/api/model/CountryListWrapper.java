package com.foodpanda.urbanninja.api.model;

import android.os.Parcel;

import com.foodpanda.urbanninja.model.Country;
import com.foodpanda.urbanninja.model.Model;
import com.foodpanda.urbanninja.model.ParcelableModel;

import java.util.List;

public class CountryListWrapper implements ParcelableModel{
    private List<Country> data;

    public List<Country> getData() {
        return data;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(data);
    }

    public CountryListWrapper() {
    }

    protected CountryListWrapper(Parcel in) {
        this.data = in.createTypedArrayList(Country.CREATOR);
    }

    public static final Creator<CountryListWrapper> CREATOR = new Creator<CountryListWrapper>() {
        public CountryListWrapper createFromParcel(Parcel source) {
            return new CountryListWrapper(source);
        }

        public CountryListWrapper[] newArray(int size) {
            return new CountryListWrapper[size];
        }
    };
}
