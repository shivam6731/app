package com.foodpanda.urbanninja.ui.fragments;

import android.content.Context;
import android.widget.TextView;

import com.foodpanda.urbanninja.ui.interfaces.MainActivityCallback;
import com.foodpanda.urbanninja.utils.DateUtil;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
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
        Calendar now = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

        Date startDate = provideScheduleDate() == null ? new Date() : provideScheduleDate();
        Calendar start = Calendar.getInstance();
        start.setTime(startDate);

        mainActivityCallback.enableActionButton(now.getTimeInMillis() > start.getTimeInMillis() - ENABLE_TIME_OUT);
    }

    private String setTimerValue() {
        String result;
        Calendar now = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

        Date startDate = provideScheduleDate() == null ? new Date() : provideScheduleDate();
        Calendar start = Calendar.getInstance();
        start.setTime(startDate);

        if (now.getTimeInMillis() < start.getTimeInMillis()) {
            result = DateUtil.timerFormat(start.getTimeInMillis() - now.getTimeInMillis());
            provideTimerDescriptionTextView().setText(provideLeftString());
        } else {
            result = DateUtil.timerFormat(now.getTimeInMillis() - start.getTimeInMillis());
            provideTimerDescriptionTextView().setText(providePassedString());
        }
        return result;
    }

    protected abstract TextView provideTimerTextView();

    protected abstract TextView provideTimerDescriptionTextView();

    protected abstract Date provideScheduleDate();

    protected abstract String provideLeftString();

    protected abstract String providePassedString();
}

