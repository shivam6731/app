package com.foodpanda.urbanninja.ui.fragments;

import android.widget.TextView;

import com.foodpanda.urbanninja.utils.DateUtil;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public abstract class BaseTimerFragment extends BaseFragment {
    private static final int UPDATE_INTERVAL = 1000;
    private Timer timer;

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
            }
        });
    }

    private String setTimerValue() {
        String result;
        Date now = new Date();
        Date to = provideScheduleDate() == null ? new Date() : provideScheduleDate();
        if (now.getTime() < to.getTime()) {
            result = DateUtil.timerFormat(to.getTime() - now.getTime());
            provideTimerDescriptionTextView().setText(provideLeftString());
        } else {
            result = DateUtil.timerFormat(now.getTime() - to.getTime());
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

