package com.foodpanda.urbanninja.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.foodpanda.urbanninja.Constants;
import com.foodpanda.urbanninja.R;
import com.foodpanda.urbanninja.model.Stop;
import com.foodpanda.urbanninja.model.enums.DialogType;
import com.foodpanda.urbanninja.ui.interfaces.MainActivityCallback;
import com.foodpanda.urbanninja.utils.FontCacheUtil;


public class IssueVendorCustomerDialog extends DialogFragment {
    private MainActivityCallback mainActivityCallback;

    private DialogType dialogType;
    private Stop stop;

    private TextView txtFirstActionTitle;
    private TextView txtFirstActionDescription;
    private ImageView imageFirstAction;

    private TextView txtSecondActionTitle;
    private TextView txtSecondActionDescription;
    private ImageView imageSecondAction;

    public static IssueVendorCustomerDialog newInstance(DialogType dialogType, Stop stop) {
        IssueVendorCustomerDialog issueVendorCustomerDialog = new IssueVendorCustomerDialog();

        Bundle bundle = new Bundle();
        bundle.putString(Constants.BundleKeys.DIALOG_TYPE, dialogType.name());
        bundle.putParcelable(Constants.BundleKeys.STOP, stop);
        issueVendorCustomerDialog.setArguments(bundle);

        return issueVendorCustomerDialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dialogType = DialogType.valueOf(getArguments().getString(Constants.BundleKeys.DIALOG_TYPE));
        stop = getArguments().getParcelable(Constants.BundleKeys.STOP);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivityCallback = (MainActivityCallback) context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // create ContextThemeWrapper from the original Activity Context with the custom theme
        Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.popup_theme);
        LayoutInflater localInflater = LayoutInflater.from(getContext()).cloneInContext(contextThemeWrapper);

        View view = localInflater.inflate(R.layout.vendor_customer_issue_dialog_layout, null);

        view.findViewById(R.id.layout_first_action).setOnClickListener(view12 -> catchFirstButtonClick());
        txtFirstActionTitle = (TextView) view.findViewById(R.id.txt_first_action_title);
        txtFirstActionDescription = (TextView) view.findViewById(R.id.txt_first_action_description);
        imageFirstAction = (ImageView) view.findViewById(R.id.image_first_action);

        view.findViewById(R.id.layout_second_action).setOnClickListener(view1 -> catchSecondButtonClick());
        txtSecondActionTitle = (TextView) view.findViewById(R.id.txt_second_action_title);
        txtSecondActionDescription = (TextView) view.findViewById(R.id.txt_second_action_description);
        imageSecondAction = (ImageView) view.findViewById(R.id.image_second_action);
        setData();

        return new AlertDialog.Builder(contextThemeWrapper)
            .setView(view)
            .setIcon(R.drawable.icon_alert_orange)
            .setTitle(FontCacheUtil.typeface(getContext(), getResources().getText(getTitleResource()), true))
            .setNegativeButton(android.R.string.cancel,
                (dialog, whichButton) -> dialog.dismiss()
            )
            .create();
    }

    /**
     * Handle clicks to first button in each dialog types
     * depends on the type make proper action such as open new dialog or open web-view ¬
     */
    private void catchFirstButtonClick() {
        switch (dialogType) {
            case ISSUE_VENDOR_CUSTOMER_SELECTION:
                mainActivityCallback.showIssueDialog(DialogType.ISSUE_CUSTOMER_SENDING);
                break;

            case ISSUE_CUSTOMER_SENDING:
                openPhoneNumber(stop.getDeliveryPhone());
                break;

            case ISSUE_VENDOR_SENDING:
                openPhoneNumber(stop.getPickupPhone());
                break;
        }

        dismiss();
    }

    /**
     * Handle clicks to second button in each dialog types
     * depends on the type make proper action such as open new dialog or open phone call app
     */
    private void catchSecondButtonClick() {
        switch (dialogType) {
            case ISSUE_VENDOR_CUSTOMER_SELECTION:
                mainActivityCallback.showIssueDialog(DialogType.ISSUE_VENDOR_SENDING);
                break;

            case ISSUE_CUSTOMER_SENDING:
                openIssueSection(Constants.Urls.CUSTOMER_ISSUE_URL);
                break;

            case ISSUE_VENDOR_SENDING:
                openIssueSection(Constants.Urls.VENDOR_ISSUE_URL);
                break;
        }

        dismiss();
    }

    /**
     * Check is phone is not empty call {@link MainActivityCallback} to open phone call dialog
     *
     * @param phoneNumber custmer or vendor phone number that should be called
     */
    private void openPhoneNumber(String phoneNumber) {
        if (!TextUtils.isEmpty(phoneNumber)) {
            mainActivityCallback.onPhoneSelected(phoneNumber);
        }
    }

    /**
     * Call {@link MainActivityCallback} to open external browser with web page by url
     *
     * @param url constant with issue section link
     */
    private void openIssueSection(@NonNull String url) {
        mainActivityCallback.openWebPage(url);
    }

    /**
     * set customize data for each separate type of issue
     * <p/>
     * we use the same dialog fragment with the same UI structure for all cases related to the vendor
     * or customer issues.
     * <p/>
     * so in this method we set proper string descriptions and icons for each unique types of data.
     */
    private void setData() {
        switch (dialogType) {
            case ISSUE_VENDOR_CUSTOMER_SELECTION:
                imageFirstAction.setImageResource(R.drawable.icon_restaurant_green);
                txtFirstActionTitle.setText(getResources().getString(R.string.issue_vendor_customer_selection_first_action_title));
                txtFirstActionDescription.setText(getResources().getString(R.string.issue_vendor_customer_selection_first_action_description));

                imageSecondAction.setImageResource(R.drawable.icon_deliver_green);
                txtSecondActionTitle.setText(getResources().getString(R.string.issue_vendor_customer_selection_second_action_title));
                txtSecondActionDescription.setText(getResources().getString(R.string.issue_vendor_customer_selection_second_action_description));

                break;
            case ISSUE_VENDOR_SENDING:
                imageFirstAction.setImageResource(R.drawable.icon_issue_phone);
                txtFirstActionTitle.setText(getResources().getString(R.string.issue_vendor_first_action_title));
                txtFirstActionDescription.setText(getResources().getString(R.string.issue_vendor_first_action_description));

                imageSecondAction.setImageResource(R.drawable.icon_issue_warning);
                txtSecondActionTitle.setText(getResources().getString(R.string.issue_vendor_second_action_title));
                txtSecondActionDescription.setText(getResources().getString(R.string.issue_vendor_second_action_description));

                break;
            case ISSUE_CUSTOMER_SENDING:
                imageFirstAction.setImageResource(R.drawable.icon_issue_phone);
                txtFirstActionTitle.setText(getResources().getString(R.string.issue_customer_first_action_title));
                txtFirstActionDescription.setText(getResources().getString(R.string.issue_customer_first_action_description));

                imageSecondAction.setImageResource(R.drawable.icon_issue_warning);
                txtSecondActionTitle.setText(getResources().getString(R.string.issue_customer_second_action_title));
                txtSecondActionDescription.setText(getResources().getString(R.string.issue_customer_second_action_description));

                break;
        }
    }

    /**
     * For each dialog we need separate title and
     * in this method we get proper data from resources
     *
     * @return link to the title resources
     */
    @StringRes
    private int getTitleResource() {
        switch (dialogType) {
            case ISSUE_VENDOR_CUSTOMER_SELECTION:
                return R.string.issue_vendor_customer_selection_title;
            case ISSUE_VENDOR_SENDING:
                return R.string.issue_vendor_title;
            case ISSUE_CUSTOMER_SENDING:
                return R.string.issue_customer_title;
            default:
                return R.string.issue_vendor_customer_selection_title;
        }
    }

}
