package com.foodpanda.urbanninja.ui.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.foodpanda.urbanninja.R;
import com.foodpanda.urbanninja.api.model.RouteWrapper;
import com.foodpanda.urbanninja.ui.interfaces.PermissionAccepted;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.joda.time.DateTime;

public class PickUpFragment extends BaseTimerFragment implements
    OnMapReadyCallback,
    GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener,
    PermissionAccepted,
    LocationListener {

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 100;
    private static final int DEFAULT_ZOOM_LEVEL = 15;

    private TextView txtDetails;
    private TextView txtEndPoint;
    private TextView txtTimer;
    private TextView txtTimerDescription;

    private GoogleMap googleMap;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;

    private RouteWrapper routeWrapper;
    private Location location;

    public static PickUpFragment newInstance(RouteWrapper routeWrapper) {
        PickUpFragment pickUpFragment = new PickUpFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(RouteWrapper.class.getSimpleName(), routeWrapper);
        pickUpFragment.setArguments(bundle);
        return pickUpFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        routeWrapper = getArguments().getParcelable(RouteWrapper.class.getSimpleName());

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

    @Nullable
    @Override
    public View onCreateView(
        LayoutInflater inflater,
        ViewGroup container,
        Bundle savedInstanceState
    ) {

        return inflater.inflate(R.layout.task_details_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        txtDetails = (TextView) view.findViewById(R.id.txt_details);
        txtTimer = (TextView) view.findViewById(R.id.txt_timer);
        txtEndPoint = (TextView) view.findViewById(R.id.txt_end_point);
        txtTimerDescription = (TextView) view.findViewById(R.id.txt_minutes_left);
        SupportMapFragment mapFragment =
            (SupportMapFragment) getChildFragmentManager().
                findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
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
        return new DateTime();
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
        LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());

        googleMap.addMarker(new MarkerOptions().
            position(myLocation).
            icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_rider)).
            title(getResources().getString(R.string.pick_up_my_location)));
        if (this.location == null) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, DEFAULT_ZOOM_LEVEL));
        }
        this.location = location;

        LatLng pointLocation = new LatLng(10, 30);

        googleMap.addMarker(new MarkerOptions().
            position(pointLocation).
            icon(BitmapDescriptorFactory.fromResource(R.drawable.pin)).
            title("Restaurant"));
    }
}
