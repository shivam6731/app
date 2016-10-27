package com.foodpanda.urbanninja.ui.util;

import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.foodpanda.urbanninja.R;
import com.foodpanda.urbanninja.manager.StorageManager;
import com.foodpanda.urbanninja.model.RouteStopActivity;
import com.foodpanda.urbanninja.model.Stop;
import com.foodpanda.urbanninja.model.enums.RouteStopActivityType;
import com.foodpanda.urbanninja.model.enums.RouteStopTask;
import com.foodpanda.urbanninja.ui.activity.BaseActivity;
import com.foodpanda.urbanninja.utils.FormatUtil;

import javax.inject.Inject;

public class OrderTypeAndPaymentHelper {
    private BaseActivity baseActivity;
    private Stop currentStop;
    private StorageManager storageManager;

    @Inject
    public OrderTypeAndPaymentHelper(BaseActivity baseActivity, StorageManager storageManager) {
        this.baseActivity = baseActivity;
        this.currentStop = storageManager.getCurrentStop();
        this.storageManager = storageManager;
    }

    /**
     * Put the icon and description for type textView
     * and add order payment method label
     *
     * @param layoutTypeAndPayment container layout for all info related to the type and payment method
     */
    public void setType(RelativeLayout layoutTypeAndPayment) {
        RouteStopTask task = currentStop.getTask();

        TextView txtType = (TextView) layoutTypeAndPayment.findViewById(R.id.txt_type);
        TextView txtPayment = (TextView) layoutTypeAndPayment.findViewById(R.id.txt_payment);
        ImageView imageType = (ImageView) layoutTypeAndPayment.findViewById(R.id.image_type);

        int textResource = task == RouteStopTask.PICKUP ? R.string.task_details_pick_up : R.string.task_details_delivery;
        txtType.setText(baseActivity.getResources().getText(textResource));

        int iconResource = task == RouteStopTask.PICKUP ? R.drawable.icon_restaurant_green : R.drawable.icon_deliver_green;
        imageType.setImageResource(iconResource);

        setPaymentMethod(txtPayment);
    }

    /**
     * We should let rider know if we an order is paid only
     * or he should collect money from customer.
     * And to do so we use TextView field below the order type
     *
     * @param txtPaymentMethod container for payment method or value
     */
    private void setPaymentMethod(TextView txtPaymentMethod) {
        Stop deliveryStop = storageManager.getDeliveryPartOfEachRouteStop(currentStop);
        if (deliveryStop == null || deliveryStop.getActivities() == null) {
            txtPaymentMethod.setText("");

            return;
        }
        for (RouteStopActivity routeStopActivity : deliveryStop.getActivities()) {
            if (isOrderPaidByCash(routeStopActivity)) {
                txtPaymentMethod.setText(
                    FormatUtil.getValueWithCurrencySymbol(storageManager.getCountry(), routeStopActivity.getValue()));

                return;
            }
        }

        txtPaymentMethod.setText(baseActivity.getResources().getString(R.string.payment_method_already_paid));
    }

    /**
     * Only one case when rider should get money from customer
     * is when we has RouteStopActivityType.COLLECT action activity
     * and the value for this activity is not 0
     *
     * @param routeStopActivity delivery action
     * @return true when rider should collect money from customer
     */
    private boolean isOrderPaidByCash(RouteStopActivity routeStopActivity) {
        return routeStopActivity.getType() == RouteStopActivityType.COLLECT &&
            (TextUtils.isEmpty(routeStopActivity.getValue()) ||
                !"0".equalsIgnoreCase(routeStopActivity.getValue()));
    }

}
