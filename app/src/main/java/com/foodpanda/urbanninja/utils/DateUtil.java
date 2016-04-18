package com.foodpanda.urbanninja.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.foodpanda.urbanninja.R;
import com.foodpanda.urbanninja.model.TimeWindow;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

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

    //Schedule duration formatter
    private static SimpleDateFormat timerFormatHours = new SimpleDateFormat("H'h'", Locale.getDefault());
    private static SimpleDateFormat timerFormatMinutes = new SimpleDateFormat("m'm'", Locale.getDefault());

    /**
     * We need to set UTC time zone only for cases when we have to show only
     * difference between two long values with ignoring time zone, such as timer
     */
    static {
        timerFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        timerFormatWithHour.setTimeZone(TimeZone.getTimeZone("UTC"));

        timerFormatHours.setTimeZone(TimeZone.getTimeZone("UTC"));
        timerFormatHours.setTimeZone(TimeZone.getTimeZone("UTC"));
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

    /**
     * In schedule screen we need specific format for today and tomorrow schedule
     *
     * @param scheduleStartDateTime time of begging of the schedule
     * @param context               android context to get string resource
     * @return formatted date for the start of schedule
     */
    public static String formatScheduleTimeWeekDayDateMonth(
        DateTime scheduleStartDateTime,
        Context context) {
        LocalDate midnightToday = DateTime.now().withTimeAtStartOfDay().toLocalDate();
        LocalDate localDate = scheduleStartDateTime.withTimeAtStartOfDay().toLocalDate();

        if (midnightToday.isEqual(localDate)) {
            return context.getResources().getString(R.string.schedule_list_today);
        } else if (midnightToday.plusDays(1).isEqual(localDate)) {
            return context.getResources().getString(R.string.schedule_list_tomorrow);
        }

        return timerFormatWeekDayDateMonth.format(scheduleStartDateTime.toDate());
    }

    public static String formatTimeWeekDayDateMonthYear(DateTime dateTime) {
        return timerFormatWeekDayDateMonthYear.format(dateTime.toDate());
    }

    /**
     * set formatter value for schedule circle section with duration of working day
     * if there is no minutes this field should be shown
     *
     * @param txtHours   textView for hours value
     * @param txtMinutes textView for minutes value
     * @param timeWindow rider schedule time window
     */
    public static void setHoursMinutes(TextView txtHours, TextView txtMinutes, TimeWindow timeWindow) {
        DateTime workingDayDuration = new DateTime(timeWindow.getEndAt().getMillis() - timeWindow.getStartAt().getMillis());
        if (workingDayDuration.getMinuteOfHour() == 0) {
            txtMinutes.setVisibility(View.GONE);
            txtHours.setGravity(Gravity.CENTER);
        } else {
            txtMinutes.setVisibility(View.VISIBLE);
            txtMinutes.setText(formatDurationMinutes(workingDayDuration));
            txtHours.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
        }
        txtHours.setText(formatDurationHours(workingDayDuration));
    }

    private static String formatDurationHours(DateTime dateTime) {
        return timerFormatHours.format(dateTime.toDate());
    }

    private static String formatDurationMinutes(DateTime dateTime) {
        return timerFormatMinutes.format(dateTime.toDate());
    }

}
