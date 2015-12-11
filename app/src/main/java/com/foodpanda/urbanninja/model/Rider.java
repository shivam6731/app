package com.foodpanda.urbanninja.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Rider implements Parcelable {
    private int id;
    private String phoneNumber;
    private String firstName;
    private String surname;
    private String username;
    private String picture;
    private boolean deleted;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.phoneNumber);
        dest.writeString(this.firstName);
        dest.writeString(this.surname);
        dest.writeString(this.username);
        dest.writeString(this.picture);
        dest.writeByte(deleted ? (byte) 1 : (byte) 0);
    }

    public Rider() {
    }

    protected Rider(Parcel in) {
        this.id = in.readInt();
        this.phoneNumber = in.readString();
        this.firstName = in.readString();
        this.surname = in.readString();
        this.username = in.readString();
        this.picture = in.readString();
        this.deleted = in.readByte() != 0;
    }

    public static final Parcelable.Creator<Rider> CREATOR = new Parcelable.Creator<Rider>() {
        public Rider createFromParcel(Parcel source) {

            return new Rider(source);
        }

        public Rider[] newArray(int size) {

            return new Rider[size];
        }
    };

    public int getId() {
        return id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getSurname() {
        return surname;
    }

    public String getUsername() {
        return username;
    }

    public String getPicture() {
        return picture;
    }

    public boolean isDeleted() {
        return deleted;
    }
}
