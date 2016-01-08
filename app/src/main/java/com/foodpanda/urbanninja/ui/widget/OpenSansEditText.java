package com.foodpanda.urbanninja.ui.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

import com.foodpanda.urbanninja.utils.FontCacheUtil;

public class OpenSansEditText extends EditText {
    public OpenSansEditText(Context context) {
        super(context);

        applyCustomFont(context);
    }

    public OpenSansEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

        applyCustomFont(context);
    }

    public OpenSansEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        applyCustomFont(context);
    }

    private void applyCustomFont(Context context) {
        Typeface currentTypeFace = getTypeface();
        Typeface customFont;
        if (currentTypeFace != null && currentTypeFace.getStyle() == Typeface.BOLD) {
            customFont = FontCacheUtil.getTypeface("OpenSans-Semibold.ttf", context);
            setTypeface(customFont, Typeface.BOLD);
        } else {
            customFont = FontCacheUtil.getTypeface("OpenSans-Regular.ttf", context);
            setTypeface(customFont);
        }
    }
}
