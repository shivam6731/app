package com.foodpanda.urbanninja.ui.util;

import android.content.Context;
import android.text.Html;

import com.foodpanda.urbanninja.R;
import com.foodpanda.urbanninja.model.enums.PolygonStatusType;
import com.foodpanda.urbanninja.ui.interfaces.NestedFragmentCallback;

public class DialogInfoHelper {
    /**
     * Fill title and message for information dialog
     * and send event to callback to activity to show dialog from Activity Context
     *
     * @param context                context to get resources
     * @param statusType             type of Polygon clock-in process
     * @param deliveryZoneName       name of delivery zone to show in a dialog
     * @param nestedFragmentCallback callback for activity to show this dialog
     */
    public static void showInformationDialog(
        Context context,
        PolygonStatusType statusType,
        String deliveryZoneName,
        NestedFragmentCallback nestedFragmentCallback) {

        CharSequence title = "";
        CharSequence message = "";
        CharSequence buttonLabel = "";

        switch (statusType) {
            case INSIDE:
                title = context.getResources().getText(R.string.delivery_zone_successful_title);
                message = context.getResources().getText(R.string.delivery_zone_successful);
                buttonLabel = context.getResources().getText(R.string.delivery_zone_successful_label);
                break;
            case OUTSIDE:
                title = context.getResources().getString(R.string.delivery_zone_outside_title, deliveryZoneName);
                message = getFormattedHtml(context.getString(R.string.delivery_zone_outside, deliveryZoneName));
                buttonLabel = context.getResources().getText(R.string.delivery_zone_outside_label);
                break;
            case NO_DATA:
                title = context.getResources().getText(R.string.delivery_zone_no_location_title);
                message = context.getResources().getText(R.string.delivery_zone_no_location);
                buttonLabel = context.getResources().getText(R.string.delivery_zone_no_location_label);
                break;
        }

        nestedFragmentCallback.openInformationDialog(title, message, buttonLabel, statusType == PolygonStatusType.NO_DATA);
    }

    /**
     * Because Html.fromHtml is deprecated we need to check android device version
     *
     * @param text with Html
     * @return formatted text with applied html tags
     */
    private static CharSequence getFormattedHtml(String text) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            return Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(text);
        }
    }
}
