package com.foodpanda.urbanninja.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.Log;
import android.widget.TextView;

import com.foodpanda.urbanninja.ui.util.TypefaceSpan;

import java.util.HashMap;

public class FontCacheUtil {
    private static HashMap<String, Typeface> fontCache = new HashMap<>();

    /**
     * get typeface from font name
     *
     * @param fontName font name
     * @param context  to get access to android assets
     * @return selected by name font
     */
    public static Typeface getTypeface(String fontName, Context context) {
        Typeface typeface = fontCache.get(fontName);

        if (typeface == null) {
            try {
                typeface = Typeface.createFromAsset(context.getAssets(), fontName);
            } catch (Exception e) {
                Log.e(Typeface.class.getSimpleName(), e.getMessage());

                return null;
            }

            fontCache.put(fontName, typeface);
        }

        return typeface;
    }

    /**
     * Convert selected message to the SpannableString with selected font
     *
     * @param context to get typeface from assets
     * @param message message to be converted
     * @param isBold  true if we need bold text
     * @return set font for selected message
     */
    public static SpannableString typeface(Context context, CharSequence message, boolean isBold) {
        Typeface typeface = FontCacheUtil.getTypeface(isBold ? "OpenSans-Semibold.ttf" : "OpenSans-Light.ttf", context);

        SpannableString spannableString = new SpannableString(message);
        spannableString.setSpan(new TypefaceSpan(typeface), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return spannableString;
    }

    /**
     * Set font family for alert dialogs messages.
     * To make part of text bold, part normal form information dialogs
     *
     * @param textView text field where we need set type family
     * @param context  Context to have access to the font files from assets
     */
    public static void setOpenSansTypeFaceFamilyForDialog(TextView textView, Context context) {
        textView.setTypeface(getTypeface("OpenSans-Semibold.ttf", context), Typeface.BOLD);
        textView.setTypeface(getTypeface("OpenSans-Light.ttf", context));
    }

}
