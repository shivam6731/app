package com.foodpanda.urbanninja.manager;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Html;
import android.text.TextUtils;

import com.foodpanda.urbanninja.R;
import com.foodpanda.urbanninja.model.Stop;
import com.foodpanda.urbanninja.model.enums.RouteStopTask;

import java.util.LinkedList;
import java.util.List;

public class MultiPickupManager {
    private StorageManager storageManager;

    public MultiPickupManager(StorageManager storageManager) {
        this.storageManager = storageManager;
    }

    /**
     * get list of PICKUP route stop in the same place
     *
     * @return list of route stops in the same place,in case when there is no multi-pick-up
     * list with only current stop would be returned
     */
    public List<Stop> getSamePlaceStops() {
        return getSamePlaceStops(storageManager.getCurrentStop());
    }

    /**
     * check is some Stop have not single item list of places in the same place
     *
     * @param stop stop to check
     * @return true if there are more then one stop nearby
     */
    public boolean isNotEmptySamePlacePickUpStops(@NonNull Stop stop) {
        return getSamePlaceStops(stop).size() > 1;
    }

    /**
     * @param context     context to get android resources
     * @param currentStop to get related list
     * @return formatted String with orders list and details about what to do
     */
    public CharSequence getMultiPickUpDetailsSting(Context context, Stop currentStop) {
        return getFormattedHtml(context.getResources().getString(
            R.string.multi_pickup_alert_details,
            getOrderCodeString(currentStop)));
    }

    /**
     * create orderCodes list String
     *
     * @param currentStop to get related list
     * @return formatted String with order codes
     */
    private CharSequence getOrderCodeString(Stop currentStop) {
        CharSequence result = "";
        List<Stop> samePlacesPickUpStopList = getSamePlaceStops(currentStop);
        for (int i = 0; i < samePlacesPickUpStopList.size(); i++) {
            if (!TextUtils.isEmpty(samePlacesPickUpStopList.get(i).getOrderCode())) {
                result = TextUtils.concat(result, samePlacesPickUpStopList.get(i).getOrderCode());
            }
            if (i != samePlacesPickUpStopList.size() - 1) {
                result = TextUtils.concat(result, ", ");
            }
        }

        return result;
    }

    /**
     * get list of stop that are close to current one
     *
     * @param currentStop stop to check places nearby
     * @return list of route stops in the same place,in case when there is no multi-pick-up
     * list with only current stop would be returned
     */
    private List<Stop> getSamePlaceStops(Stop currentStop) {
        List<Stop> samePlacesStopList = new LinkedList<>();

        if (currentStop != null && currentStop.getTask() != null) {
            switch (currentStop.getTask()) {
                case DELIVER:
                    samePlacesStopList.add(currentStop);
                    break;
                case PICKUP:
                    samePlacesStopList.addAll(getSamePlacePickupStops());
                    break;
            }
        }

        return samePlacesStopList;
    }

    /**
     * get list of stop in the same place
     *
     * @return list of stop that are nearby
     */
    private List<Stop> getSamePlacePickupStops() {
        List<Stop> samePlacesStopList = new LinkedList<>();
        List<Stop> routeStopPlan = storageManager.getStopList();
        Stop currentStop = storageManager.getCurrentStop();
        if (currentStop != null) {
            samePlacesStopList.add(currentStop);
            //we skip the first stop because
            //first one is our currentStop
            for (int i = 1; i < routeStopPlan.size(); i++) {
                if (isPickupFromSamePlace(routeStopPlan.get(i), currentStop)) {
                    samePlacesStopList.add(routeStopPlan.get(i));
                }
            }
        }

        return samePlacesStopList;
    }

    /**
     * Check if two stop in the same the place and we task of selected one it PICKUP
     *
     * @param stop        to check if it's in the same place as current
     * @param currentStop current PICKUP stop to compare with all others
     * @return true if both stops are in the same place and selected is PICKUP
     */
    private boolean isPickupFromSamePlace(@NonNull Stop stop, @NonNull Stop currentStop) {
        return stop.getTask() == RouteStopTask.PICKUP &&
            isTheSameVendorCodes(currentStop, stop);
    }

    /**
     * Instead of checking place location we decided to check order code where we can find restaurant id
     * <p>
     * Check if we have the same pick-up part of order codes for both routeStops
     * the pattern is xxxx-yyyy
     * where xxxx we check only place code
     *
     * @param stop        next stop from the route stop plan with valid order code
     * @param currentStop current stop with valid order code
     * @return true if vendor part of order code is the same
     */
    private boolean isTheSameVendorCodes(@NonNull Stop stop, @NonNull Stop currentStop) {
        return stop.getOrderCode().substring(0, 4).
            equalsIgnoreCase(currentStop.getOrderCode().substring(0, 4));
    }

    /**
     * Because Html.fromHtml is deprecated we need to check android device version
     *
     * @param text with Html
     * @return formatted text with applied html tags
     */
    private CharSequence getFormattedHtml(String text) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            return Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(text);
        }
    }
}
