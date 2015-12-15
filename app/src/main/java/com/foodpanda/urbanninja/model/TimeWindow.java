package com.foodpanda.urbanninja.model;

import android.os.Parcel;
import android.os.Parcelable;

public class TimeWindow implements Parcelable {
    private String start;
    private String end;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.start);
        dest.writeString(this.end);
    }

    public TimeWindow() {
    }

    protected TimeWindow(Parcel in) {
        this.start = in.readString();
        this.end = in.readString();
    }

    public static final Parcelable.Creator<TimeWindow> CREATOR = new Parcelable.Creator<TimeWindow>() {
        public TimeWindow createFromParcel(Parcel source) {

            return new TimeWindow(source);
        }

        public TimeWindow[] newArray(int size) {

            return new TimeWindow[size];
        }
    };

    public String getStart() {
        return start;
    }

    public String getEnd() {
        return end;
    }
}
