package com.foodpanda.urbanninja.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.foodpanda.urbanninja.model.enums.RouteStopActivityType;

public class RouteStopActivity implements Parcelable {
    private int id;
    private RouteStopActivityType type;
    private String value;
    private String description;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.type == null ? -1 : this.type.ordinal());
        dest.writeString(this.value);
        dest.writeString(this.description);
    }

    public RouteStopActivity() {
    }

    protected RouteStopActivity(Parcel in) {
        this.id = in.readInt();
        int tmpType = in.readInt();
        this.type = tmpType == -1 ? null : RouteStopActivityType.values()[tmpType];
        this.value = in.readString();
        this.description = in.readString();
    }

    public static final Parcelable.Creator<RouteStopActivity> CREATOR = new Parcelable.Creator<RouteStopActivity>() {
        public RouteStopActivity createFromParcel(Parcel source) {
            return new RouteStopActivity(source);
        }

        public RouteStopActivity[] newArray(int size) {
            return new RouteStopActivity[size];
        }
    };

    public int getId() {
        return id;
    }

    public RouteStopActivityType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }
}
