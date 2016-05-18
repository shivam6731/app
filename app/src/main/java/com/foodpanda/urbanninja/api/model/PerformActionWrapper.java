package com.foodpanda.urbanninja.api.model;

import com.foodpanda.urbanninja.model.enums.Status;

import org.joda.time.DateTime;

public class PerformActionWrapper {
    private Status status;
    private DateTime actionPerformedAt;

    public PerformActionWrapper(Status status, DateTime actionPerformedAt) {
        this.status = status;
        this.actionPerformedAt = actionPerformedAt;
    }
}
