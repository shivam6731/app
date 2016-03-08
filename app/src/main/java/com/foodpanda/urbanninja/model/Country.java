package com.foodpanda.urbanninja.model;

import android.os.Parcel;

public class Country implements ParcelableModel {
    private String code;
    private long id;
    private String environment;
    private String url;
    private String platform;
    private String version;
    private String component;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.code);
        dest.writeLong(this.id);
        dest.writeString(this.environment);
        dest.writeString(this.url);
        dest.writeString(this.platform);
        dest.writeString(this.version);
        dest.writeString(this.component);
    }

    public Country() {
    }

    protected Country(Parcel in) {
        this.code = in.readString();
        this.id = in.readLong();
        this.environment = in.readString();
        this.url = in.readString();
        this.platform = in.readString();
        this.version = in.readString();
        this.component = in.readString();
    }

    public static final Creator<Country> CREATOR = new Creator<Country>() {
        public Country createFromParcel(Parcel source) {
            return new Country(source);
        }

        public Country[] newArray(int size) {
            return new Country[size];
        }
    };

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
    public long getId() {
        return id;
    }

    public String getEnvironment() {
        return environment;
    }

    public String getUrl() {
        return url;
    }

    public String getPlatform() {
        return platform;
    }

    public String getVersion() {
        return version;
    }

    public String getComponent() {
        return component;
    }
}
