package com.foodpanda.urbanninja.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class TimeWindow implements Parcelable {
    private Date startTime;
    private Date endTime;

    public TimeWindow() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(startTime != null ? startTime.getTime() : -1);
        dest.writeLong(endTime != null ? endTime.getTime() : -1);
    }

    protected TimeWindow(Parcel in) {
        long tmpStart = in.readLong();
        this.startTime = tmpStart == -1 ? null : new Date(tmpStart);
        long tmpEnd = in.readLong();
        this.endTime = tmpEnd == -1 ? null : new Date(tmpEnd);
    }

    public static final Creator<TimeWindow> CREATOR = new Creator<TimeWindow>() {
        public TimeWindow createFromParcel(Parcel source) {
            return new TimeWindow(source);
        }

        public TimeWindow[] newArray(int size) {
            return new TimeWindow[size];
        }
    };

    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
        return endTime;
    }
}
