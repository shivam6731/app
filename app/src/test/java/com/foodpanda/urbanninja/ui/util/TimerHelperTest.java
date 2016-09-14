package com.foodpanda.urbanninja.ui.util;

import android.app.Application;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.widget.TextView;

import com.foodpanda.urbanninja.BuildConfig;
import com.foodpanda.urbanninja.R;
import com.foodpanda.urbanninja.ui.activity.MainActivity;
import com.foodpanda.urbanninja.ui.fragments.BaseFragment;
import com.foodpanda.urbanninja.ui.interfaces.TimerDataProvider;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.robolectric.Robolectric.buildActivity;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, packageName = "com.foodpanda.urbanninja")
public class TimerHelperTest {

    private TextView contentTextView;

    private TimerHelper timerHelper;

    @Mock
    private BaseFragment baseFragment;

    private MainActivity mainActivity;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Application app = RuntimeEnvironment.application;
        app.onCreate();
        mainActivity = buildActivity(MainActivity.class).get();
        DateTimeUtils.setCurrentMillisFixed(DateTime.now().getMillis());

        contentTextView = new TextView(app);
    }

    @Test
    public void testNotNull() {
        timerHelper = new TimerHelper(mainActivity,
            baseFragment,
            setTimerCallback(DateTime.now().plusMinutes(10), DateTime.now().minusMillis(10)));

        assertNotNull(timerHelper);
    }

    @Test
    public void testSetTimer() {
        timerHelper = new TimerHelper(mainActivity,
            baseFragment,
            setTimerCallback(DateTime.now(), DateTime.now()));

        timerHelper.setTimer();
        assertNotNull(timerHelper.getTimer());
    }

    @Test
    public void testStopTimer() {
        timerHelper = new TimerHelper(mainActivity,
            baseFragment,
            setTimerCallback(DateTime.now(), DateTime.now()));

        timerHelper.stopTimer();
        assertNull(timerHelper.getTimer());
    }

    @Test
    public void testSetTimerValue10Minutes() {
        timerHelper = new TimerHelper(mainActivity,
            baseFragment,
            setTimerCallback(DateTime.now().plusMinutes(10), DateTime.now().minusMillis(10)));

        assertEquals(timerHelper.setTimerValue(), "10:00");
        ColorDrawable buttonColor = (ColorDrawable) contentTextView.getBackground();
        int colorId = buttonColor.getColor();
        assertTrue(colorId == ContextCompat.getColor(mainActivity, R.color.timer_in_time_background));
        assertTrue(contentTextView.getCurrentTextColor() == ContextCompat.getColor(mainActivity, R.color.timer_in_time_text));
    }

    @Test
    public void testSetTimerValue1Hour10Minutes() {
        timerHelper = new TimerHelper(mainActivity,
            baseFragment,
            setTimerCallback(DateTime.now().plusMinutes(10).plusHours(1), DateTime.now().minusMillis(10)));

        assertEquals(timerHelper.setTimerValue(), "01:10:00");
        ColorDrawable buttonColor = (ColorDrawable) contentTextView.getBackground();
        int colorId = buttonColor.getColor();
        assertTrue(colorId == ContextCompat.getColor(mainActivity, R.color.timer_in_time_background));
        assertTrue(contentTextView.getCurrentTextColor() == ContextCompat.getColor(mainActivity, R.color.timer_in_time_text));
    }

    @Test
    public void testSetTimerValue1MinuteLate() {
        timerHelper = new TimerHelper(mainActivity,
            baseFragment,
            setTimerCallback(DateTime.now().minusMinutes(1), DateTime.now().plusMinutes(10)));

        assertEquals(timerHelper.setTimerValue(), "01:00");
        ColorDrawable buttonColor = (ColorDrawable) contentTextView.getBackground();
        int colorId = buttonColor.getColor();
        assertTrue(colorId == ContextCompat.getColor(mainActivity, R.color.timer_late_background));
        assertTrue(contentTextView.getCurrentTextColor() == ContextCompat.getColor(mainActivity, R.color.warning_text_color));
    }

    @Test
    public void testSetTimerValue1Hour1MinuteLate() {
        timerHelper = new TimerHelper(mainActivity,
            baseFragment,
            setTimerCallback(DateTime.now().minusMinutes(1).minusHours(1), DateTime.now().plusMinutes(10)));

        assertEquals(timerHelper.setTimerValue(), "01:01:00");
        ColorDrawable buttonColor = (ColorDrawable) contentTextView.getBackground();
        int colorId = buttonColor.getColor();
        assertTrue(colorId == ContextCompat.getColor(mainActivity, R.color.timer_late_background));
        assertTrue(contentTextView.getCurrentTextColor() == ContextCompat.getColor(mainActivity, R.color.warning_text_color));
    }

    @Test
    public void testSetTimerValueExpired() {
        timerHelper = new TimerHelper(mainActivity,
            baseFragment,
            setTimerCallback(DateTime.now().minusMinutes(2), DateTime.now().minusMinutes(1)));

        assertEquals(timerHelper.setTimerValue(), "");
        ColorDrawable buttonColor = (ColorDrawable) contentTextView.getBackground();
        int colorId = buttonColor.getColor();
        assertTrue(colorId == ContextCompat.getColor(mainActivity, R.color.timer_late_background));
        assertTrue(contentTextView.getCurrentTextColor() == ContextCompat.getColor(mainActivity, R.color.warning_text_color));
    }

    @Test
    public void testSetTimerValueFuture() {
        timerHelper = new TimerHelper(mainActivity,
            baseFragment,
            setTimerCallback(
                DateTime.now().plusDays(1).plusHours(2),
                DateTime.now().plusDays(1).plusHours(2).plusMinutes(2)));

        assertEquals(timerHelper.setTimerValue(), "");
        ColorDrawable buttonColor = (ColorDrawable) contentTextView.getBackground();
        int colorId = buttonColor.getColor();
        assertTrue(colorId == ContextCompat.getColor(mainActivity, R.color.timer_in_time_background));
        assertTrue(contentTextView.getCurrentTextColor() == ContextCompat.getColor(mainActivity, R.color.timer_in_time_text));
    }

    private TimerDataProvider setTimerCallback(final DateTime start, final DateTime end) {
        return new TimerDataProvider() {
            @Override
            public TextView provideTimerTextView() {
                return contentTextView;
            }

            @Override
            public DateTime provideScheduleDate() {
                return start;
            }

            @Override
            public DateTime provideScheduleEndDate() {
                return end;
            }


            @Override
            public int provideActionButtonString() {
                return 0;
            }
        };
    }
}
