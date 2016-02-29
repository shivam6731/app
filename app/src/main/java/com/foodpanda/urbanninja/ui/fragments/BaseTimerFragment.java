package com.foodpanda.urbanninja.ui.fragments;

import android.content.Context;
import android.widget.TextView;

import com.foodpanda.urbanninja.ui.interfaces.NestedFragmentCallback;
import com.foodpanda.urbanninja.utils.DateUtil;

import org.joda.time.DateTime;

import java.util.Timer;
import java.util.TimerTask;

public abstract class BaseTimerFragment extends BaseFragment {
    protected NestedFragmentCallback nestedFragmentCallback;
    protected boolean needToModifyActionButton;

    private static final int UPDATE_INTERVAL_IN_MILLISECONDS = 1000;
    private static final int ENABLE_TIME_OUT_IN_MILLISECONDS = 30 * 60 * 1000;
    private Timer timer;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        nestedFragmentCallback = (NestedFragmentCallback) getParentFragment();
    }

    @Override
    public void onStart() {
        super.onStart();
        setTimer();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (timer != null) {
            timer.cancel();
        }
        timer = null;
    }

    private void setTimer() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                drawTime();
            }
        }, 0, UPDATE_INTERVAL_IN_MILLISECONDS);
    }

    private void drawTime() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (BaseTimerFragment.this.isAdded()) {
                    provideTimerTextView().setText(setTimerValue());
                    if (needToModifyActionButton) {
                        enableActionButton();
                    }
                }
            }
        });
    }

    private void enableActionButton() {
        nestedFragmentCallback.enableActionButton(
            isChangeAllowed(),
            provideActionButtonString());
    }

    private boolean isChangeAllowed() {
        DateTime now = new DateTime();
        DateTime startDate = provideScheduleDate() == null ? new DateTime() : provideScheduleDate();
        DateTime endDate = provideScheduleEndDate() == null ? new DateTime() : provideScheduleEndDate();

        return now.getMillis() > startDate.getMillis() - ENABLE_TIME_OUT_IN_MILLISECONDS && endDate.getMillis() > now.getMillis();
    }


    private String setTimerValue() {
        DateTime now = new DateTime();
        DateTime startDate = provideScheduleDate() == null ? new DateTime() : provideScheduleDate();
        DateTime endDate = provideScheduleEndDate() == null ? new DateTime() : provideScheduleEndDate();
        TextView textViewDescription = provideTimerDescriptionTextView();

        long date = Math.abs(startDate.getMillis() - now.getMillis());

        if (now.getMillis() < startDate.getMillis()) {
            textViewDescription.setText(getTimeLeft(date));

            return DateUtil.formatTimeMinutes(date);
        } else {
            textViewDescription.setText(getTimePassed(endDate.getMillis(), now.getMillis()));
            if (endDate.getMillis() > now.getMillis()) {

                return DateUtil.formatTimeMinutes(date);
            } else {

                return "";
            }
        }
    }

    private String getTimeLeft(long date) {
        if (date > DateUtil.ONE_DAY) {

            return getResources().getString(provideFutureString());
        } else {

            return provideLeftString();
        }
    }

    private String getTimePassed(long endDate, long nowDate) {
        if (endDate > nowDate) {

            return providePassedString();
        } else {

            return getResources().getString(provideExpireString());
        }
    }

    /**
     * provide TextView for the clock witch we would change every second by timer
     *
     * @return TextView with big text size (according to our design)
     */

    protected abstract TextView provideTimerTextView();

    /**
     * provide TextView the for the description values where
     * we would see do we have some extra time or we are late
     *
     * @return TextView with big text size (according to our design)
     * @see #provideLeftString()
     * @see #providePassedString()
     */
    protected abstract TextView provideTimerDescriptionTextView();

    /**
     * provide start time for any action with timer
     *
     * @return DateTime from the server side
     */
    protected abstract DateTime provideScheduleDate();

    /**
     * provide end time for any action with timer
     *
     * @return DateTime from the server side
     */
    protected abstract DateTime provideScheduleEndDate();

    /**
     * provide description of the left time value for the TextView
     *
     * @return String from resources
     * @see #provideTimerDescriptionTextView()
     */
    protected abstract String provideLeftString();

    /**
     * provide description of the passed time value for the TextView
     *
     * @return String from resources
     * @see #provideTimerDescriptionTextView()
     */
    protected abstract String providePassedString();

    /**
     * provide label for the bottom button
     *
     * @return link to the string resources
     */
    protected abstract int provideActionButtonString();

    /**
     * provide label for the action that is expired
     *
     * @return link to the string resources
     */
    protected abstract int provideExpireString();

    /**
     * provide label for the action in the future
     *
     * @return link to the string resources
     */
    protected abstract int provideFutureString();
}

