package com.foodpanda.urbanninja.manager;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

import com.foodpanda.urbanninja.model.DeliveryZone;
import com.foodpanda.urbanninja.model.GeoCoordinate;
import com.foodpanda.urbanninja.model.enums.PolygonStatusType;
import com.foodpanda.urbanninja.ui.activity.BaseActivity;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;

import java.util.LinkedList;
import java.util.List;

public class CheckPolygonManager {
    private BaseActivity baseActivity;

    public CheckPolygonManager(BaseActivity baseActivity) {
        this.baseActivity = baseActivity;
    }

    /**
     * Check if Location inside delivery zone polygon
     *
     * @param deliveryZone delivery zone where we get polygon
     * @return true if rider inside delivery zone
     */
    public PolygonStatusType checkIfLocationInPolygon(@NonNull DeliveryZone deliveryZone) {
        Location lastKnownLocation = getLastRiderLocation();
        if (lastKnownLocation == null) {
            return PolygonStatusType.NO_DATA;
        }

        LatLng locationLatLng = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());

        return PolyUtil.containsLocation(locationLatLng, convertPolygonToListLatLng(deliveryZone), false) ?
            PolygonStatusType.INSIDE : PolygonStatusType.OUTSIDE;
    }

    /**
     * Convert our polygon model to the list of LatLng
     *
     * @param deliveryZone delivery zone where we get polygon
     * @return list of LatLng point of rider delivery zone
     */
    private List<LatLng> convertPolygonToListLatLng(@NonNull DeliveryZone deliveryZone) {
        List<LatLng> latLngList = new LinkedList<>();
        if (deliveryZone.getPolygon() != null) {
            for (GeoCoordinate geoCoordinate : deliveryZone.getPolygon()) {
                latLngList.add(new LatLng(geoCoordinate.getLat(), geoCoordinate.getLon()));
            }
        }

        return latLngList;
    }

    /**
     * get last user location
     * if permission is granted we try to retrieve last location
     * otherwise return null
     *
     * @return last rider location
     */
    private Location getLastRiderLocation() {
        //When I try to split this check in a different way android studio put warning message about permission check here
        if (ContextCompat.checkSelfPermission(baseActivity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(baseActivity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationManager locationManager = (LocationManager) baseActivity.getSystemService(Context.LOCATION_SERVICE);

            return locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        }

        return null;
    }

}
