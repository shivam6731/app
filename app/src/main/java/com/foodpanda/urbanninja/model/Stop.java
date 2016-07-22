package com.foodpanda.urbanninja.model;

import android.os.Parcel;

import com.foodpanda.urbanninja.model.enums.Status;
import com.foodpanda.urbanninja.model.enums.RouteStopTask;

import org.joda.time.DateTime;

import java.util.List;

public class Stop implements MapDetailsProvider {
    private long id;
    private int locationId;
    private int sequence;
    private int processingTimeSeconds;
    private int loadDelta;
    private int loadUponArrival;
    private TimeWindow timeWindow;
    private DateTime arrivalTime;
    private Status status;
    private GeoCoordinate gps;
    private String name;
    private String comment;
    private String address;
    private RouteStopTask task;
    private List<RouteStopActivity> activities;
    private String pickupPhone;
    private String deliveryPhone;
    private long orderId;
    private String orderCode;
    private String vendorName;

    /**
     * need this constructor only for test
     */
    Stop(String deliveryPhone, String pickupPhone, RouteStopTask task) {
        this.deliveryPhone = deliveryPhone;
        this.pickupPhone = pickupPhone;
        this.task = task;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
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
        dest.writeTypedList(activities);
        dest.writeString(pickupPhone);
        dest.writeString(deliveryPhone);
        dest.writeLong(orderId);
        dest.writeString(orderCode);
        dest.writeString(vendorName);
    }

    public Stop() {
    }

    protected Stop(Parcel in) {
        this.id = in.readLong();
        this.locationId = in.readInt();
        this.sequence = in.readInt();
        this.processingTimeSeconds = in.readInt();
        this.loadDelta = in.readInt();
        this.loadUponArrival = in.readInt();
        this.timeWindow = in.readParcelable(TimeWindow.class.getClassLoader());
        this.arrivalTime = (DateTime) in.readSerializable();
        int tmpStatus = in.readInt();
        this.status = tmpStatus == -1 ? null : Status.values()[tmpStatus];
        this.gps = in.readParcelable(GeoCoordinate.class.getClassLoader());
        this.name = in.readString();
        this.comment = in.readString();
        this.address = in.readString();
        int tmpTask = in.readInt();
        this.task = tmpTask == -1 ? null : RouteStopTask.values()[tmpTask];
        this.activities = in.createTypedArrayList(RouteStopActivity.CREATOR);
        this.pickupPhone = in.readString();
        this.deliveryPhone = in.readString();
        this.orderId = in.readLong();
        this.orderCode = in.readString();
        this.vendorName = in.readString();
    }

    public static final Creator<Stop> CREATOR = new Creator<Stop>() {
        public Stop createFromParcel(Parcel source) {
            return new Stop(source);
        }

        public Stop[] newArray(int size) {
            return new Stop[size];
        }
    };

    public long getId() {
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

    public Status getStatus() {
        return status;
    }

    public String getName() {
        return name;
    }

    @Override
    public String getPhoneNumber() {
        return RouteStopTask.DELIVER == getTask() ? deliveryPhone : pickupPhone;
    }

    public RouteStopTask getTask() {
        return task;
    }

    public List<RouteStopActivity> getActivities() {
        return activities;
    }

    public String getPickupPhone() {
        return pickupPhone;
    }

    public String getDeliveryPhone() {
        return deliveryPhone;
    }

    public long getOrderId() {
        return orderId;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setTask(RouteStopTask task) {
        this.task = task;
    }

    public void setActivities(List<RouteStopActivity> activities) {
        this.activities = activities;
    }

    @Override
    public GeoCoordinate getCoordinate() {
        return gps;
    }

    @Override
    public String getAddress() {
        return address;
    }

    @Override
    public String getComment() {
        return comment;
    }

    public String getOrderCode() {
        return orderCode;
    }

    @Override
    public boolean isDoneButtonVisible() {
        return status == Status.ON_THE_WAY;
    }

    /**
     * Needs only for tests
     *
     * @param orderCode order identity code
     */
    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }
}
