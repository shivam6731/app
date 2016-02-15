package com.foodpanda.urbanninja.model.enums;

public enum PushNotificationType {
    ROUTE_UPDATED("ROUTE_UPDATED"),
    SCHEDULE_UPDATED("SCHEDULE_UPDATED");

    private final String text;

    PushNotificationType(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
