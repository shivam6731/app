package com.foodpanda.urbanninja.ui.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.foodpanda.urbanninja.utils.FontCacheUtil;

public class MontserratTextView extends TextView {
    public MontserratTextView(Context context) {
        super(context);

        applyCustomFont(context);
    }

    public MontserratTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        applyCustomFont(context);
    }

    public MontserratTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        applyCustomFont(context);
    }

    private void applyCustomFont(Context context) {
        Typeface customFont = FontCacheUtil.getTypeface("Montserrat-Bold.ttf", context);
        setTypeface(customFont);
    }
}
