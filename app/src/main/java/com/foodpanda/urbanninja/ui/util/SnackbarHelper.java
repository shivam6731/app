package com.foodpanda.urbanninja.ui.util;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.foodpanda.urbanninja.Constants;
import com.foodpanda.urbanninja.R;

public class SnackbarHelper {
    private Context context;
    private View view;

    public SnackbarHelper(Context context, View view) {
        this.context = context;
        this.view = view;
    }

    public void showOrderCanceledSnackbar() {
        final Snackbar snackbar = Snackbar
            .make(view,
                context.getResources().getString(R.string.task_details_canceled),
                Constants.SNACKBAR_DURATION_IN_MILLISECONDS);

        snackbar.setAction(context.getResources().getString(R.string.task_details_canceled_dismiss), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //If left this method empty it would automaticaly dismiss snackbar
                //otherwise  call dismiss method we would not have animation
            }
        });
        snackbar.setActionTextColor(ContextCompat.getColor(context, R.color.snack_bar_alert_text_color));
        View snackbarView = snackbar.getView();

        // Changing  background color
        snackbarView.setBackgroundColor(ContextCompat.getColor(context, R.color.snackbar_background));

        // Changing action button text color and style
        TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        textView.setTypeface(null, Typeface.BOLD);
        snackbar.show();
    }
}
