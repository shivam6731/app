package com.foodpanda.urbanninja.ui.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
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

    private static final int POSITION_VENDOR_NAME_PICK_UP = 2;
    private static final int POSITION_VENDOR_NAME_DELIVERY = 3;

    private TextView txtName;
    private TextView txtAddress;
    private TextView txtComment;
    private CheckBox checkBoxDone;
    private CardView cardView;

    private LinearLayout layoutAllContent;
    private LinearLayout layoutComment;
    private LinearLayout layoutAddress;

    private GoogleMap googleMap;
    private MapView mapView;

    private MapAddressDetailsCallback mapAddressDetailsCallback;

    private MapDetailsProvider mapDetailsProvider;
    private boolean isRouteDetailsShown;
    private boolean isTopPanelShown;
    private MapPointType mapPointType;

    public static MapAddressDetailsFragment newInstance(MapDetailsProvider mapDetailsProvider, MapPointType mapPointType) {
        return newInstance(mapDetailsProvider, mapPointType, true);
    }

    public static MapAddressDetailsFragment newInstance(MapDetailsProvider mapDetailsProvider, MapPointType mapPointType, boolean isRouteDetailsShouldBeShown) {
        return newInstance(mapDetailsProvider, mapPointType, isRouteDetailsShouldBeShown, true);
    }

    public static MapAddressDetailsFragment newInstance(
        MapDetailsProvider mapDetailsProvider,
        MapPointType mapPointType,
        boolean isMapActionShown,
        boolean isTopPanelShown) {
        Bundle bundle = new Bundle();

        bundle.putParcelable(Constants.BundleKeys.MAP_ADDRESS_DETAILS, mapDetailsProvider);
        bundle.putSerializable(Constants.BundleKeys.MAP_ADDRESS_POINT_TYPE, mapPointType);
        bundle.putBoolean(Constants.BundleKeys.IS_ROUTE_DETAILS_SHOWN, isMapActionShown);
        bundle.putBoolean(Constants.BundleKeys.MAP_TOP_LAYOUT_SHOWN, isTopPanelShown);

        MapAddressDetailsFragment fragment = new MapAddressDetailsFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getParentFragment() instanceof MapAddressDetailsCallback) {
            mapAddressDetailsCallback = (MapAddressDetailsCallback) getParentFragment();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mapDetailsProvider = getArguments().getParcelable(Constants.BundleKeys.MAP_ADDRESS_DETAILS);
        mapPointType = (MapPointType) getArguments().getSerializable(Constants.BundleKeys.MAP_ADDRESS_POINT_TYPE);
        isRouteDetailsShown = getArguments().getBoolean(Constants.BundleKeys.IS_ROUTE_DETAILS_SHOWN);
        isTopPanelShown = getArguments().getBoolean(Constants.BundleKeys.MAP_TOP_LAYOUT_SHOWN);
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

        layoutAllContent = (LinearLayout) view.findViewById(R.id.layout_all_content);
        layoutAddress = (LinearLayout) view.findViewById(R.id.layout_address);
        layoutComment = (LinearLayout) view.findViewById(R.id.layout_comment);
        cardView = (CardView) view.findViewById(R.id.card_view);

        RelativeLayout layoutMapActions = (RelativeLayout) view.findViewById(R.id.map_actions_layout);
        layoutMapActions.setVisibility(isRouteDetailsShown ? View.VISIBLE : View.GONE);

        txtName.setVisibility(isTopPanelShown ? View.VISIBLE : View.GONE);
        hidePaddingAndElevationIfNeeds();

        checkBoxDone = (CheckBox) view.findViewById(R.id.checkbox_done);
        checkBoxDone.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (mapAddressDetailsCallback != null) {
                mapAddressDetailsCallback.setActionButtonVisible(isChecked);
            }
        });
        setActionDoneCheckboxVisibility(mapDetailsProvider.isDoneButtonVisible());

        FloatingActionButton floatingActionButton = (FloatingActionButton) view.findViewById(R.id.floating_button_map);
        floatingActionButton.setOnClickListener(view1 -> {
            if (mapAddressDetailsCallback != null) {
                mapAddressDetailsCallback.onSeeMapClicked(mapDetailsProvider.getCoordinate(), mapDetailsProvider.getName());
            }
        });
        view.findViewById(R.id.txt_call).setOnClickListener(view1 -> {
            if (mapAddressDetailsCallback != null) {
                mapAddressDetailsCallback.onPhoneNumberClicked(mapDetailsProvider.getPhoneNumber());
            }
        });

        mapView = (MapView) view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this::setGoogleMapData);
        setData();
    }

    /**
     * In case when this map address fragments uses as additional information
     * we have to hide padding and shadows for the whole card view
     */
    private void hidePaddingAndElevationIfNeeds() {
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);

        cardView.setUseCompatPadding(isTopPanelShown);
        if (isTopPanelShown) {
            cardView.setCardElevation(getResources().getDimension(R.dimen.margin_default));
            layoutParams.setMargins(
                getResources().getDimensionPixelSize(R.dimen.margin_default),
                getResources().getDimensionPixelSize(R.dimen.margin_default),
                getResources().getDimensionPixelSize(R.dimen.margin_default),
                getResources().getDimensionPixelSize(R.dimen.margin_default));
        } else {
            cardView.setCardElevation(0);
            layoutParams.setMargins(
                getResources().getDimensionPixelSize(R.dimen.margin_tiny),
                getResources().getDimensionPixelSize(R.dimen.margin_tiny),
                getResources().getDimensionPixelSize(R.dimen.margin_tiny),
                getResources().getDimensionPixelSize(R.dimen.margin_tiny));
        }
        cardView.setLayoutParams(layoutParams);
    }

    private void setGoogleMapData(GoogleMap googleMap) {
        MapAddressDetailsFragment.this.googleMap = googleMap;

        //this change is not regarding to the user permission and map should be not clickable even if
        //user denied permissions
        MapAddressDetailsFragment.this.googleMap.getUiSettings().setAllGesturesEnabled(false);
        MapAddressDetailsFragment.this.googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        MapAddressDetailsFragment.this.googleMap.setOnMarkerClickListener(marker -> {
            //to disable all marker default actions we should return true here
            return true;
        });

        getLastKnownLocation();
    }

    @Override
    public void onResume() {
        if (mapView != null) {
            mapView.onResume();
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mapView != null) {
            mapView.onPause();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mapView != null) {
            mapView.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onDestroy() {
        if (mapView != null) {
            mapView.onDestroy();
        }
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        if (mapView != null) {
            mapView.onLowMemory();
        }
        super.onLowMemory();
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
        showVendorName();
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

    /**
     * The vendor name is required field for route details information screen
     * In case when vendor name is long and needs more then one line
     * we show this details in a separate details layout
     * when type is #DELIVERY this name should be below the map
     * if type is #PICK_UP the name should be the last item
     */
    private void showVendorName() {
        if (isRouteDetailsShown && !TextUtils.isEmpty(mapDetailsProvider.getVendorName())) {
            LinearLayout vendorNameLayout = (LinearLayout)
                View.inflate(activity, R.layout.map_address_vendor_name_layout, null);

            TextView txtVendorName = (TextView) vendorNameLayout.findViewById(R.id.txt_vendor_name);
            TextView txtVendorNameTitle = (TextView) vendorNameLayout.findViewById(R.id.txt_vendor_name_title);

            txtVendorName.setText(mapDetailsProvider.getVendorName());

            switch (mapPointType) {
                case DELIVERY:
                    txtVendorNameTitle.setText(getString(R.string.task_details_vendor_name_at_customer));
                    layoutAllContent.addView(vendorNameLayout, POSITION_VENDOR_NAME_DELIVERY);
                    break;
                case PICK_UP:
                    txtVendorNameTitle.setText(getString(R.string.task_details_vendor_name));
                    layoutAllContent.addView(vendorNameLayout, POSITION_VENDOR_NAME_PICK_UP);
                    break;
            }
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

    /**
     * Get last known location of the use just to show
     * to up to date information before receive the data from GPS
     */
    private void getLastKnownLocation() {
        if (checkPermission()) {
            //we need to set this value to false only with location permission
            googleMap.setMyLocationEnabled(false);

            LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
            Location lastLocation = locationManager.getLastKnownLocation
                (LocationManager.PASSIVE_PROVIDER);
            if (lastLocation != null) {
                drawMarkers(lastLocation);
            }
        }
    }

    /**
     * we have such method in {@link com.foodpanda.urbanninja.ui.activity.BaseActivity},
     * however if we use it from there android lint has an error about permission
     * so to get rid of it we have to copy this method from activity
     *
     * @return is permissions accepted
     */
    private boolean checkPermission() {
        return ContextCompat.checkSelfPermission(activity,
            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Draw current user marker and destination point marker
     * Moreover set a bound to fix this two points in a best way
     *
     * @param location user location
     */
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

        return googleMap.addMarker(new MarkerOptions().
            position(riderLocationLatLng).
            anchor(MARKER_ANCHOR, MARKER_ANCHOR).
            icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_rider_location)).
            title(getResources().getString(R.string.route_stop_details_my_location)));
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
                padding)
        );
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
