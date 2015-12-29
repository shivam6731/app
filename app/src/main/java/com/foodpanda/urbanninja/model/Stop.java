package com.foodpanda.urbanninja.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.foodpanda.urbanninja.model.enums.RouteStopStatus;

import org.joda.time.DateTime;

import java.util.Date;

public class Stop implements Parcelable {
    private int id;
    private int sequence;
    private int processingTimeSeconds;
    private int loadDelta;
    private int loadUponArrival;
    private TimeWindow timeWindow;
    private DateTime arrivalTime;
    private int sequenceNumber;
    private RouteStopStatus routeStopStatus;

    public Stop() {
    }
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
        dest.writeSerializable(this.arrivalTime);
        dest.writeInt(this.sequenceNumber);
        dest.writeInt(this.routeStopStatus == null ? -1 : this.routeStopStatus.ordinal());
    }

    protected Stop(Parcel in) {
        this.id = in.readInt();
        this.sequence = in.readInt();
        this.processingTimeSeconds = in.readInt();
        this.loadDelta = in.readInt();
        this.loadUponArrival = in.readInt();
        this.timeWindow = in.readParcelable(TimeWindow.class.getClassLoader());
        this.arrivalTime = (DateTime) in.readSerializable();
        this.sequenceNumber = in.readInt();
        int tmpRouteStopStatus = in.readInt();
        this.routeStopStatus = tmpRouteStopStatus == -1 ? null : RouteStopStatus.values()[tmpRouteStopStatus];
    }

    public static final Creator<Stop> CREATOR = new Creator<Stop>() {
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

    public DateTime getArrivalTime() {
        return arrivalTime;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public RouteStopStatus getRouteStopStatus() {
        return routeStopStatus;
    }
}
