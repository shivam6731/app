package com.foodpanda.urbanninja.utils;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

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
    public void testFormatWeekDayDateMonth() throws Exception {
        assertEquals("Tuesday - 29 March", DateUtil.formatTimeWeekDayDateMonth(
            DateTime.now(DateTimeZone.UTC).withDate(2016, 3, 29)));
    }

    @Test
    public void testFormatWeekDayDateMonthYear() throws Exception {
        assertEquals("Tuesday - 29/03/2016", DateUtil.formatTimeWeekDayDateMonthYear(
            DateTime.now(DateTimeZone.UTC).withDate(2016, 3, 29)));
    }
}
