package com.foodpanda.urbanninja.model;

import android.os.Parcel;

import com.foodpanda.urbanninja.model.enums.Action;
import com.foodpanda.urbanninja.model.enums.RouteStopStatus;

public class OrderStop implements ParcelableModel {
    private String name;
    private double value;
    private RouteStopStatus routeStopStatus;
    private Action action;

    public OrderStop(String name, double value, RouteStopStatus routeStopStatus, Action action) {
        this.name = name;
        this.value = value;
        this.routeStopStatus = routeStopStatus;
        this.action = action;
    }

    public OrderStop() {
    }

    protected OrderStop(Parcel in) {
        this.name = in.readString();
        this.value = in.readDouble();
        int tmpRouteStopTaskStatus = in.readInt();
        this.routeStopStatus = tmpRouteStopTaskStatus == -1 ? null : RouteStopStatus.values()[tmpRouteStopTaskStatus];
        int tmpAction = in.readInt();
        this.action = tmpAction == -1 ? null : Action.values()[tmpAction];
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeDouble(this.value);
        dest.writeInt(this.routeStopStatus == null ? -1 : this.routeStopStatus.ordinal());
        dest.writeInt(this.action == null ? -1 : this.action.ordinal());
    }

    public static final Creator<OrderStop> CREATOR = new Creator<OrderStop>() {
        @Override
        public OrderStop createFromParcel(Parcel source) {
            return new OrderStop(source);
        }

        @Override
        public OrderStop[] newArray(int size) {
            return new OrderStop[size];
        }
    };

    public String getName() {
        return name;
    }

    public double getValue() {
        return value;
    }

    public RouteStopStatus getRouteStopStatus() {
        return routeStopStatus;
    }

    public Action getAction() {
        return action;
    }
}
