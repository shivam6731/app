package com.foodpanda.urbanninja.api.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.foodpanda.urbanninja.model.enums.Status;

import org.joda.time.DateTime;

public class PerformActionWrapper {
    private Status action;
    private String actionPerformedAt;
    private RiderLocation riderLocation;

    public PerformActionWrapper(
        @NonNull Status action,
        @NonNull DateTime actionPerformedAt,
        @Nullable RiderLocation riderLocation
    ) {
        this.action = action;
        this.actionPerformedAt = convertDateToString(actionPerformedAt);
        this.riderLocation = riderLocation;
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
