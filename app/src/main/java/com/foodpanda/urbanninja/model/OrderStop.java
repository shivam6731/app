package com.foodpanda.urbanninja.model;

import android.os.Parcel;

import com.foodpanda.urbanninja.model.enums.Status;
import com.foodpanda.urbanninja.model.enums.RouteStopTask;

public class OrderStop implements ParcelableModel {
    private String name;
    private double value;
    private RouteStopTask task;
    private Status status;

    public OrderStop() {
    }

    protected OrderStop(Parcel in) {
        this.name = in.readString();
        this.value = in.readDouble();
        int tmpRouteStopTaskStatus = in.readInt();
        this.task = tmpRouteStopTaskStatus == -1 ? null : RouteStopTask.values()[tmpRouteStopTaskStatus];
        int tmpAction = in.readInt();
        this.status = tmpAction == -1 ? null : Status.values()[tmpAction];
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeDouble(this.value);
        dest.writeInt(this.task == null ? -1 : this.task.ordinal());
        dest.writeInt(this.status == null ? -1 : this.status.ordinal());
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

    public RouteStopTask getTask() {
        return task;
    }

    public Status getStatus() {
        return status;
    }
}
