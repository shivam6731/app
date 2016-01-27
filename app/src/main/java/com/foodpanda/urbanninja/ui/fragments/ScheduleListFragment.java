package com.foodpanda.urbanninja.ui.fragments;

import android.os.Bundle;
import android.view.View;

import com.foodpanda.urbanninja.App;
import com.foodpanda.urbanninja.R;
import com.foodpanda.urbanninja.api.BaseApiCallback;
import com.foodpanda.urbanninja.api.model.ErrorMessage;
import com.foodpanda.urbanninja.api.model.ScheduleCollectionWrapper;
import com.foodpanda.urbanninja.api.model.ScheduleWrapper;
import com.foodpanda.urbanninja.manager.ApiManager;
import com.foodpanda.urbanninja.ui.adapter.ScheduleAdapter;

import java.util.ArrayList;

public class ScheduleListFragment extends BaseListFragment<ScheduleAdapter> implements BaseApiCallback<ScheduleCollectionWrapper> {
    private ApiManager apiManager;

    public static ScheduleListFragment newInstance() {
        ScheduleListFragment fragment = new ScheduleListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiManager = App.API_MANAGER;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        apiManager.getScheduleList(this);
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
