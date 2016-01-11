package com.foodpanda.urbanninja.utils;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;

public class DateUtil {
    public static final int ONE_HOUR = 1000 * 60 * 60;
    public static final int ONE_DAY = 24 * ONE_HOUR;

    private static SimpleDateFormat timerFormat = new SimpleDateFormat("mm:ss");
    private static SimpleDateFormat timerFormatWithHour = new SimpleDateFormat("HH:mm:ss");

    private static SimpleDateFormat timerFormatWeekDayDateMonth = new SimpleDateFormat("EEEE - d MMMM");
    private static SimpleDateFormat timerFormatMinutesHour = new SimpleDateFormat("HH:mm");

    private static String timerFormatMinute(long date) {
        return timerFormat.format(date);
    }

    private static String timerFormatWithHour(long date) {
        return timerFormatWithHour.format(date);
    }

    public static String timeFormatMinutesHour(long date) {
        if (date > ONE_DAY) {
            return "";
        }
        if (date > ONE_HOUR) {
            return timerFormatWithHour(date);
        } else {
            return timerFormatMinute(date);
        }
    }

    public static String timeFormatMinutesHour(DateTime dateTime) {
        return timerFormat.format(dateTime.toDate());
    }

    public static String timerFormatWeekDayDateMonth(DateTime dateTime) {
        return timerFormatMinutesHour.format(dateTime.toDate());
    }
}
