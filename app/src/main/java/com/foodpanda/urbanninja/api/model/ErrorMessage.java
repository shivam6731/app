package com.foodpanda.urbanninja.api.model;

public class ErrorMessage {
    private int status;
    private String message;
    private String developerMessage;

    public ErrorMessage(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public ErrorMessage() {
    }

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
