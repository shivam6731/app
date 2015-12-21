package com.foodpanda.urbanninja.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.foodpanda.urbanninja.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Date;

public class TaskDetailsFragment extends BaseTimerFragment implements OnMapReadyCallback {

    private TextView txtDetails;
    private TextView txtEndPoint;
    private TextView txtTimer;
    private TextView txtTimerDescription;

    public static TaskDetailsFragment newInstance() {
        TaskDetailsFragment taskDetailsFragment = new TaskDetailsFragment();

        return taskDetailsFragment;
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
    protected Date provideScheduleDate() {
        return new Date();
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
        LatLng sydney = new LatLng(-34, 151);
        googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}
