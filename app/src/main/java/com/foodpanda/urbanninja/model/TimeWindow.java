package com.foodpanda.urbanninja.model;

import android.os.Parcel;

import org.joda.time.DateTime;

public class TimeWindow implements ParcelableModel {
    private DateTime startAt;
    private DateTime endAt;

    public TimeWindow() {
    }

    public TimeWindow(DateTime startAt, DateTime endAt) {
        this.startAt = startAt;
        this.endAt = endAt;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(this.startAt);
        dest.writeSerializable(this.endAt);
    }

    protected TimeWindow(Parcel in) {
        this.startAt = (DateTime) in.readSerializable();
        this.endAt = (DateTime) in.readSerializable();
    }


    public static final Creator<TimeWindow> CREATOR = new Creator<TimeWindow>() {
        public TimeWindow createFromParcel(Parcel source) {
            return new TimeWindow(source);
        }

        public TimeWindow[] newArray(int size) {
            return new TimeWindow[size];
        }
    };

    public DateTime getStartAt() {
        return startAt;
    }

    public DateTime getEndAt() {
        return endAt;
    }
}
