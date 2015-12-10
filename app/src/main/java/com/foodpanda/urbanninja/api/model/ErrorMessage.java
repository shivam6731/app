package com.foodpanda.urbanninja.api.model;

public class ErrorMessage {
    private int status;
    private String message;
    private String developerMessage;

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getDeveloperMessage() {
        return developerMessage;
    }
}
