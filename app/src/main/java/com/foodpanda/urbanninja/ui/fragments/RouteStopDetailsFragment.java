package com.foodpanda.urbanninja.ui.fragments;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.foodpanda.urbanninja.Constants;
import com.foodpanda.urbanninja.R;
import com.foodpanda.urbanninja.model.Stop;
import com.foodpanda.urbanninja.ui.interfaces.LocationChangedCallback;
import com.foodpanda.urbanninja.ui.interfaces.NestedFragmentCallback;
import com.foodpanda.urbanninja.ui.interfaces.TimerDataProvider;
import com.foodpanda.urbanninja.ui.util.TimerHelper;
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

import org.joda.time.DateTime;

import java.util.LinkedList;
import java.util.List;

public class RouteStopDetailsFragment extends BaseFragment implements
    OnMapReadyCallback,
    LocationChangedCallback,
    TimerDataProvider {

    private NestedFragmentCallback nestedFragmentCallback;
    private TimerHelper timerHelper;

    private TextView txtDetails;
    private TextView txtEndPoint;
    private TextView txtTimer;
    private TextView txtTimerDescription;

    private SwipeRefreshLayout swipeRefreshLayout;

    private GoogleMap googleMap;
    private MapView mapView;

    private Stop stop;
    private Location location;

    public static RouteStopDetailsFragment newInstance(Stop stop) {
        RouteStopDetailsFragment routeStopDetailsFragment = new RouteStopDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BundleKeys.STOP, stop);
        routeStopDetailsFragment.setArguments(bundle);

        return routeStopDetailsFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        nestedFragmentCallback = (NestedFragmentCallback) getParentFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stop = getArguments().getParcelable(Constants.BundleKeys.STOP);
        timerHelper = new TimerHelper(activity, this, this);
    }

    @Nullable
    @Override
    public View onCreateView(
        LayoutInflater inflater,
        ViewGroup container,
        Bundle savedInstanceState
    ) {

        return inflater.inflate(R.layout.route_stop_details_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        txtDetails = (TextView) view.findViewById(R.id.txt_details);
        txtTimer = (TextView) view.findViewById(R.id.txt_timer);
        txtEndPoint = (TextView) view.findViewById(R.id.txt_end_point);
        txtTimerDescription = (TextView) view.findViewById(R.id.txt_minutes_left);

        mapView = (MapView) view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                RouteStopDetailsFragment.this.googleMap = googleMap;
                RouteStopDetailsFragment.this.googleMap.getUiSettings().setMyLocationButtonEnabled(false);
                RouteStopDetailsFragment.this.googleMap.setMyLocationEnabled(false);
                RouteStopDetailsFragment.this.location = null;
                getLastKnownLocation();
            }
        });
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_to_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                nestedFragmentCallback.onRefresh();
            }
        });

        setData();
    }

    @Override
    public void onStart() {
        super.onStart();
        timerHelper.setTimer();
    }

    @Override
    public void onStop() {
        super.onStop();
        timerHelper.stopTimer();
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
        String text = "";

        if (!TextUtils.isEmpty(stop.getAddress())) {
            text += stop.getAddress();
        }
        if (!TextUtils.isEmpty(stop.getComment())) {
            text += getResources().getString(R.string.task_details_comment, stop.getComment());
        }

        txtEndPoint.setText(text);
        txtDetails.setText(detailsText());
    }

    private void getLastKnownLocation() {
        if (activity.isPermissionGranted()) {
            LocationManager locationManager = (LocationManager) activity.getSystemService
                (Context.LOCATION_SERVICE);
            Location lastLocation = locationManager.getLastKnownLocation
                (LocationManager.PASSIVE_PROVIDER);
            if (lastLocation != null) {
                drawMarkers(lastLocation);
            }
        }
    }

    /**
     * Set info about about destination point
     * to the Spanned and depends on what type of task it is
     * the description would be "delivery" or "pickup"
     *
     * @return text for the TextView
     */
    private Spanned detailsText() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getResources().getString(R.string.task_details_go_to));
        stringBuilder.append(" <b>");
        stringBuilder.append(stop.getName());
        stringBuilder.append("</b> ");
        stringBuilder.append(getResources().getString(R.string.task_details_to));
        stringBuilder.append(" <b>");
        stringBuilder.append(getStatusString());
        stringBuilder.append("</b>");
        return Html.fromHtml(stringBuilder.toString());
    }

    private String getStatusString() {
        switch (stop.getTask()) {
            case DELIVER:
                return getResources().getString(R.string.task_details_delivery);
            case PICKUP:
                return getResources().getString(R.string.task_details_pick_up);
            default:
                return getResources().getString(R.string.task_details_pick_up);
        }
    }

    @Override
    public TextView provideTimerTextView() {
        return txtTimer;
    }

    @Override
    public TextView provideTimerDescriptionTextView() {
        return txtTimerDescription;
    }

    @Override
    public DateTime provideScheduleDate() {
        return stop.getArrivalTime();
    }

    @Override
    public DateTime provideScheduleEndDate() {
        return stop.getArrivalTime().plusDays(1);
    }

    @Override
    public String provideLeftString() {
        return getResources().getString(R.string.task_details_time_left);
    }

    @Override
    public String providePassedString() {
        return getResources().getString(R.string.task_details_time_passed);
    }

    @Override
    public int provideActionButtonString() {
        return 0;
    }

    @Override
    public String provideExpireString() {
        return getResources().getString(R.string.action_order_expired);
    }

    @Override
    public String provideFutureString() {
        return getResources().getString(R.string.action_order_in_future);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }

    @Override
    public void onLocationChanged(Location location) {
        drawMarkers(location);
    }

    public void refreshComplete() {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void drawMarkers(Location location) {
        if (googleMap == null || !isAdded()) {
            return;
        }

        googleMap.clear();
        List<Marker> markers = new LinkedList<>();
        LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());

        Marker marker = googleMap.addMarker(new MarkerOptions().
            position(myLocation).
            anchor(0.5f, 0.5f).
            icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_rider)).
            title(getResources().getString(R.string.route_stop_details_my_location)));

        if (location.hasAccuracy()) {
            CircleOptions circleOptions = new CircleOptions();
            circleOptions.
                center(myLocation).
                radius(location.getAccuracy()).
                fillColor(ContextCompat.getColor(activity, R.color.location_radius_color)).
                strokeColor(ContextCompat.getColor(activity, R.color.location_radius_border_color)).
                strokeWidth(getResources().getDimension(R.dimen.margin_tiny_tiny));
            googleMap.addCircle(circleOptions);
        }

        markers.add(marker);
        if (stop.getGps() != null) {
            markers.add(drawPointMarker());
        }

        if (this.location == null) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (Marker m : markers) {
                builder.include(m.getPosition());
            }
            LatLngBounds bounds = builder.build();
            int padding = ContextCompat.getDrawable(activity, R.drawable.pin).getIntrinsicHeight();

            googleMap.animateCamera(
                CameraUpdateFactory.newLatLngBounds(
                    bounds,
                    this.getResources().getDisplayMetrics().widthPixels,
                    this.getResources().getDimensionPixelOffset(R.dimen.map_height),
                    padding));
        }
        this.location = location;
    }

    private Marker drawPointMarker() {
        LatLng pointLocation = new LatLng(stop.getGps().getLat(), stop.getGps().getLon());

        return googleMap.addMarker(new MarkerOptions().
            position(pointLocation).
            anchor(1.0f, 0.5f).
            icon(BitmapDescriptorFactory.fromResource(R.drawable.pin)).
            title(stop.getName()));
    }

}
