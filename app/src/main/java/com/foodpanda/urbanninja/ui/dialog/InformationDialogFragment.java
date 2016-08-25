package com.foodpanda.urbanninja.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import com.foodpanda.urbanninja.Constants;
import com.foodpanda.urbanninja.R;
import com.foodpanda.urbanninja.ui.interfaces.MainActivityCallback;

public class InformationDialogFragment extends DialogFragment {
    private MainActivityCallback mainActivityCallback;

    private CharSequence title;
    private CharSequence message;
    private CharSequence buttonLabel;
    private boolean redirectToSetting;

    public static InformationDialogFragment newInstance(
        CharSequence title,
        CharSequence message,
        CharSequence buttonLabel,
        boolean redirectToSetting) {
        InformationDialogFragment dialogFragment = new InformationDialogFragment();

        Bundle args = new Bundle();
        args.putCharSequence(Constants.BundleKeys.TITLE, title);
        args.putCharSequence(Constants.BundleKeys.MESSAGE, message);
        args.putCharSequence(Constants.BundleKeys.LABEL, buttonLabel);
        args.putBoolean(Constants.BundleKeys.SETTING_OPTION, redirectToSetting);
        dialogFragment.setArguments(args);

        return dialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        title = getArguments().getCharSequence(Constants.BundleKeys.TITLE);
        message = getArguments().getCharSequence(Constants.BundleKeys.MESSAGE);
        buttonLabel = getArguments().getCharSequence(Constants.BundleKeys.LABEL);
        redirectToSetting = getArguments().getBoolean(Constants.BundleKeys.SETTING_OPTION);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivityCallback = (MainActivityCallback) context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
            .setIcon(R.drawable.icon_alert_orange)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(buttonLabel,
                (dialog, whichButton) -> {
                    if (redirectToSetting) {
                        mainActivityCallback.onGPSSettingClicked();
                    }
                    InformationDialogFragment.this.dismiss();
                }
            ).create();

        //Add primary text color for alert dialog
        alertDialog.setOnShowListener(
            arg0 -> alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).
                setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary)));

        return alertDialog;
    }
}

