package com.foodpanda.urbanninja.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.foodpanda.urbanninja.model.enums.RouteStopStatus;

import java.util.Date;

public class Stop implements Parcelable {
    private int id;
    private int sequence;
    private int processingTimeSeconds;
    private int loadDelta;
    private int loadUponArrival;
    private TimeWindow timeWindow;
    private Date arrivalTime;
    private int sequenceNumber;
    private RouteStopStatus routeStopStatus;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.sequence);
        dest.writeInt(this.processingTimeSeconds);
        dest.writeInt(this.loadDelta);
        dest.writeInt(this.loadUponArrival);
        dest.writeParcelable(this.timeWindow, 0);
        dest.writeLong(arrivalTime != null ? arrivalTime.getTime() : -1);
        dest.writeInt(this.sequenceNumber);
        dest.writeInt(this.routeStopStatus == null ? -1 : this.routeStopStatus.ordinal());
    }

    public Stop() {
    }

    protected Stop(Parcel in) {
        this.id = in.readInt();
        this.sequence = in.readInt();
        this.processingTimeSeconds = in.readInt();
        this.loadDelta = in.readInt();
        this.loadUponArrival = in.readInt();
        this.timeWindow = in.readParcelable(TimeWindow.class.getClassLoader());
        long tmpArrivalTime = in.readLong();
        this.arrivalTime = tmpArrivalTime == -1 ? null : new Date(tmpArrivalTime);
        this.sequenceNumber = in.readInt();
        int tmpStatus = in.readInt();
        this.routeStopStatus = tmpStatus == -1 ? null : RouteStopStatus.values()[tmpStatus];
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

    public Date getArrivalTime() {
        return arrivalTime;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public RouteStopStatus getRouteStopStatus() {
        return routeStopStatus;
    }
}
