package com.foodpanda.urbanninja.ui.fragments;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.foodpanda.urbanninja.Constants;
import com.foodpanda.urbanninja.R;
import com.foodpanda.urbanninja.model.MapDetailsProvider;
import com.foodpanda.urbanninja.model.enums.MapPointType;
import com.foodpanda.urbanninja.ui.interfaces.MapAddressDetailsCallback;
import com.foodpanda.urbanninja.ui.interfaces.MapAddressDetailsChangeListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.LinkedList;
import java.util.List;

public class MapAddressDetailsFragment extends BaseFragment implements
    OnMapReadyCallback,
    MapAddressDetailsChangeListener {

    //Minimal radius to clock-in outside of this radius clock-in would be forbidden
    private static final int CLOCK_IN_RADIUS = 400;

    public static final float MARKER_ANCHOR = 0.5f;

    private TextView txtName;
    private TextView txtAddress;
    private TextView txtComment;
    private CheckBox checkBoxDone;

    private LinearLayout layoutComment;
    private LinearLayout layoutAddress;

    private GoogleMap googleMap;
    private MapView mapView;

    private Location location;

    private MapAddressDetailsCallback mapAddressDetailsCallback;

    private MapDetailsProvider mapDetailsProvider;
    private boolean isMapActionShown;
    private MapPointType mapPointType;

    public static MapAddressDetailsFragment newInstance(MapDetailsProvider mapDetailsProvider, MapPointType mapPointType) {
        return newInstance(mapDetailsProvider, mapPointType, false);
    }

    public static MapAddressDetailsFragment newInstance(MapDetailsProvider mapDetailsProvider, MapPointType mapPointType, boolean isMapActionShown) {
        Bundle bundle = new Bundle();

        bundle.putParcelable(Constants.BundleKeys.MAP_ADDRESS_DETAILS, mapDetailsProvider);
        bundle.putSerializable(Constants.BundleKeys.MAP_ADDRESS_POINT_TYPE, mapPointType);
        bundle.putBoolean(Constants.BundleKeys.MAP_ADDRESS_LAYOUT_SHOWN, isMapActionShown);

        MapAddressDetailsFragment fragment = new MapAddressDetailsFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mapAddressDetailsCallback = (MapAddressDetailsCallback) getParentFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mapDetailsProvider = getArguments().getParcelable(Constants.BundleKeys.MAP_ADDRESS_DETAILS);
        mapPointType = (MapPointType) getArguments().getSerializable(Constants.BundleKeys.MAP_ADDRESS_POINT_TYPE);
        isMapActionShown = getArguments().getBoolean(Constants.BundleKeys.MAP_ADDRESS_LAYOUT_SHOWN);
    }

    @Nullable
    @Override
    public View onCreateView(
        LayoutInflater inflater,
        ViewGroup container,
        Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.map_and_address_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        txtName = (TextView) view.findViewById(R.id.txt_name);
        txtComment = (TextView) view.findViewById(R.id.txt_comment);
        txtAddress = (TextView) view.findViewById(R.id.txt_address);

        layoutAddress = (LinearLayout) view.findViewById(R.id.layout_address);
        layoutComment = (LinearLayout) view.findViewById(R.id.layout_comment);
        RelativeLayout layoutMapActions = (RelativeLayout) view.findViewById(R.id.map_actions_layout);
        layoutMapActions.setVisibility(isMapActionShown ? View.VISIBLE : View.GONE);

        checkBoxDone = (CheckBox) view.findViewById(R.id.checkbox_done);
        checkBoxDone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mapAddressDetailsCallback != null) {
                    mapAddressDetailsCallback.setActionButtonEnable(isChecked);
                }
            }
        });
        setActionDoneCheckboxVisibility(mapDetailsProvider.isDoneButtonVisible());

        FloatingActionButton floatingActionButton = (FloatingActionButton) view.findViewById(R.id.floating_button_map);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mapAddressDetailsCallback != null) {
                    mapAddressDetailsCallback.onSeeMapClicked(mapDetailsProvider.getCoordinate(), mapDetailsProvider.getName());
                }
            }
        });
        view.findViewById(R.id.txt_call).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapAddressDetailsCallback.onPhoneNumberClicked(mapDetailsProvider.getPhoneNumber());
            }
        });

        mapView = (MapView) view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                MapAddressDetailsFragment.this.googleMap = googleMap;
                MapAddressDetailsFragment.this.googleMap.getUiSettings().setScrollGesturesEnabled(false);
                if (activity.isPermissionGranted()) {
                    MapAddressDetailsFragment.this.googleMap.getUiSettings().setMyLocationButtonEnabled(false);
                    MapAddressDetailsFragment.this.googleMap.setMyLocationEnabled(false);
                    MapAddressDetailsFragment.this.location = null;
                    getLastKnownLocation();
                }
            }
        });
        setData();
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    /**
     * Here we set all information about the current route stop for the text field
     * we have the same text view for the address and comment so we need to check if this data
     * present before set it
     */
    private void setData() {
        txtName.setText(getResources().getString(R.string.task_details_go_to, mapDetailsProvider.getName()));
        setComment(mapDetailsProvider.getComment());
        setAddress(mapDetailsProvider.getAddress());
    }

    /**
     * Set comment data to the textView
     * if comment is empty hide the whole layout
     *
     * @param comment order comment
     */
    private void setComment(String comment) {
        if (TextUtils.isEmpty(comment)) {
            layoutComment.setVisibility(View.GONE);
        } else {
            txtComment.setText(comment);
        }
    }

    /**
     * Set address data to the textView
     * if address is empty hide the whole layout
     *
     * @param address order address
     */
    private void setAddress(String address) {
        if (TextUtils.isEmpty(address)) {
            layoutAddress.setVisibility(View.GONE);
        } else {
            txtAddress.setText(address);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }

    @Override
    public void onLocationChanged(Location location) {
        drawMarkers(location);
    }

    private void getLastKnownLocation() {
        LocationManager locationManager = (LocationManager) activity.getSystemService
            (Context.LOCATION_SERVICE);
        Location lastLocation = locationManager.getLastKnownLocation
            (LocationManager.PASSIVE_PROVIDER);
        if (lastLocation != null) {
            drawMarkers(lastLocation);
        }
    }

    private void drawMarkers(Location location) {
        if (googleMap == null || !isAdded()) {
            return;
        }

        googleMap.clear();
        LatLng riderLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        drawAccuracyCircleForRiderLocation(location, riderLatLng);

        List<Marker> markers = new LinkedList<>();

        markers.add(createRiderLocationMarker(riderLatLng));

        if (mapDetailsProvider.getCoordinate() != null) {
            markers.add(createDestinationPointMarker());
        }

        setBoundsForLocationAndDestinationPoints(markers);

        this.location = location;
    }

    /**
     * Create marker for destination point
     * with anchor to set proper position for this point in the center
     *
     * @return marker for point
     */
    private Marker createDestinationPointMarker() {
        LatLng pointLocation = new LatLng(mapDetailsProvider.getCoordinate().getLat(), mapDetailsProvider.getCoordinate().getLon());
        drawRadiusForClockIn(pointLocation);

        return googleMap.addMarker(new MarkerOptions().
            position(pointLocation).
            icon(BitmapDescriptorFactory.fromResource(getMarkerIconResource())).
            anchor(MARKER_ANCHOR, MARKER_ANCHOR).
            title(mapDetailsProvider.getName()));
    }

    /**
     * create marker for rider location
     *
     * @param riderLocationLatLng rider coordinates pair
     * @return marker with rider for rider location
     */
    private Marker createRiderLocationMarker(LatLng riderLocationLatLng) {
        Marker marker = googleMap.addMarker(new MarkerOptions().
            position(riderLocationLatLng).
            anchor(MARKER_ANCHOR, MARKER_ANCHOR).
            icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_rider_location)).
            title(getResources().getString(R.string.route_stop_details_my_location)));

        return marker;
    }

    /**
     * draw accuracy circle for rider location to show real range for location
     *
     * @param location    rider location
     * @param riderLatLng rider location coordinates pair
     */
    private void drawAccuracyCircleForRiderLocation(Location location, LatLng riderLatLng) {
        if (location.hasAccuracy()) {
            CircleOptions circleOptions = new CircleOptions();
            circleOptions.
                center(riderLatLng).
                radius(location.getAccuracy()).
                fillColor(ContextCompat.getColor(activity, R.color.location_radius_color)).
                strokeWidth(0);
            googleMap.addCircle(circleOptions);
        }
    }

    /**
     * To fill all space in the map we need to set bounds for all markers
     *
     * @param markers list of markers that should be filled in the map
     */
    private void setBoundsForLocationAndDestinationPoints(List<Marker> markers) {
        if (this.location == null) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (Marker m : markers) {
                builder.include(m.getPosition());
            }
            LatLngBounds bounds = builder.build();
            int padding = ContextCompat.getDrawable(activity, R.drawable.icon_pickup_circle).getIntrinsicHeight();

            googleMap.animateCamera(
                CameraUpdateFactory.newLatLngBounds(
                    bounds,
                    this.getResources().getDisplayMetrics().widthPixels,
                    this.getResources().getDimensionPixelOffset(R.dimen.map_height),
                    padding));
        }
    }

    /**
     * Depend on type of view the icon in the map should be different
     * and here we figure out what icon it would be
     *
     * @return link to drawable resource to icon
     */
    private int getMarkerIconResource() {
        switch (mapPointType) {
            case CLOCK_IN:
                return R.drawable.icon_start_circle;
            case PICK_UP:
                return R.drawable.icon_pickup_circle;
            case DELIVERY:
                return R.drawable.icon_deliver_circle;
            default:
                return R.drawable.icon_start_circle;
        }
    }

    /**
     * In clock-in screen rider should be in a radius of {@value #CLOCK_IN_RADIUS}
     * and we have to draw this circle in the map
     *
     * @param startingPointLocation coordinate of the staring point
     */
    private void drawRadiusForClockIn(LatLng startingPointLocation) {
        if (mapPointType == MapPointType.CLOCK_IN) {
            CircleOptions circleOptions = new CircleOptions();
            circleOptions.
                center(startingPointLocation).
                radius(CLOCK_IN_RADIUS).
                fillColor(ContextCompat.getColor(activity, R.color.location_radius_color)).
                strokeWidth(0);
            googleMap.addCircle(circleOptions);
        }
    }

    @Override
    public void setActionDoneCheckboxVisibility(boolean isVisible) {
        checkBoxDone.setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
    }
}
