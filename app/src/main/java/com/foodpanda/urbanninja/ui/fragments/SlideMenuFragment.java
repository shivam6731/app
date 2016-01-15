package com.foodpanda.urbanninja.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.foodpanda.urbanninja.R;
import com.foodpanda.urbanninja.ui.interfaces.SlideMenuCallback;

public class SlideMenuFragment extends BaseFragment {
    private SlideMenuCallback mainActivityCallback;

    public static SlideMenuFragment newInstance() {
        SlideMenuFragment loginFragment = new SlideMenuFragment();

        return loginFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivityCallback = (SlideMenuCallback) context;
    }

    @Nullable
    @Override
    public View onCreateView(
        LayoutInflater inflater,
        ViewGroup container,
        Bundle savedInstanceState
    ) {

        return inflater.inflate(R.layout.slide_menu_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.btn_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivityCallback.onLogoutClicked();
            }
        });
        view.findViewById(R.id.btn_profile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(activity, "Profile Clicked", Toast.LENGTH_SHORT).show();
            }
        });
        view.findViewById(R.id.btn_settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(activity, "Settings Clicked", Toast.LENGTH_SHORT).show();
            }
        });
        view.findViewById(R.id.btn_schedule).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivityCallback.onScheduleClicked();
            }
        });
    }

}
