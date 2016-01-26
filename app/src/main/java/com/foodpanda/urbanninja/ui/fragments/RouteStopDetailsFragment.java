package com.foodpanda.urbanninja.ui.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.foodpanda.urbanninja.App;
import com.foodpanda.urbanninja.R;
import com.foodpanda.urbanninja.manager.StorageManager;
import com.foodpanda.urbanninja.model.Stop;
import com.foodpanda.urbanninja.ui.interfaces.PermissionAccepted;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
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

public class RouteStopDetailsFragment extends BaseTimerFragment implements
    OnMapReadyCallback,
    GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener,
    PermissionAccepted,
    LocationListener {

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 100;

    private TextView txtDetails;
    private TextView txtEndPoint;
    private TextView txtTimer;
    private TextView txtTimerDescription;

    private GoogleMap googleMap;
    private MapView mapView;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private StorageManager storageManager;

    private Stop stop;
    private Location location;

    public static RouteStopDetailsFragment newInstance() {
        return new RouteStopDetailsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storageManager = App.STORAGE_MANAGER;

        if (storageManager.getStopList().size() > 0) {
            stop = storageManager.getStopList().get(0);
        }

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(activity)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

            locationRequest = new LocationRequest();
            locationRequest.setInterval(10000);
            locationRequest.setFastestInterval(5000);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        }
    }

    public void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    public void onStop() {
        googleApiClient.disconnect();
        super.onStop();
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
        googleMap = mapView.getMap();
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        googleMap.setMyLocationEnabled(false);

        setData();
    }

    private void setData() {
        txtEndPoint.setText(stop.getAddress());
        txtDetails.setText(detailsText());
    }

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
    protected TextView provideTimerTextView() {
        return txtTimer;
    }

    @Override
    protected TextView provideTimerDescriptionTextView() {
        return txtTimerDescription;
    }

    @Override
    protected DateTime provideScheduleDate() {
        return stop.getArrivalTime();
    }

    @Override
    protected DateTime provideScheduleEndDate() {
        return stop.getArrivalTime().plusDays(1);
    }

    @Override
    protected String provideLeftString() {
        return getResources().getString(R.string.task_details_time_left);
    }

    @Override
    protected String providePassedString() {
        return getResources().getString(R.string.task_details_time_passed);
    }

    @Override
    protected int provideActionButtonString() {
        return 0;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        askForPermissions();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(getContext(), connectionResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        drawMarkers(location);
    }

    @Override
    public void onPermissionAccepted() {
        askForPermissions();
    }

    private void askForPermissions() {
        if (ContextCompat.checkSelfPermission(activity,
            Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                MY_PERMISSIONS_REQUEST_LOCATION);
        } else {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                googleApiClient, locationRequest, this);
        }
    }

    private void drawMarkers(Location location) {
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
                fillColor(getResources().getColor(R.color.location_radius_color)).
                strokeColor(getResources().getColor(R.color.location_radius_border_color)).
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
            int padding = getResources().getDrawable(R.drawable.pin).getIntrinsicHeight();

            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
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
