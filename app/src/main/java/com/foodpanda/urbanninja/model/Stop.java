package com.foodpanda.urbanninja.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.foodpanda.urbanninja.model.enums.RouteStopStatus;
import com.foodpanda.urbanninja.model.enums.RouteStopTaskStatus;

import org.joda.time.DateTime;

public class Stop implements ParcelableModel {
    private int id;
    private int locationId;
    private int sequence;
    private int processingTimeSeconds;
    private int loadDelta;
    private int loadUponArrival;
    private TimeWindow timeWindow;
    private DateTime arrivalTime;
    private RouteStopStatus status;
    private GeoCoordinate gps;
    private String name;
    private String comment;
    private String address;
    private RouteStopTaskStatus task;

    public Stop() {
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.locationId);
        dest.writeInt(this.sequence);
        dest.writeInt(this.processingTimeSeconds);
        dest.writeInt(this.loadDelta);
        dest.writeInt(this.loadUponArrival);
        dest.writeParcelable(this.timeWindow, 0);
        dest.writeSerializable(this.arrivalTime);
        dest.writeInt(this.status == null ? -1 : this.status.ordinal());
        dest.writeParcelable(this.gps, 0);
        dest.writeString(this.name);
        dest.writeString(this.comment);
        dest.writeString(this.address);
        dest.writeInt(this.task == null ? -1 : this.task.ordinal());
    }

    protected Stop(Parcel in) {
        this.id = in.readInt();
        this.locationId = in.readInt();
        this.sequence = in.readInt();
        this.processingTimeSeconds = in.readInt();
        this.loadDelta = in.readInt();
        this.loadUponArrival = in.readInt();
        this.timeWindow = in.readParcelable(TimeWindow.class.getClassLoader());
        this.arrivalTime = (DateTime) in.readSerializable();
        int tmpStatus = in.readInt();
        this.status = tmpStatus == -1 ? null : RouteStopStatus.values()[tmpStatus];
        this.gps = in.readParcelable(GeoCoordinate.class.getClassLoader());
        this.name = in.readString();
        this.comment = in.readString();
        this.address = in.readString();
        int tmpTask = in.readInt();
        this.task = tmpTask == -1 ? null : RouteStopTaskStatus.values()[tmpTask];
    }

    public static final Parcelable.Creator<Stop> CREATOR = new Parcelable.Creator<Stop>() {
        public Stop createFromParcel(Parcel source) {
            return new Stop(source);
        }

        public Stop[] newArray(int size) {
            return new Stop[size];
        }
    };

    public int getId() {
        return id;
    }

    public int getLocationId() {
        return locationId;
    }

    public int getSequence() {
        return sequence;
    }

    public int getProcessingTimeSeconds() {
        return processingTimeSeconds;
    }

    public int getLoadDelta() {
        return loadDelta;
    }

    public int getLoadUponArrival() {
        return loadUponArrival;
    }

    public TimeWindow getTimeWindow() {
        return timeWindow;
    }

    public DateTime getArrivalTime() {
        return arrivalTime;
    }

    public RouteStopStatus getStatus() {
        return status;
    }

    public GeoCoordinate getGps() {
        return gps;
    }

    public String getName() {
        return name;
    }

    public String getComment() {
        return comment;
    }

    public String getAddress() {
        return address;
    }

    public RouteStopTaskStatus getTask() {
        return task;
    }
}
