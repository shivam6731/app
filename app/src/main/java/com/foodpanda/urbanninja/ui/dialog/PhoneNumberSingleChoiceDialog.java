package com.foodpanda.urbanninja.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.widget.Toast;

import com.foodpanda.urbanninja.Constants;
import com.foodpanda.urbanninja.R;
import com.foodpanda.urbanninja.model.Stop;
import com.foodpanda.urbanninja.ui.interfaces.MainActivityCallback;

import java.util.HashMap;
import java.util.Map;

public class PhoneNumberSingleChoiceDialog extends DialogFragment {
    private MainActivityCallback mainActivityCallback;
    private Stop stop;
    private Map<String, String> phoneNameMap = new HashMap<>();

    public static PhoneNumberSingleChoiceDialog newInstance(Stop stop) {
        PhoneNumberSingleChoiceDialog phoneNumberSingleChoiceDialog = new PhoneNumberSingleChoiceDialog();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BundleKeys.STOP, stop);
        phoneNumberSingleChoiceDialog.setArguments(bundle);

        return phoneNumberSingleChoiceDialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stop = getArguments().getParcelable(Constants.BundleKeys.STOP);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivityCallback = (MainActivityCallback) activity;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle(R.string.dialog_phone_title);

        final String[] values = setPhoneData();
        if (phoneNameMap.isEmpty()) {
            Toast.makeText(getContext(), getResources().getString(R.string.dialog_phone_not_numbers), Toast.LENGTH_SHORT).show();
            dismiss();
        }

        dialog.setSingleChoiceItems(values, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mainActivityCallback.onPhoneSelected(phoneNameMap.get(values[which]));
                dialog.dismiss();
            }
        });

        return dialog.create();
    }

    private String[] setPhoneData() {
        if (stop != null) {
            if (!TextUtils.isEmpty(stop.getDeliveryPhone())) {
                phoneNameMap.put(getResources().getString(R.string.dialog_phone_customer), stop.getDeliveryPhone());
            }
            if (!TextUtils.isEmpty(stop.getPickupPhone())) {
                phoneNameMap.put(getResources().getString(R.string.dialog_phone_restaurant), stop.getPickupPhone());
            }
        }

        return phoneNameMap.keySet().toArray(new String[phoneNameMap.keySet().size()]);
    }
}
