package com.foodpanda.urbanninja.ui.util;

import android.widget.TextView;

import com.foodpanda.urbanninja.ui.activity.BaseActivity;
import com.foodpanda.urbanninja.ui.fragments.BaseFragment;
import com.foodpanda.urbanninja.ui.interfaces.NestedFragmentCallback;
import com.foodpanda.urbanninja.ui.interfaces.TimerDataProvider;
import com.foodpanda.urbanninja.utils.DateUtil;

import org.joda.time.DateTime;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Writes remaining time and description to the TextView
 * in all screens with Timer inside
 */
public class TimerHelper {
    private static final int UPDATE_INTERVAL_IN_MILLISECONDS = 1000;
    private static final int ENABLE_TIME_OUT_IN_MILLISECONDS = 30 * 60 * 1000;

    private NestedFragmentCallback nestedFragmentCallback;
    private BaseFragment baseFragment;
    private TimerDataProvider timerDataProvider;
    private BaseActivity baseActivity;

    private Timer timer;

    public TimerHelper(
        BaseActivity baseActivity,
        BaseFragment baseFragment,
        TimerDataProvider timerDataProvider) {
        this.baseActivity = baseActivity;
        this.baseFragment = baseFragment;
        this.timerDataProvider = timerDataProvider;
    }

    public TimerHelper(
        BaseActivity baseActivity,
        BaseFragment baseFragment,
        TimerDataProvider timerDataProvider,
        NestedFragmentCallback nestedFragmentCallback) {
        this(baseActivity, baseFragment, timerDataProvider);
        this.nestedFragmentCallback = nestedFragmentCallback;
    }

    /**
     * starts timer and updates the value and description TextView
     * with {@value #UPDATE_INTERVAL_IN_MILLISECONDS} interval
     */
    public void setTimer() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                drawTime();
            }
        }, 0, UPDATE_INTERVAL_IN_MILLISECONDS);
    }

    /**
     * stop and delete timer
     */
    public void stopTimer() {
        if (timer != null) {
            timer.cancel();
        }
        timer = null;
    }

    /**
     * in UI thread draw the timer and description value
     * for TextView and disable it if it's necessary
     */
    private void drawTime() {
        baseActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (baseFragment.isAdded()) {
                    timerDataProvider.provideTimerTextView().setText(setTimerValue());
                    if (nestedFragmentCallback != null) {
                        enableActionButton();
                    }
                }
            }
        });
    }

    private void enableActionButton() {
        nestedFragmentCallback.enableActionButton(
            isChangeAllowed(),
            timerDataProvider.provideActionButtonString());
    }

    private boolean isChangeAllowed() {
        DateTime now = new DateTime();
        DateTime startDate = timerDataProvider.provideScheduleDate() == null ? new DateTime() : timerDataProvider.provideScheduleDate();
        DateTime endDate = timerDataProvider.provideScheduleEndDate() == null ? new DateTime() : timerDataProvider.provideScheduleEndDate();

        return now.getMillis() > startDate.getMillis() - ENABLE_TIME_OUT_IN_MILLISECONDS && endDate.getMillis() > now.getMillis();
    }

    /**
     * Count timer value between orderTime and now
     * and formatted it with DateUtils.
     * Moreover add description value to the TextView
     *
     * @return formatted timer value
     */
    String setTimerValue() {
        DateTime now = new DateTime();
        DateTime startDate = timerDataProvider.provideScheduleDate() == null ? new DateTime() : timerDataProvider.provideScheduleDate();
        DateTime endDate = timerDataProvider.provideScheduleEndDate() == null ? new DateTime() : timerDataProvider.provideScheduleEndDate();
        TextView textViewDescription = timerDataProvider.provideTimerDescriptionTextView();

        long date = Math.abs(startDate.getMillis() - now.getMillis());

        if (now.getMillis() < startDate.getMillis()) {
            textViewDescription.setText(getTimeLeft(date));

            return DateUtil.formatTimeHoursMinutesSeconds(date);
        } else {
            textViewDescription.setText(getTimePassed(endDate.getMillis(), now.getMillis()));
            if (endDate.getMillis() > now.getMillis()) {
                return DateUtil.formatTimeHoursMinutesSeconds(date);
            } else {
                return "";
            }
        }
    }

    private String getTimeLeft(long date) {
        if (date > DateUtil.ONE_DAY) {
            return timerDataProvider.provideFutureString();
        } else {
            return timerDataProvider.provideLeftString();
        }
    }

    private String getTimePassed(long endDate, long nowDate) {
        if (endDate > nowDate) {
            return timerDataProvider.providePassedString();
        } else {
            return timerDataProvider.provideExpireString();
        }
    }

    /**
     * get Timer needs only for tests
     *
     * @return timer
     */
    Timer getTimer() {
        return timer;
    }
}
