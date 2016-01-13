package com.foodpanda.urbanninja.api.model;

import com.foodpanda.urbanninja.model.enums.Action;

import org.joda.time.DateTime;

public class PerformActionWrapper {
    private Action action;
    private DateTime actionPerformedAt;

    public PerformActionWrapper(Action action, DateTime actionPerformedAt) {
        this.action = action;
        this.actionPerformedAt = actionPerformedAt;
    }
}
