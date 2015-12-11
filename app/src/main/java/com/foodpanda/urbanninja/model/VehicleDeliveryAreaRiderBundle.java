package com.foodpanda.urbanninja.model;

import android.os.Parcel;
import android.os.Parcelable;

public class VehicleDeliveryAreaRiderBundle implements Parcelable {
    private DeliveryArea deliveryArea;
    private Vehicle vehicle;
    private Rider rider;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.deliveryArea, 0);
        dest.writeParcelable(this.vehicle, 0);
        dest.writeParcelable(this.rider, 0);
    }

    public VehicleDeliveryAreaRiderBundle() {
    }

    protected VehicleDeliveryAreaRiderBundle(Parcel in) {
        this.deliveryArea = in.readParcelable(DeliveryArea.class.getClassLoader());
        this.vehicle = in.readParcelable(Vehicle.class.getClassLoader());
        this.rider = in.readParcelable(Rider.class.getClassLoader());
    }

    public static final Parcelable.Creator<VehicleDeliveryAreaRiderBundle> CREATOR = new Parcelable.Creator<VehicleDeliveryAreaRiderBundle>() {
        public VehicleDeliveryAreaRiderBundle createFromParcel(Parcel source) {
            return new VehicleDeliveryAreaRiderBundle(source);
        }

        public VehicleDeliveryAreaRiderBundle[] newArray(int size) {
            return new VehicleDeliveryAreaRiderBundle[size];
        }
    };

    public DeliveryArea getDeliveryArea() {
        return deliveryArea;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public Rider getRider() {
        return rider;
    }
}
