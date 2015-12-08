package com.foodpanda.urbanninja.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Token implements Parcelable {
    private String accessToken;
    private String tokenType;
    private int expiresIn;
    private String refreshToken;

    public String getAccessToken() {
        return accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public int getExpiresIn() {
        return expiresIn;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.accessToken);
        dest.writeString(this.tokenType);
        dest.writeInt(this.expiresIn);
        dest.writeString(this.refreshToken);
    }

    public Token() {
    }

    protected Token(Parcel in) {
        this.accessToken = in.readString();
        this.tokenType = in.readString();
        this.expiresIn = in.readInt();
        this.refreshToken = in.readString();
    }

    public static final Parcelable.Creator<Token> CREATOR = new Parcelable.Creator<Token>() {
        public Token createFromParcel(Parcel source) {
            return new Token(source);
        }

        public Token[] newArray(int size) {
            return new Token[size];
        }
    };
}
