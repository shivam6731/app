package com.foodpanda.urbanninja.ui.interfaces;

import com.foodpanda.urbanninja.api.model.ScheduleWrapper;
import com.foodpanda.urbanninja.model.GeoCoordinate;
import com.foodpanda.urbanninja.model.Stop;
import com.foodpanda.urbanninja.model.VehicleDeliveryAreaRiderBundle;

public interface MainActivityCallback {
    void onSeeMapClicked(GeoCoordinate geoCoordinate, String pinLabel);

    void enableActionButton(boolean b, int text);

    void changeActionButtonVisibility(boolean b);

    void openReadyToWork(ScheduleWrapper scheduleWrapper);

    void openEmptyListFragment(VehicleDeliveryAreaRiderBundle vehicleDeliveryAreaRiderBundle);

    void openRouteStopDetails(Stop stop);

    void openRouteStopActionList(Stop stop);

    void openLoadFragment();
}
