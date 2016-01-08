package com.foodpanda.urbanninja.ui.interfaces;

import com.foodpanda.urbanninja.api.model.RouteWrapper;
import com.foodpanda.urbanninja.api.model.ScheduleWrapper;
import com.foodpanda.urbanninja.model.GeoCoordinate;
import com.foodpanda.urbanninja.model.VehicleDeliveryAreaRiderBundle;

public interface MainActivityCallback {
    void onSeeMapClicked(GeoCoordinate geoCoordinate, String pinLabel);

    void enableActionButton(boolean b, int text);

    void openReadyToWork(ScheduleWrapper scheduleWrapper);

    void openEmptyListFragment(VehicleDeliveryAreaRiderBundle vehicleDeliveryAreaRiderBundle);

    void openPickUp(RouteWrapper routeWrapper);

    void openTaskList();
}
