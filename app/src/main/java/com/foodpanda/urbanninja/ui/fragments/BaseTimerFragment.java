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
                provideTimerTextView().setText(DateUtil.timerFormat(new Date()));
            }
        });
    }

    protected abstract TextView provideTimerTextView();
}
