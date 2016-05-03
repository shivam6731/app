package com.foodpanda.urbanninja.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;

import org.joda.time.DateTime;

import java.util.List;

public class WorkingDay implements ParentListItem, ParcelableModel {
    private DateTime dateTime;
    private double total;
    private List<OrderReport> orderReports;

    public WorkingDay() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(this.dateTime);
        dest.writeDouble(this.total);
        dest.writeTypedList(orderReports);
    }

    protected WorkingDay(Parcel in) {
        this.dateTime = (DateTime) in.readSerializable();
        this.total = in.readDouble();
        this.orderReports = in.createTypedArrayList(OrderReport.CREATOR);
    }

    public static final Parcelable.Creator<WorkingDay> CREATOR = new Parcelable.Creator<WorkingDay>() {
        @Override
        public WorkingDay createFromParcel(Parcel source) {
            return new WorkingDay(source);
        }

        @Override
        public WorkingDay[] newArray(int size) {
            return new WorkingDay[size];
        }
    };

    @Override
    public List<?> getChildItemList() {
        return orderReports;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return false;
    }

    public void setDateTime(DateTime dateTime) {
        this.dateTime = dateTime;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public void setOrderReports(List<OrderReport> orderReports) {
        this.orderReports = orderReports;
    }

    public DateTime getDateTime() {
        return dateTime;
    }

    public double getTotal() {
        return total;
    }

}
