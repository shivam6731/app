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
