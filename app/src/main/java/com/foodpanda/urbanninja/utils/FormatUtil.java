package com.foodpanda.urbanninja.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.foodpanda.urbanninja.R;
import com.foodpanda.urbanninja.model.Country;

import org.joda.time.DateTime;

import java.text.NumberFormat;
import java.util.Locale;

public class FormatUtil {

    private FormatUtil() {
        // Static helper class
    }

    /**
     * Convert string to number and format as currency of passed country
     *
     * @return empty string if cannot convert string to number
     * otherwise it would be value with currency symbol
     */
    public static String getValueWithCurrencySymbol(Country country, String value) {
        try {
            return getValueWithCurrencySymbolFromNumber(country, Double.parseDouble(value));
        } catch (NumberFormatException | NullPointerException e) {
            return "";
        }
    }

    /**
     * Format number as currency of passed country
     */
    public static String getValueWithCurrencySymbolFromNumber(Country country, Number numericValue) {
        if (TextUtils.isEmpty(country.getCode())) {
            return "";
        }

        Locale locale = new Locale("en", country.getCode());
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(locale);

        return numberFormat.format(numericValue);
    }

    /**
     * For preOrder type of routeStopActivity we receive
     * time in what order should be delivered in with time zone offset
     * and here we format it to show only hours and minutes in riders current timezone
     *
     * @param value   UTC date as a string for preOrder time
     * @param context to get string android resources
     * @return formatter HH:mm string with time for order to be arrived
     */
    public static String getPreOrderValue(String value, Context context) {
        if (TextUtils.isEmpty(value)) {
            return context.getResources().getString(R.string.route_action_pre_order_empty_title);
        }
        try {
            String formattedTime = DateUtil.formatTimeHoursMinutes(DateTime.parse(value));

            return context.getResources().getString(R.string.route_action_pre_order_title, formattedTime);
        } catch (IllegalArgumentException e) {
            Log.e(FormatUtil.class.getSimpleName(), e.getMessage());

            return context.getResources().getString(R.string.route_action_pre_order_empty_title);
        }
    }

}
