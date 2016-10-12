package com.foodpanda.urbanninja.manager;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;

import com.foodpanda.urbanninja.model.DeliveryZone;
import com.foodpanda.urbanninja.model.GeoCoordinate;
import com.foodpanda.urbanninja.model.StartingPoint;
import com.foodpanda.urbanninja.model.enums.PolygonStatusType;
import com.foodpanda.urbanninja.ui.activity.BaseActivity;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;

import java.util.LinkedList;
import java.util.List;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

class CheckPolygonManager {
    private static final int RADIUS_TO_STARTING_POINT_IN_METERS = 300;
    //We need activity to try to retrieve last rider location
    private BaseActivity baseActivity;

    CheckPolygonManager(BaseActivity baseActivity) {
        this.baseActivity = baseActivity;
    }

    /**
     * Check if Location inside delivery zone polygon
     *
     * @param deliveryZone delivery zone where we get polygon and starting point
     * @return true if rider inside delivery zone
     */
    PolygonStatusType checkIfLocationInPolygonOrNearStartingPoint(@NonNull DeliveryZone deliveryZone) {
        Location lastKnownLocation = getLastRiderLocation();
        if (lastKnownLocation == null) {
            return PolygonStatusType.NO_DATA;
        }

        return isLocationInsidePolygonOrNearStartingPoint(deliveryZone, lastKnownLocation) ?
            PolygonStatusType.INSIDE :
            PolygonStatusType.OUTSIDE;
    }

    private boolean isLocationInsidePolygonOrNearStartingPoint(
        DeliveryZone deliveryZone,
        Location lastKnownLocation
    ) {
        LatLng locationLatLng = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());

        return isLocationInPolygon(locationLatLng, convertPolygonToListLatLng(deliveryZone)) ||
            isLocationNearStartingPoint(locationLatLng, deliveryZone.getStartingPoint());
    }

    /**
     * Check if current rider location is inside delivery polygon
     *
     * @param locationLatLng current rider location LatLng
     * @param polygon        delivery zone polygon converted to List<LatLng>
     * @return true if rider location is inside in delivery zone polygon
     */
    private boolean isLocationInPolygon(LatLng locationLatLng, List<LatLng> polygon) {
        return PolyUtil.containsLocation(locationLatLng, polygon, false);
    }

    /**
     * Check the distance between rider current location and starting point
     * In case when he close enough we would allow to clock-in
     * we compare distance with RADIUS_TO_STARTING_POINT_IN_METERS constant
     *
     * @param locationLatLng rider location {@link LatLng}
     * @param startingPoint  starting point to get coordinates from
     * @return true if distance is less then RADIUS_TO_STARTING_POINT_IN_METERS
     */
    private boolean isLocationNearStartingPoint(@NonNull LatLng locationLatLng, StartingPoint startingPoint) {
        if (startingPoint == null || startingPoint.getCoordinate() == null) {
            return false;
        }
        float[] results = new float[1];
        Location.distanceBetween(
            locationLatLng.latitude,
            locationLatLng.longitude,
            startingPoint.getCoordinate().getLat(),
            startingPoint.getCoordinate().getLon(),
            results);

        return results[0] < RADIUS_TO_STARTING_POINT_IN_METERS;
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
    @Nullable
    Location getLastRiderLocation() {
        //When I try to split this check in a different way android studio put warning message about permission check here
        if (ContextCompat.checkSelfPermission(baseActivity, ACCESS_FINE_LOCATION) == PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(baseActivity, ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED) {
            LocationManager locationManager = (LocationManager) baseActivity.getSystemService(Context.LOCATION_SERVICE);

            return locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        }

        return null;
    }

}
