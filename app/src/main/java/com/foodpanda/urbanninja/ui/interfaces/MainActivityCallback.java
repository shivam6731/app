package com.foodpanda.urbanninja.ui.interfaces;

import com.foodpanda.urbanninja.api.model.RouteWrapper;
import com.foodpanda.urbanninja.api.model.ScheduleWrapper;
import com.foodpanda.urbanninja.model.GeoCoordinate;
import com.foodpanda.urbanninja.model.VehicleDeliveryAreaRiderBundle;

public interface MainActivityCallback {
    void onSeeMapClicked(GeoCoordinate geoCoordinate);

    void enableActionButton(boolean b);

    void openReadyToWork(ScheduleWrapper scheduleWrapper);

    void openEmptyListFragment(VehicleDeliveryAreaRiderBundle vehicleDeliveryAreaRiderBundle);

    void openPickUp(RouteWrapper routeWrapper);
}
