package com.foodpanda.urbanninja.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Country implements Parcelable {
    @SerializedName("code")
    private String code;

    @SerializedName("currency_code")
    private String currencyCode;

    @SerializedName("brand")
    private String brand;

    @SerializedName("title")
    private String title;

    @SerializedName("image")
    private String image;

    @SerializedName("url")
    private String url;

    public Country() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.code);
        dest.writeString(this.currencyCode);
        dest.writeString(this.brand);
        dest.writeString(this.title);
        dest.writeString(this.image);
        dest.writeString(this.url);
    }

    protected Country(Parcel in) {
        this.code = in.readString();
        this.currencyCode = in.readString();
        this.brand = in.readString();
        this.title = in.readString();
        this.image = in.readString();
        this.url = in.readString();
    }

    public static final Parcelable.Creator<Country> CREATOR = new Parcelable.Creator<Country>() {
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

    public String getCurrencyCode() {
        return currencyCode;
    }

    public String getBrand() {
        return brand;
    }

    public String getTitle() {
        return title;
    }

    public String getImage() {
        return image;
    }

    public String getUrl() {
        return url;
    }
}
