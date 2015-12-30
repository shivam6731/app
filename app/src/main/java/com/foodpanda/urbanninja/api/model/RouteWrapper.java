package com.foodpanda.urbanninja.api.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.foodpanda.urbanninja.model.Stop;

import java.util.List;

public class RouteWrapper implements Parcelable {
    private int id;
    private List<Stop> stops;

    public RouteWrapper() {
    }

    public int getId() {
        return id;
    }

    public List<Stop> getStops() {
        return stops;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeTypedList(stops);
    }

    protected RouteWrapper(Parcel in) {
        this.id = in.readInt();
        this.stops = in.createTypedArrayList(Stop.CREATOR);
    }

    public static final Parcelable.Creator<RouteWrapper> CREATOR = new Parcelable.Creator<RouteWrapper>() {
        public RouteWrapper createFromParcel(Parcel source) {
            return new RouteWrapper(source);
        }

        public RouteWrapper[] newArray(int size) {
            return new RouteWrapper[size];
        }
    };
}
