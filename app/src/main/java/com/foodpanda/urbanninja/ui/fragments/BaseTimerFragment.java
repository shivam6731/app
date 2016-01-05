package com.foodpanda.urbanninja.ui.fragments;

import android.content.Context;
import android.widget.TextView;

import com.foodpanda.urbanninja.R;
import com.foodpanda.urbanninja.ui.interfaces.MainActivityCallback;
import com.foodpanda.urbanninja.utils.DateUtil;

import org.joda.time.DateTime;

import java.util.Timer;
import java.util.TimerTask;

public abstract class BaseTimerFragment extends BaseFragment {
    protected MainActivityCallback mainActivityCallback;

    private static final int UPDATE_INTERVAL = 1000;
    private static final int ENABLE_TIME_OUT = 30 * 60 * 1000;
    private Timer timer;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivityCallback = (MainActivityCallback) context;
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
        }, 0, UPDATE_INTERVAL);
    }

    private void drawTime() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (BaseTimerFragment.this.isAdded()) {
                    provideTimerTextView().setText(setTimerValue());
                    enableActionButton();
                }
            }
        });
    }

    private void enableActionButton() {
        DateTime now = new DateTime();

        DateTime startDate = provideScheduleDate() == null ? new DateTime() : provideScheduleDate();

        mainActivityCallback.enableActionButton(
            now.getMillis() > startDate.getMillis() - ENABLE_TIME_OUT,
            provideActionButtonString());
    }

    private String setTimerValue() {
        String result;

        DateTime now = new DateTime();

        DateTime startDate = provideScheduleDate() == null ? new DateTime() : provideScheduleDate();

        long date = Math.abs(startDate.getMillis() - now.getMillis());
        result = DateUtil.timeFormat(date);
        if (now.getMillis() < startDate.getMillis()) {
            if (date > DateUtil.ONE_DAY) {
                provideTimerDescriptionTextView().setText(getResources().getString(R.string.action_ready_no_shift));
            } else {
                provideTimerDescriptionTextView().setText(provideLeftString());
            }
        } else {
            if (date > DateUtil.ONE_DAY) {
                provideTimerDescriptionTextView().setText(getResources().getString(R.string.action_ready_shift_expired));
            } else {
                provideTimerDescriptionTextView().setText(providePassedString());
            }
        }

        return result;
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
}

