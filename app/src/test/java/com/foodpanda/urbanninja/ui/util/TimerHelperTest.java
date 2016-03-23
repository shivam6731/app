package com.foodpanda.urbanninja.ui.util;

import android.app.Application;
import android.widget.TextView;

import com.foodpanda.urbanninja.BuildConfig;
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

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, packageName = "com.foodpanda.urbanninja")
public class TimerHelperTest {
    private Application app;

    private TextView textViewDescription;

    private TimerHelper timerHelper;

    @Mock
    private BaseFragment baseFragment;

    @Mock
    private MainActivity mainActivity;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        app = RuntimeEnvironment.application;
        app.onCreate();
        DateTimeUtils.setCurrentMillisFixed(DateTime.now().getMillis());

        textViewDescription = new TextView(app);
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
        assertEquals(textViewDescription.getText(), "Left");
    }

    @Test
    public void testSetTimerValue1Hour10Minutes() {
        timerHelper = new TimerHelper(mainActivity,
            baseFragment,
            setTimerCallback(DateTime.now().plusMinutes(10).plusHours(1), DateTime.now().minusMillis(10)));

        assertEquals(timerHelper.setTimerValue(), "01:10:00");
        assertEquals(textViewDescription.getText(), "Left");
    }

    @Test
    public void testSetTimerValue1MinuteLate() {
        timerHelper = new TimerHelper(mainActivity,
            baseFragment,
            setTimerCallback(DateTime.now().minusMinutes(1), DateTime.now().plusMinutes(10)));

        assertEquals(timerHelper.setTimerValue(), "01:00");
        assertEquals(textViewDescription.getText(), "Passed");
    }

    @Test
    public void testSetTimerValue1Hour1MinuteLate() {
        timerHelper = new TimerHelper(mainActivity,
            baseFragment,
            setTimerCallback(DateTime.now().minusMinutes(1).minusHours(1), DateTime.now().plusMinutes(10)));

        assertEquals(timerHelper.setTimerValue(), "01:01:00");
        assertEquals(textViewDescription.getText(), "Passed");
    }

    @Test
    public void testSetTimerValueExpired() {
        timerHelper = new TimerHelper(mainActivity,
            baseFragment,
            setTimerCallback(DateTime.now().minusMinutes(2), DateTime.now().minusMinutes(1)));

        assertEquals(timerHelper.setTimerValue(), "");
        assertEquals(textViewDescription.getText(), "Expired");
    }

    @Test
    public void testSetTimerValueFuture() {
        timerHelper = new TimerHelper(mainActivity,
            baseFragment,
            setTimerCallback(
                DateTime.now().plusDays(1).plusHours(2),
                DateTime.now().plusDays(1).plusHours(2).plusMinutes(2)));

        assertEquals(timerHelper.setTimerValue(), "");
        assertEquals(textViewDescription.getText(), "Future");
    }

    private TimerDataProvider setTimerCallback(final DateTime start, final DateTime end) {
        return new TimerDataProvider() {
            @Override
            public TextView provideTimerTextView() {
                return new TextView(app);
            }

            @Override
            public TextView provideTimerDescriptionTextView() {
                return textViewDescription;
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
            public String provideLeftString() {
                return "Left";
            }

            @Override
            public String providePassedString() {
                return "Passed";
            }

            @Override
            public int provideActionButtonString() {
                return 0;
            }

            @Override
            public String provideExpireString() {
                return "Expired";
            }

            @Override
            public String provideFutureString() {
                return "Future";
            }
        };
    }
}
