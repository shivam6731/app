package com.foodpanda.urbanninja.ui.interfaces;

import android.widget.TextView;

import org.joda.time.DateTime;

/**
 * Interface provides all information for
 * screens with timer inside
 */
public interface TimerDataProvider {

    /**
     * provide TextView for the clock witch we would change every second by timer
     *
     * @return TextView with big text size (according to our design)
     */

    TextView provideTimerTextView();

    /**
     * provide TextView the for the description values where
     * we would see do we have some extra time or we are late
     *
     * @return TextView with big text size (according to our design)
     * @see #provideLeftString()
     * @see #providePassedString()
     */
    TextView provideTimerDescriptionTextView();

    /**
     * provide start time for any action with timer
     *
     * @return DateTime from the server side
     */
    DateTime provideScheduleDate();

    /**
     * provide end time for any action with timer
     *
     * @return DateTime from the server side
     */
    DateTime provideScheduleEndDate();

    /**
     * provide description of the left time value for the TextView
     *
     * @return String from resources
     * @see #provideTimerDescriptionTextView()
     */
    String provideLeftString();

    /**
     * provide description of the passed time value for the TextView
     *
     * @return String from resources
     * @see #provideTimerDescriptionTextView()
     */
    String providePassedString();

    /**
     * provide label for the bottom button
     *
     * @return link to the string resources
     */
    int provideActionButtonString();

    /**
     * provide label for the action that is expired
     *
     * @return description for the expired order
     */
    String provideExpireString();

    /**
     * provide label for the action in the future
     *
     * @return description for the future order
     */
    String provideFutureString();
}
