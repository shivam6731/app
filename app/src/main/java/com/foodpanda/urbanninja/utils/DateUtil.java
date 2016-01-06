package com.foodpanda.urbanninja.utils;

import java.text.SimpleDateFormat;

public class DateUtil {
    public static final int ONE_HOUR = 1000 * 60 * 60;
    public static final int ONE_DAY = 24 * ONE_HOUR;

    private static SimpleDateFormat timerFormat = new SimpleDateFormat("mm:ss");
    private static SimpleDateFormat timerFormatWithHours = new SimpleDateFormat("HH:mm:ss");

    private static String timerFormatMinutes(long date) {
        return timerFormat.format(date);
    }

    private static String timerFormatWithHours(long date) {
        return timerFormatWithHours.format(date);
    }

    public static String timeFormat(long date) {
        if (date > ONE_DAY) {
            return "";
        }
        if (date > ONE_HOUR) {
            return timerFormatWithHours(date);
        } else {
            return timerFormatMinutes(date);
        }
    }
}
