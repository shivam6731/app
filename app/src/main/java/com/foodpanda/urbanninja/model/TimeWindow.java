package com.foodpanda.urbanninja.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.joda.time.DateTime;

import java.util.Date;

public class TimeWindow implements Parcelable {
    private DateTime startTime;
    private DateTime endTime;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(this.startTime);
        dest.writeSerializable(this.endTime);
    }

    public TimeWindow() {
    }

    protected TimeWindow(Parcel in) {
        this.startTime = (DateTime) in.readSerializable();
        this.endTime = (DateTime) in.readSerializable();
    }

    public static final Creator<TimeWindow> CREATOR = new Creator<TimeWindow>() {
        public TimeWindow createFromParcel(Parcel source) {
            return new TimeWindow(source);
        }

        public TimeWindow[] newArray(int size) {
            return new TimeWindow[size];
        }
    };

    public DateTime getStartTime() {
        return startTime;
    }

    public DateTime getEndTime() {
        return endTime;
    }
}
