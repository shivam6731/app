package com.foodpanda.urbanninja.ui.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

import com.foodpanda.urbanninja.utils.FontCacheUtil;

public class MontserratButton extends Button {
    public MontserratButton(Context context) {
        super(context);

        applyCustomFont(context);
    }

    public MontserratButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        applyCustomFont(context);
    }

    public MontserratButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        applyCustomFont(context);
    }

    private void applyCustomFont(Context context) {
        Typeface currentTypeFace = getTypeface();
        Typeface customFont;
        if (currentTypeFace != null && currentTypeFace.getStyle() == Typeface.BOLD) {
            customFont = FontCacheUtil.getTypeface("Montserrat-Bold.ttf", context);
            setTypeface(customFont, Typeface.BOLD);
        } else {
            customFont = FontCacheUtil.getTypeface("Montserrat-Regular.ttf", context);
            setTypeface(customFont);
        }
    }
}
