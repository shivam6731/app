package com.foodpanda.urbanninja.ui.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.AttributeSet;

import com.foodpanda.urbanninja.utils.FontCacheUtil;

public class OpenSansCheckBox extends AppCompatCheckBox {
    public OpenSansCheckBox(Context context) {
        super(context);

        applyCustomFont(context);
    }

    public OpenSansCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);

        applyCustomFont(context);
    }

    public OpenSansCheckBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        applyCustomFont(context);
    }

    private void applyCustomFont(Context context) {
        Typeface currentTypeFace = getTypeface();
        Typeface customFont;
        if (currentTypeFace != null) {
            switch (currentTypeFace.getStyle()) {
                case Typeface.BOLD:
                    customFont = FontCacheUtil.getTypeface("OpenSans-Semibold.ttf", context);
                    setTypeface(customFont, Typeface.BOLD);
                    break;
                case Typeface.ITALIC:
                    customFont = FontCacheUtil.getTypeface("OpenSans-LightItalic.ttf", context);
                    setTypeface(customFont, Typeface.ITALIC);
                    break;
                default:
                    customFont = FontCacheUtil.getTypeface("OpenSans-Light.ttf", context);
                    setTypeface(customFont);
                    break;
            }
        }
    }
}
