package com.foodpanda.urbanninja.ui.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.foodpanda.urbanninja.utils.FontCacheUtil;

public class OpenSansRegularTextView extends TextView{
    public OpenSansRegularTextView(Context context) {
        super(context);

        applyCustomFont(context);
    }

    public OpenSansRegularTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        applyCustomFont(context);
    }

    public OpenSansRegularTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        applyCustomFont(context);
    }

    private void applyCustomFont(Context context) {
        Typeface customFont = FontCacheUtil.getTypeface("OpenSans-Regular.ttf", context);
        setTypeface(customFont);
    }

}
