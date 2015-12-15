package com.foodpanda.urbanninja.api.model;

import com.google.gson.annotations.SerializedName;

public class AuthRequest {
    private String username;
    @SerializedName("client_id")
    private String clientId = "logisticsDashboard";
    private String password;
    @SerializedName("grant_type")
    private String grantType = "password";

    public AuthRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
