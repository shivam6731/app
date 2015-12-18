package com.foodpanda.urbanninja.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class TimeWindow implements Parcelable {
    private Date start;
    private Date end;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(start != null ? start.getTime() : -1);
        dest.writeLong(end != null ? end.getTime() : -1);
    }

    public TimeWindow() {
    }

    protected TimeWindow(Parcel in) {
        long tmpStart = in.readLong();
        this.start = tmpStart == -1 ? null : new Date(tmpStart);
        long tmpEnd = in.readLong();
        this.end = tmpEnd == -1 ? null : new Date(tmpEnd);
    }

    public static final Creator<TimeWindow> CREATOR = new Creator<TimeWindow>() {
        public TimeWindow createFromParcel(Parcel source) {
            return new TimeWindow(source);
        }

        public TimeWindow[] newArray(int size) {
            return new TimeWindow[size];
        }
    };

    public Date getStart() {
        return start;
    }

    public Date getEnd() {
        return end;
    }
}
