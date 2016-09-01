package com.foodpanda.urbanninja.ui.util;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.widget.EditText;

import com.foodpanda.urbanninja.R;

/**
 * Draw custom text in the canvas to show this text as a hint of currency symbol
 */
public class TagDrawable extends Drawable {

    private String text = "";
    private Paint paint;
    private EditText editText;

    public TagDrawable(EditText editText) {
        this.editText = editText;
        paint = setPaint();
    }

    /**
     * set String value that should be drawn in a canvas
     *
     * @param text value that should be drawn (for instance currency symbol)
     */
    public void setText(String text) {
        this.text = text;
        setBounds(0, 0, getIntrinsicWidth(), getIntrinsicHeight());
        invalidateSelf();
    }

    /**
     * This method draw everything on canvas.
     * here we have to set positions of paints that should be drawn
     *
     * @param canvas canvas with all items drawn
     */
    @Override
    public void draw(@NonNull Canvas canvas) {
        canvas.drawText(text, 0, editText.getLineBounds(0, new Rect()) + canvas.getClipBounds().top, paint);
    }

    /**
     * set Paint param for text
     * such as textSIze
     * text color
     * style of draw
     *
     * @return paint that would be drawn
     */
    private Paint setPaint() {
        Paint paint = new Paint();
        paint.setColor(ContextCompat.getColor(editText.getContext(), R.color.description_text_color));
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(editText.getTextSize());

        return paint;
    }

    @Override
    public void setAlpha(int i) {
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public int getIntrinsicWidth() {
        return (int) paint.measureText(text);
    }
}
