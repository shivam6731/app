package com.foodpanda.urbanninja.ui.interfaces;

import com.foodpanda.urbanninja.model.GeoCoordinate;

public interface MainActivityCallback {
    void onSeeMapClicked(GeoCoordinate geoCoordinate);

    void enableActionButton(boolean b);
}
