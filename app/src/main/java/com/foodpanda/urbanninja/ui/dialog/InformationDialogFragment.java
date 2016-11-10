package com.foodpanda.urbanninja.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.widget.TextView;

import com.foodpanda.urbanninja.Constants;
import com.foodpanda.urbanninja.R;
import com.foodpanda.urbanninja.model.enums.DialogType;
import com.foodpanda.urbanninja.ui.interfaces.MainActivityCallback;
import com.foodpanda.urbanninja.utils.FontCacheUtil;

public class InformationDialogFragment extends DialogFragment {
    private MainActivityCallback mainActivityCallback;

    private CharSequence title;
    private CharSequence message;
    private CharSequence buttonLabel;
    private DialogType dialogType;

    public static InformationDialogFragment newInstance(
        CharSequence title,
        CharSequence message,
        CharSequence buttonLabel,
        DialogType dialogType
    ) {
        InformationDialogFragment dialogFragment = new InformationDialogFragment();

        Bundle args = new Bundle();
        args.putCharSequence(Constants.BundleKeys.TITLE, title);
        args.putCharSequence(Constants.BundleKeys.MESSAGE, message);
        args.putCharSequence(Constants.BundleKeys.LABEL, buttonLabel);
        args.putString(Constants.BundleKeys.DIALOG_TYPE, dialogType.name());
        dialogFragment.setArguments(args);

        return dialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        title = getArguments().getCharSequence(Constants.BundleKeys.TITLE);
        message = getArguments().getCharSequence(Constants.BundleKeys.MESSAGE);
        buttonLabel = getArguments().getCharSequence(Constants.BundleKeys.LABEL);
        dialogType = DialogType.valueOf(getArguments().getString(Constants.BundleKeys.DIALOG_TYPE));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivityCallback = (MainActivityCallback) context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.popup_theme))
            .setIcon(R.drawable.icon_alert_red)
            .setTitle(FontCacheUtil.typeface(getContext(), title, true))
            .setMessage(message)
            .setPositiveButton(FontCacheUtil.typeface(getContext(), buttonLabel, false),
                (dialog, whichButton) -> {
                    onPositiveButtonClicked();
                    dismiss();
                }
            );

        AlertDialog alertDialog;
        setCancelable(dialogType != DialogType.FAKE_LOCATION_SETTING);

        //set information details depends on type
        switch (dialogType) {
            case ISSUE_COLLECTION_WARNING:
                alertDialogBuilder
                    .setNegativeButton(android.R.string.cancel, (dialogInterface, i) -> {
                        dismiss();
                    });
                alertDialog = alertDialogBuilder.create();
                break;
            case INFORMATION:
                alertDialog = alertDialogBuilder.create();
                //Add primary text color for alert dialog
                alertDialog.setOnShowListener(
                    arg0 ->
                        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).
                            setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary)));

                break;
            default:
                alertDialog = alertDialogBuilder.create();
                break;

        }

        //set custom fonts for view in alert dialog
        alertDialog.setOnShowListener(dialogInterface -> {
            FontCacheUtil.setOpenSansTypeFaceFamilyForDialog((TextView) alertDialog.findViewById(android.R.id.message), getContext());
        });

        return alertDialog;
    }

    /**
     * Call main activity callback to redirect to the next step dependence on type of dialog
     */
    private void onPositiveButtonClicked() {
        switch (dialogType) {
            case GPS:
                mainActivityCallback.onGPSSettingClicked();
                break;
            case ISSUE_COLLECTION_WARNING:
                mainActivityCallback.showCollectionIssueDialog();
                break;
            case FAKE_LOCATION_SETTING:
                mainActivityCallback.showDevSetting();
                break;
        }

    }

}

