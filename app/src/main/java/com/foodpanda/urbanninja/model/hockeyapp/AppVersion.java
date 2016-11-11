package com.foodpanda.urbanninja.model.hockeyapp;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * It's only part HockeyApp version model
 * that we need to check current version
 * <p/>
 * you can check documentation here https://support.hockeyapp.net/kb/api/api-versions#list-versions
 */
public class AppVersion implements Parcelable {
    private int version;
    private String shortversion;
    private String title;
    private String notes;
    private String downloadUrl;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.version);
        dest.writeString(this.shortversion);
        dest.writeString(this.title);
        dest.writeString(this.notes);
        dest.writeString(this.downloadUrl);
    }

    public AppVersion() {
    }

    /**
     * Needs only for tests
     */
    public AppVersion(int version, String shortversion, String downloadUrl) {
        this.version = version;
        this.shortversion = shortversion;
        this.downloadUrl = downloadUrl;
    }

    protected AppVersion(Parcel in) {
        this.version = in.readInt();
        this.shortversion = in.readString();
        this.title = in.readString();
        this.notes = in.readString();
        this.downloadUrl = in.readString();
    }

    public static final Parcelable.Creator<AppVersion> CREATOR = new Parcelable.Creator<AppVersion>() {
        @Override
        public AppVersion createFromParcel(Parcel source) {
            return new AppVersion(source);
        }

        @Override
        public AppVersion[] newArray(int size) {
            return new AppVersion[size];
        }
    };

    public int getVersion() {
        return version;
    }

    public String getShortversion() {
        return shortversion;
    }

    public String getTitle() {
        return title;
    }

    public String getNotes() {
        return notes;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }
}
