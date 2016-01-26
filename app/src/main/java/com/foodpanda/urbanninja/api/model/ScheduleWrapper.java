package com.foodpanda.urbanninja.api.model;

import android.os.Parcel;

import com.foodpanda.urbanninja.model.DeliveryArea;
import com.foodpanda.urbanninja.model.ParcelableModel;
import com.foodpanda.urbanninja.model.Rider;
import com.foodpanda.urbanninja.model.StartPoint;
import com.foodpanda.urbanninja.model.TimeWindow;

import org.joda.time.DateTime;

public class ScheduleWrapper implements ParcelableModel {
    private int id;
    private StartPoint startingPoint;
    private Rider rider;
    private DeliveryArea deliveryArea;
    private TimeWindow timeWindow;
    private DateTime clockedInAt;
    private boolean clockedIn;

    public ScheduleWrapper() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeParcelable(this.startingPoint, 0);
        dest.writeParcelable(this.rider, 0);
        dest.writeParcelable(this.deliveryArea, 0);
        dest.writeParcelable(this.timeWindow, 0);
        dest.writeSerializable(this.clockedInAt);
        dest.writeByte(clockedIn ? (byte) 1 : (byte) 0);
    }

    protected ScheduleWrapper(Parcel in) {
        this.id = in.readInt();
        this.startingPoint = in.readParcelable(StartPoint.class.getClassLoader());
        this.rider = in.readParcelable(Rider.class.getClassLoader());
        this.deliveryArea = in.readParcelable(DeliveryArea.class.getClassLoader());
        this.timeWindow = in.readParcelable(TimeWindow.class.getClassLoader());
        this.clockedInAt = (DateTime) in.readSerializable();
        this.clockedIn = in.readByte() != 0;
    }

    public static final Creator<ScheduleWrapper> CREATOR = new Creator<ScheduleWrapper>() {
        public ScheduleWrapper createFromParcel(Parcel source) {
            return new ScheduleWrapper(source);
        }

        public ScheduleWrapper[] newArray(int size) {
            return new ScheduleWrapper[size];
        }
    };

    public int getId() {
        return id;
    }

    public StartPoint getStartingPoint() {
        return startingPoint;
    }

    public Rider getRider() {
        return rider;
    }

    public DeliveryArea getDeliveryArea() {
        return deliveryArea;
    }

    public TimeWindow getTimeWindow() {
        return timeWindow;
    }

    public DateTime getclockedInAt() {
        return clockedInAt;
    }

    public boolean isclockedIn() {
        return clockedIn;
    }
}
