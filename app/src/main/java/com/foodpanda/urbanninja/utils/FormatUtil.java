package com.foodpanda.urbanninja.utils;

import android.text.TextUtils;

import com.foodpanda.urbanninja.model.Country;

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
}
