package com.foodpanda.urbanninja.api.model;

import com.foodpanda.urbanninja.model.enums.Status;

import org.joda.time.DateTime;

public class PerformActionWrapper {
    private Status action;
    private String actionPerformedAt;

    public PerformActionWrapper(Status action, DateTime actionPerformedAt) {
        this.action = action;
        this.actionPerformedAt = convertDateToString(actionPerformedAt);
    }

    /**
     * As exception we need milliseconds for rider actions
     * to get rid of missed sequence of actions in the database
     * and to do so we changed formatter
     *
     * @param actionPerformedAt time when action happened
     * @return formatter with milliseconds string
     */
    private String convertDateToString(DateTime actionPerformedAt) {
        return actionPerformedAt.toString();
    }

    /**
     * Needs only for test
     *
     * @return formatter time of action
     */
    String getActionPerformedAt() {
        return actionPerformedAt;
    }
}
