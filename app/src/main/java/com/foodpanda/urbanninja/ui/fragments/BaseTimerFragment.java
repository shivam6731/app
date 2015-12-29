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
                provideTimerTextView().setText(setTimerValue());
                enableActionButton();
            }
        });
    }

    private void enableActionButton() {
        DateTime now = new DateTime();

        DateTime startDate = provideScheduleDate() == null ? new DateTime() : provideScheduleDate();

        mainActivityCallback.enableActionButton(now.getMillis() > startDate.getMillis() - ENABLE_TIME_OUT);
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

    protected abstract TextView provideTimerTextView();

    protected abstract TextView provideTimerDescriptionTextView();

    protected abstract DateTime provideScheduleDate();

    protected abstract String provideLeftString();

    protected abstract String providePassedString();
}

