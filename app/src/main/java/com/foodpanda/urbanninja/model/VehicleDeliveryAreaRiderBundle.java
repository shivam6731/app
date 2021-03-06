package com.foodpanda.urbanninja.model;

import android.os.Parcel;
import android.os.Parcelable;

public class VehicleDeliveryAreaRiderBundle implements ParcelableModel {
    private DeliveryZone deliveryZone;
    private Vehicle vehicle;
    private Rider rider;

    public VehicleDeliveryAreaRiderBundle() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.deliveryZone, 0);
        dest.writeParcelable(this.vehicle, 0);
        dest.writeParcelable(this.rider, 0);
    }

    protected VehicleDeliveryAreaRiderBundle(Parcel in) {
        this.deliveryZone = in.readParcelable(DeliveryZone.class.getClassLoader());
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

    public DeliveryZone getDeliveryZone() {
        return deliveryZone;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public Rider getRider() {
        return rider;
    }

    /**
     * Needs only for tests
     *
     * @param rider fake rider
     */
    public void setRider(Rider rider) {
        this.rider = rider;
    }

    /**
     * Needs only for tests
     *
     * @param vehicle fake vehicle
     */
    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }
}
