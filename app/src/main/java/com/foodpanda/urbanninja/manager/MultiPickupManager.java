package com.foodpanda.urbanninja.manager;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.foodpanda.urbanninja.R;
import com.foodpanda.urbanninja.model.GeoCoordinate;
import com.foodpanda.urbanninja.model.Stop;
import com.foodpanda.urbanninja.model.enums.RouteStopTask;
import com.foodpanda.urbanninja.ui.util.DialogInfoHelper;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

public class MultiPickupManager {

    private static final BigDecimal SAME_PLACE_ACCURACY = BigDecimal.valueOf(0.00003);
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
        return DialogInfoHelper.getFormattedHtml(
            context.getResources().getString(
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
        Stop currentStop = storageManager.getCurrentStop();
        if (currentStop != null) {
            for (Stop stop : storageManager.getStopList()) {

                if (isPickupFromSamePlace(stop, currentStop)) {
                    samePlacesStopList.add(stop);
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
    private boolean isPickupFromSamePlace(Stop stop, Stop currentStop) {
        return stop.getTask() == RouteStopTask.PICKUP && isLocationNear(currentStop.getCoordinate(), stop.getCoordinate());
    }

    /**
     * Check if places nearby to each other or in the same place
     * </p>
     * We have two cases for multi pick-up and for
     *
     * @param currentStopCoordinate current rider route stop coordinates
     * @param nextStopCoordinate    next step coordinates to compare
     * @return true if stops in the same place or nearby
     */
    private boolean isLocationNear(GeoCoordinate currentStopCoordinate, GeoCoordinate nextStopCoordinate) {
        return BigDecimal.valueOf(currentStopCoordinate.getLat()).subtract(BigDecimal.valueOf(nextStopCoordinate.getLat())).abs().compareTo(SAME_PLACE_ACCURACY) == -1 &&
            BigDecimal.valueOf(currentStopCoordinate.getLon()).subtract(BigDecimal.valueOf(nextStopCoordinate.getLon())).abs().compareTo(SAME_PLACE_ACCURACY) == -1;
    }
}
