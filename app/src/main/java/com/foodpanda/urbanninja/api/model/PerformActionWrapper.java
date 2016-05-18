package com.foodpanda.urbanninja.api.model;

import com.foodpanda.urbanninja.model.enums.Status;

import org.joda.time.DateTime;

public class PerformActionWrapper {
    private Status action;
    private DateTime actionPerformedAt;

    public PerformActionWrapper(Status action, DateTime actionPerformedAt) {
        this.action = action;
        this.actionPerformedAt = actionPerformedAt;
    }
}
