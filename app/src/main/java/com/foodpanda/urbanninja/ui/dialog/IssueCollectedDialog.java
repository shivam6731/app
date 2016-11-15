package com.foodpanda.urbanninja.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.foodpanda.urbanninja.Constants;
import com.foodpanda.urbanninja.R;
import com.foodpanda.urbanninja.model.Country;
import com.foodpanda.urbanninja.model.enums.CollectionIssueReason;
import com.foodpanda.urbanninja.ui.interfaces.MainActivityCallback;
import com.foodpanda.urbanninja.ui.util.TagDrawable;
import com.foodpanda.urbanninja.utils.FontCacheUtil;
import com.foodpanda.urbanninja.utils.FormatUtil;

public class IssueCollectedDialog extends DialogFragment {
    private MainActivityCallback mainActivityCallback;
    private EditText editTextAmount;
    private Country country;

    public static IssueCollectedDialog newInstance(Country country) {
        IssueCollectedDialog issueCollectedDialog = new IssueCollectedDialog();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BundleKeys.COUNTRY, country);
        issueCollectedDialog.setArguments(bundle);

        return issueCollectedDialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivityCallback = (MainActivityCallback) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        country = getArguments().getParcelable(Constants.BundleKeys.COUNTRY);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // create ContextThemeWrapper from the original Activity Context with the custom theme
        Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.popup_theme);

        View view = View.inflate(getContext(), R.layout.collection_issue_dialog_layout, null);

        editTextAmount = (EditText) view.findViewById(R.id.edit_collect_amount);
        setCurrencySymbol();

        //set reasons for adapter
        Spinner spinner = (Spinner) view.findViewById(R.id.spinner_collect_reason);
        setSpinnerAdapter(spinner, contextThemeWrapper);

        return new AlertDialog.Builder(contextThemeWrapper)
            .setIcon(R.drawable.icon_alert_orange)
            .setView(view)
            .setTitle(FontCacheUtil.typeface(getContext(), getResources().getText(R.string.issue_collection_report_title), true))
            .setPositiveButton(getResources().getText(R.string.issue_collection_report_ok),
                (dialog, whichButton) -> sendCollectionIssue()
            )
            .create();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    /**
     * set reasons string translated data for spinner and set with data to adapter
     *
     * @param spinner Spinner to set reasons data
     * @param context specific context with custome style for spinner
     */
    private void setSpinnerAdapter(Spinner spinner, Context context) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            context,
            android.R.layout.simple_spinner_dropdown_item,
            CollectionIssueReason.convertToTranslationList(getContext())
        );

        spinner.setAdapter(adapter);
    }

    /**
     * Send issue reason and amount of collected money for the server side
     * or if fields are empty show toast with
     */
    private void sendCollectionIssue() {
        double amount = getAmountOfMoney();
        CollectionIssueReason reason = getReason();

        if (amount > 0 && reason != null) {
            mainActivityCallback.sendCollectionIssue(amount, reason);
        } else {
            Toast.makeText(getContext(), R.string.issue_collection_empty_fields, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Parse editText to get amount of money that rider wrote in a dialog
     *
     * @return parsed amount of money
     */
    private double getAmountOfMoney() {
        String amount = editTextAmount.getText().toString();

        return TextUtils.isEmpty(amount) ? 0 : Double.valueOf(amount);
    }

    /**
     * Parse editText to get reason of an issue that rider wrote in a dialog
     *
     * @return reason String for editText from dialog
     */
    private CollectionIssueReason getReason() {
        Spinner spinner = (Spinner) getDialog().findViewById(R.id.spinner_collect_reason);

        return CollectionIssueReason.values()[spinner.getSelectedItemPosition()];
    }

    /**
     * Add currency symbol for amount of money
     * that rider put in edit text
     */
    private void setCurrencySymbol() {
        editTextAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    TagDrawable currencyDrawable = new TagDrawable(editTextAmount);
                    currencyDrawable.setText(FormatUtil.getCurrencySymbol(country));
                    editTextAmount.setCompoundDrawablesWithIntrinsicBounds(currencyDrawable, null, null, null);
                    editTextAmount.invalidate();
                } else {
                    editTextAmount.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                    editTextAmount.invalidate();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

}
