package com.foodpanda.urbanninja.ui.util;

import android.content.Context;

import com.foodpanda.urbanninja.R;
import com.foodpanda.urbanninja.model.enums.DialogType;
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
        DialogType dialogType = DialogType.INFORMATION;

        switch (statusType) {
            case INSIDE:
                title = context.getResources().getText(R.string.delivery_zone_successful_title);
                message = context.getResources().getText(R.string.delivery_zone_successful);
                buttonLabel = context.getResources().getText(R.string.delivery_zone_successful_label);
                break;
            case OUTSIDE:
                title = context.getResources().getString(R.string.delivery_zone_outside_title, deliveryZoneName);
                message = context.getResources().getText(R.string.delivery_zone_outside);
                buttonLabel = context.getResources().getText(R.string.delivery_zone_outside_label);
                break;
            case NO_DATA:
                title = context.getResources().getText(R.string.delivery_zone_no_location_title);
                message = context.getResources().getText(R.string.delivery_zone_no_location);
                buttonLabel = context.getResources().getText(R.string.delivery_zone_no_location_label);
                dialogType = DialogType.GPS;
                break;
        }

        nestedFragmentCallback.openInformationDialog(title, message, buttonLabel, dialogType);
    }

}
