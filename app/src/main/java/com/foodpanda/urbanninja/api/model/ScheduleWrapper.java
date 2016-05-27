package com.foodpanda.urbanninja.api.model;

import android.os.Parcel;

import com.foodpanda.urbanninja.model.DeliveryZone;
import com.foodpanda.urbanninja.model.ParcelableModel;
import com.foodpanda.urbanninja.model.Rider;
import com.foodpanda.urbanninja.model.TimeWindow;

import org.joda.time.DateTime;

public class ScheduleWrapper implements ParcelableModel {
    private int id;
    private Rider rider;
    private DeliveryZone deliveryZone;
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
        dest.writeParcelable(this.rider, 0);
        dest.writeParcelable(this.deliveryZone, 0);
        dest.writeParcelable(this.timeWindow, 0);
        dest.writeSerializable(this.clockedInAt);
        dest.writeByte(clockedIn ? (byte) 1 : (byte) 0);
    }

    protected ScheduleWrapper(Parcel in) {
        this.id = in.readInt();
        this.rider = in.readParcelable(Rider.class.getClassLoader());
        this.deliveryZone = in.readParcelable(DeliveryZone.class.getClassLoader());
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

    public Rider getRider() {
        return rider;
    }

    public DeliveryZone getDeliveryZone() {
        return deliveryZone;
    }

    public TimeWindow getTimeWindow() {
        return timeWindow;
    }

    public DateTime getclockedInAt() {
        return clockedInAt;
    }

    public boolean isClockedIn() {
        return clockedIn;
    }

    public void setTimeWindow(TimeWindow timeWindow) {
        this.timeWindow = timeWindow;
    }

    public boolean isScheduleFinished() {
        return (getTimeWindow() != null && getTimeWindow().getEndAt() != null) && getTimeWindow().getEndAt().isBeforeNow();
    }

    /**
     * needs only for unit tests
     *
     * @param clockedIn flag is clock-in
     */
    public void setClockedIn(boolean clockedIn) {
        this.clockedIn = clockedIn;
    }
}
