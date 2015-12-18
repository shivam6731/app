package com.foodpanda.urbanninja.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    private static SimpleDateFormat timerFormat = new SimpleDateFormat("mm:ss");

    public static String timerFormat(long date) {
        return timerFormat.format(date);
    }
}
