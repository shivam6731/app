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
import android.view.ViewTreeObserver;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.foodpanda.urbanninja.Constants;
import com.foodpanda.urbanninja.R;
import com.foodpanda.urbanninja.model.Stop;
import com.foodpanda.urbanninja.model.enums.Action;
import com.foodpanda.urbanninja.model.enums.RouteStopTaskStatus;
import com.foodpanda.urbanninja.ui.interfaces.DrivingHereCallback;
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
    TimerDataProvider,
    DrivingHereCallback {

    private NestedFragmentCallback nestedFragmentCallback;
    private TimerHelper timerHelper;

    private TextView txtType;
    private TextView txtName;
    private TextView txtAddress;
    private TextView txtComment;
    private TextView txtTimer;
    private CheckBox checkBoxDone;

    private LinearLayout layoutComment;
    private LinearLayout layoutAddress;

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
        txtName = (TextView) view.findViewById(R.id.txt_name);
        txtType = (TextView) view.findViewById(R.id.txt_type);
        txtTimer = (TextView) view.findViewById(R.id.txt_timer);
        txtComment = (TextView) view.findViewById(R.id.txt_comment);
        txtAddress = (TextView) view.findViewById(R.id.txt_address);

        layoutAddress = (LinearLayout) view.findViewById(R.id.layout_address);
        layoutComment = (LinearLayout) view.findViewById(R.id.layout_comment);

        checkBoxDone = (CheckBox) view.findViewById(R.id.checkbox_done);
        checkBoxDone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (nestedFragmentCallback != null) {
                    nestedFragmentCallback.setEnableActionButton(isChecked);
                }
            }
        });

        changeActionDoneCheckboxVisibility(stop.getStatus() == Action.ON_THE_WAY);

        FloatingActionButton floatingActionButton = (FloatingActionButton) view.findViewById(R.id.floating_button_map);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (nestedFragmentCallback != null) {
                    nestedFragmentCallback.onSeeMapClicked(stop.getGps(), stop.getName());
                }
            }
        });
        view.findViewById(R.id.txt_call).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(activity, "Open map", Toast.LENGTH_LONG).show();
            }
        });

        mapView = (MapView) view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                RouteStopDetailsFragment.this.googleMap = googleMap;
                RouteStopDetailsFragment.this.googleMap.getUiSettings().setScrollGesturesEnabled(false);
                if (activity.isPermissionGranted()) {
                    RouteStopDetailsFragment.this.googleMap.getUiSettings().setMyLocationButtonEnabled(false);
                    RouteStopDetailsFragment.this.googleMap.setMyLocationEnabled(false);
                    RouteStopDetailsFragment.this.location = null;
                    getLastKnownLocation();
                }
            }
        });
        final ScrollView scrollView = (ScrollView) view.findViewById(R.id.scroll_view);
        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                // this fragment in a child view for some main container with
                // swipe to refresh logic inside, however here we have own scroll and to get rid of scroll conflict
                // we make main swipe view disable until we reach the top of the view here
                if (nestedFragmentCallback != null) {
                    nestedFragmentCallback.setSwipeToRefreshEnable(scrollView.getScrollY() == 0);
                }
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
        enableSwipeToRefreshDeleteCallback();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
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
        txtName.setText(getResources().getString(R.string.task_details_go_to, stop.getName()));
        setType(stop.getTask());
        setComment(stop.getComment());
        setAddress(stop.getAddress());
    }

    /**
     * Put the icon and description for type textView
     *
     * @param task type of order
     */
    private void setType(RouteStopTaskStatus task) {
        int resource = task == RouteStopTaskStatus.PICKUP ? R.string.route_action_pick_up : R.string.route_action_deliver;
        txtType.setText(getResources().getText(resource));

        //TODO add icon
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

    private void getLastKnownLocation() {
        LocationManager locationManager = (LocationManager) activity.getSystemService
            (Context.LOCATION_SERVICE);
        Location lastLocation = locationManager.getLastKnownLocation
            (LocationManager.PASSIVE_PROVIDER);
        if (lastLocation != null) {
            drawMarkers(lastLocation);
        }
    }

    /**
     * Main main container swipe view enable and delete callback
     * We need to delete callback because {@link ViewTreeObserver.OnScrollChangedListener}
     * calls after {@link #onDestroy()} called and it means that we would have a wrong state for swipe view
     */
    private void enableSwipeToRefreshDeleteCallback() {
        if (nestedFragmentCallback != null) {
            nestedFragmentCallback.setSwipeToRefreshEnable(true);
            nestedFragmentCallback = null;
        }
    }

    @Override
    public TextView provideTimerTextView() {
        return txtTimer;
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
            icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_rider_location)).
            title(getResources().getString(R.string.route_stop_details_my_location)));

        if (location.hasAccuracy()) {
            CircleOptions circleOptions = new CircleOptions();
            circleOptions.
                center(myLocation).
                radius(location.getAccuracy()).
                fillColor(ContextCompat.getColor(activity, R.color.location_radius_color)).
                strokeWidth(0);
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
            int padding = ContextCompat.getDrawable(activity, R.drawable.icon_pickup_circle).getIntrinsicHeight();

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
            icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_pickup_circle)).
            title(stop.getName()));
    }

    @Override
    public void changeActionDoneCheckboxVisibility(boolean isVisible) {
        checkBoxDone.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }
}
