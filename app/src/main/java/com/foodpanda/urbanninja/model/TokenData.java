package com.foodpanda.urbanninja.model;

import com.google.gson.annotations.SerializedName;

public class TokenData {
    @SerializedName("user_id")
    private int userId;

    @SerializedName("iss")
    private String iss;

    @SerializedName("user_class")
    private String userClass;

    @SerializedName("user_roles")
    private String[] userRoles;

    @SerializedName("exp")
    private long exp;

    @SerializedName("iat")
    private long iat;

    public int getUserId() {
        return userId;
    }

    public String getIss() {
        return iss;
    }

    public String getUserClass() {
        return userClass;
    }

    public String[] getUserRoles() {
        return userRoles;
    }

    public long getExp() {
        return exp;
    }

    public long getIat() {
        return iat;
    }
}
