package com.foodpanda.urbanninja.api.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.foodpanda.urbanninja.model.hockeyapp.AppVersion;

import java.util.List;

public class HockeyAppVersionList implements Parcelable {
    private List<AppVersion> appVersions;

    public List<AppVersion> getAppVersions() {
        return appVersions;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.appVersions);
    }

    public HockeyAppVersionList() {
    }

    protected HockeyAppVersionList(Parcel in) {
        this.appVersions = in.createTypedArrayList(AppVersion.CREATOR);
    }

    public static final Parcelable.Creator<HockeyAppVersionList> CREATOR = new Parcelable.Creator<HockeyAppVersionList>() {
        @Override
        public HockeyAppVersionList createFromParcel(Parcel source) {
            return new HockeyAppVersionList(source);
        }

        @Override
        public HockeyAppVersionList[] newArray(int size) {
            return new HockeyAppVersionList[size];
        }
    };

    /**
     * Needs only for tests
     *
     * @param appVersions fake version list
     */
    public void setAppVersions(List<AppVersion> appVersions) {
        this.appVersions = appVersions;
    }
}
