package com.foodpanda.urbanninja.utils;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtil {
    public static final int ONE_HOUR = 1000 * 60 * 60;
    public static final int ONE_DAY = 24 * ONE_HOUR;

    //Timer formatter
    private static SimpleDateFormat timerFormat = new SimpleDateFormat("mm:ss", Locale.getDefault());
    private static SimpleDateFormat timerFormatWithHour = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

    //Date formatter
    private static SimpleDateFormat timerFormatWeekDayDateMonth = new SimpleDateFormat("EEEE - d MMMM", Locale.getDefault());
    private static SimpleDateFormat timerFormatWeekDayDateMonthYear = new SimpleDateFormat("EEEE - dd/MM/yyyy", Locale.getDefault());

    //Hour formatter for start and end of schedule 
    private static SimpleDateFormat timerFormatHoursMinutes = new SimpleDateFormat("HH:mm", Locale.getDefault());

    /**
     * We need to set UTC time zone only for cases when we have to show only
     * difference between two long values with ignoring time zone, such as timer
     */
    static {
        timerFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        timerFormatWithHour.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    private static String formatTimeMinute(long date) {
        return timerFormat.format(date);
    }

    private static String formatTimeWithHour(long date) {
        return timerFormatWithHour.format(date);
    }

    public static String formatTimeHoursMinutesSeconds(long date) {
        if (date > ONE_DAY) {
            return "";
        }
        if (date > ONE_HOUR) {
            return formatTimeWithHour(date);
        } else {
            return formatTimeMinute(date);
        }
    }

    public static String formatTimeHoursMinutes(DateTime dateTime) {
        return timerFormatHoursMinutes.format(dateTime.toDate());
    }

    public static String formatTimeWeekDayDateMonth(DateTime dateTime) {
        return timerFormatWeekDayDateMonth.format(dateTime.toDate());
    }

    public static String formatTimeWeekDayDateMonthYear(DateTime dateTime) {
        return timerFormatWeekDayDateMonthYear.format(dateTime.toDate());
    }

}
