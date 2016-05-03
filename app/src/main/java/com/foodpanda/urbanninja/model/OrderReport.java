package com.foodpanda.urbanninja.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class OrderReport implements ParcelableModel {
    private String code;
    private List<OrderStop> orderSteps;

    public OrderReport() {
    }

    public OrderReport(String code, List<OrderStop> orderSteps) {
        this.code = code;
        this.orderSteps = orderSteps;
    }

    protected OrderReport(Parcel in) {
        this.code = in.readString();
        this.orderSteps = in.createTypedArrayList(OrderStop.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.code);
        dest.writeTypedList(orderSteps);
    }

    public static final Parcelable.Creator<OrderReport> CREATOR = new Parcelable.Creator<OrderReport>() {
        @Override
        public OrderReport createFromParcel(Parcel source) {
            return new OrderReport(source);
        }

        @Override
        public OrderReport[] newArray(int size) {
            return new OrderReport[size];
        }
    };

    public String getCode() {
        return code;
    }

    public List<OrderStop> getOrderSteps() {
        return orderSteps;
    }
}
