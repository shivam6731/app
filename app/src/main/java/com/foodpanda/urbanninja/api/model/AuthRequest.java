package com.foodpanda.urbanninja.api.model;

public class AuthRequest {
    private String username;
    private String clientId = "logisticsDashboard";
    private String password;
    private String grantType = "password";

    public AuthRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
