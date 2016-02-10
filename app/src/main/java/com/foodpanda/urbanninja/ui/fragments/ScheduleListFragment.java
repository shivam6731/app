package com.foodpanda.urbanninja.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.foodpanda.urbanninja.App;
import com.foodpanda.urbanninja.Constants;
import com.foodpanda.urbanninja.R;
import com.foodpanda.urbanninja.api.BaseApiCallback;
import com.foodpanda.urbanninja.api.model.ErrorMessage;
import com.foodpanda.urbanninja.api.model.ScheduleCollectionWrapper;
import com.foodpanda.urbanninja.api.model.ScheduleWrapper;
import com.foodpanda.urbanninja.manager.ApiManager;
import com.foodpanda.urbanninja.ui.adapter.ScheduleAdapter;
import com.foodpanda.urbanninja.ui.interfaces.MainActivityCallback;

import java.util.ArrayList;

public class ScheduleListFragment extends BaseListFragment<ScheduleAdapter> implements BaseApiCallback<ScheduleCollectionWrapper> {
    private ApiManager apiManager;
    private boolean isVisible;
    private MainActivityCallback mainActivityCallback;

    public static ScheduleListFragment newInstance(boolean isVisible) {
        ScheduleListFragment fragment = new ScheduleListFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constants.BundleKeys.VISIBILITY, isVisible);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivityCallback = (MainActivityCallback) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiManager = App.API_MANAGER;
        isVisible = getArguments().getBoolean(Constants.BundleKeys.VISIBILITY);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        apiManager.getScheduleList(this);
    }

    @Override
    public void onDestroyView() {
        mainActivityCallback.changeActionButtonVisibility(isVisible);
        super.onDestroyView();
    }

    @Override
    protected ScheduleAdapter provideListAdapter() {
        return new ScheduleAdapter(new ArrayList<ScheduleWrapper>(), activity);
    }

    @Override
    protected int provideListLayout() {
        return R.layout.schedule_list_fragment;
    }

    @Override
    public void onItemClick(View view, int position) {

    }

    @Override
    public void onSuccess(ScheduleCollectionWrapper scheduleWrappers) {
        adapter.addAll(scheduleWrappers);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onError(ErrorMessage errorMessage) {
        activity.onError(errorMessage.getStatus(), errorMessage.getMessage());
    }
}