package com.foodpanda.urbanninja.utils;

/**
 * Android has {@link android.text.TextUtils}, but those are annoying to mock in pure unit tests
 *
 * The implementation of common String checks is easy, so let's copy some for easy testing.
 */
public class StringUtil {

    private StringUtil() {
        // Static helper class
    }

    /**
     * Returns true if text is either null or an empty string
     */
    public static boolean isEmpty(CharSequence text) {
        return text == null || text.length() == 0;
    }
}
