package com.foodpanda.urbanninja.utils;

import android.app.Application;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.foodpanda.urbanninja.BuildConfig;
import com.foodpanda.urbanninja.model.TimeWindow;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, packageName = "com.foodpanda.urbanninja")
public class DateUtilsTest {

    @Before
    public void setUp() {
        DateTimeUtils.setCurrentMillisFixed(DateTime.now().getMillis());
    }

    @Test
    public void testFormatTimeMinutes() throws Exception {
        assertEquals("01:02:00", DateUtil.formatTimeHoursMinutesSeconds(
            DateTime.now().plusHours(1).plusMinutes(2).getMillis() - DateTime.now().getMillis()));

        assertEquals("58:00", DateUtil.formatTimeHoursMinutesSeconds(
            DateTime.now().plusHours(1).minusMinutes(2).getMillis() - DateTime.now().getMillis()));
    }

    @Test
    public void testFormatTimeHoursMinutes() throws Exception {
        assertEquals("00:00", DateUtil.formatTimeHoursMinutes(
            DateTime.now().withTime(0, 0, 0, 0)));

        assertEquals("01:02", DateUtil.formatTimeHoursMinutes(
            DateTime.now().withTime(0, 0, 0, 0).plusHours(1).plusMinutes(2)));

        assertEquals("02:02", DateUtil.formatTimeHoursMinutes(
            DateTime.now().withTime(0, 0, 0, 0).plusHours(2).plusMinutes(2)));

        assertEquals("13:02", DateUtil.formatTimeHoursMinutes(
            DateTime.now().withTime(0, 0, 0, 0).plusHours(13).plusMinutes(2)));

        assertEquals("13:02", DateUtil.formatTimeHoursMinutes(
            DateTime.now().withTime(0, 0, 0, 0).plusHours(-11).plusMinutes(2)));
    }

    @Test
    public void testFormatScheduleWeekDayDateMonth() throws Exception {
        Application app = RuntimeEnvironment.application;
        app.onCreate();

        assertEquals("Tuesday - 29 March", DateUtil.formatScheduleTimeWeekDayDateMonth(
            DateTime.now(DateTimeZone.UTC).withDate(2016, 3, 29), app));

        assertEquals("Today", DateUtil.formatScheduleTimeWeekDayDateMonth(
            DateTime.now(DateTimeZone.UTC), app));

        assertEquals("Tomorrow", DateUtil.formatScheduleTimeWeekDayDateMonth(
            DateTime.now(DateTimeZone.UTC).plusDays(1), app));
    }

    @Test
    public void testFormatWeekDayDateMonthYear() throws Exception {
        assertEquals("Tuesday - 29/03/2016", DateUtil.formatTimeWeekDayDateMonthYear(
            DateTime.now(DateTimeZone.UTC).withDate(2016, 3, 29)));
    }

    @Test
    public void testFormatDayMonthYear() throws Exception {
        assertEquals("6 April 2016", DateUtil.formatTimeDayMonthYear("2016-04-06"));
    }

    @Test
    public void testFormatTimeZone() throws Exception {
        assertEquals("+0000", DateUtil.formatTimeZone(new DateTime(DateTimeZone.UTC).toDate()));
    }

    @Test
    public void testSetHoursMinutesOnlyHours() throws Exception {
        Application app = RuntimeEnvironment.application;
        app.onCreate();

        TextView txtHours = new TextView(app);
        TextView txtMinutes = new TextView(app);

        TimeWindow timeWindow = new TimeWindow(DateTime.now().withTime(0, 0, 0, 0),
            DateTime.now().withTime(0, 0, 0, 0).plusHours(8));

        DateUtil.setHoursMinutes(txtHours, txtMinutes, timeWindow);

        assertTrue(txtHours.getVisibility() == View.VISIBLE);
        assertEquals("8h", txtHours.getText().toString());
        assertEquals(Gravity.CENTER, txtHours.getGravity());
        assertTrue(txtMinutes.getVisibility() == View.GONE);
    }

    @Test
    public void testSetHoursMinutesMixedTimeWindow() throws Exception {
        Application app = RuntimeEnvironment.application;
        app.onCreate();

        TextView txtHours = new TextView(app);
        TextView txtMinutes = new TextView(app);

        TimeWindow timeWindow = new TimeWindow(DateTime.now().withTime(0, 0, 0, 0),
            DateTime.now().withTime(0, 0, 0, 0).plusHours(5).plusMinutes(1).plusSeconds(45));

        DateUtil.setHoursMinutes(txtHours, txtMinutes, timeWindow);

        assertTrue(txtHours.getVisibility() == View.VISIBLE);
        assertEquals("5h", txtHours.getText().toString());
        assertTrue(txtMinutes.getVisibility() == View.VISIBLE);
        assertEquals("1m", txtMinutes.getText().toString());
        assertEquals(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, txtHours.getGravity());
    }

}
