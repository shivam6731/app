package com.foodpanda.urbanninja.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.foodpanda.urbanninja.R;

public class ReadyToWorkFragment extends BaseTimerFragment {
    private TextView txtStartPoint;
    private TextView txtTimer;

    public static ReadyToWorkFragment newInstance() {
        ReadyToWorkFragment readyToWorkFragment = new ReadyToWorkFragment();

        return readyToWorkFragment;
    }

    @Nullable
    @Override
    public View onCreateView(
        LayoutInflater inflater,
        ViewGroup container,
        Bundle savedInstanceState
    ) {

        return inflater.inflate(R.layout.ready_to_work_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        txtStartPoint = (TextView) view.findViewById(R.id.txt_start_point);
        txtTimer = (TextView) view.findViewById(R.id.txt_timer);
        view.findViewById(R.id.btn_see_map).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(activity, "btn_see_map", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected TextView provideTimerTextView() {
        return txtTimer;
    }
}
