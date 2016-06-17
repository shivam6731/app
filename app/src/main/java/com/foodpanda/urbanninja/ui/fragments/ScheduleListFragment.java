package com.foodpanda.urbanninja.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.foodpanda.urbanninja.App;
import com.foodpanda.urbanninja.R;
import com.foodpanda.urbanninja.api.BaseApiCallback;
import com.foodpanda.urbanninja.api.model.ErrorMessage;
import com.foodpanda.urbanninja.api.model.ScheduleCollectionWrapper;
import com.foodpanda.urbanninja.manager.ApiManager;
import com.foodpanda.urbanninja.ui.adapter.ScheduleAdapter;
import com.foodpanda.urbanninja.ui.interfaces.MainActivityCallback;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.util.ArrayList;

public class ScheduleListFragment extends BaseListFragment<ScheduleAdapter> implements BaseApiCallback<ScheduleCollectionWrapper> {
    private MainActivityCallback mainActivityCallback;
    private ApiManager apiManager;

    public static ScheduleListFragment newInstance() {
        ScheduleListFragment fragment = new ScheduleListFragment();

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
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        apiManager.getScheduleList(this);
        activity.showProgress();
        recyclerView.addItemDecoration(new StickyRecyclerHeadersDecoration(adapter));
        mainActivityCallback.writeFragmentTitle(getResources().getString(R.string.side_menu_schedule));
    }

    @Override
    protected ScheduleAdapter provideListAdapter() {
        return new ScheduleAdapter(new ArrayList<>(), activity);
    }

    @Override
    protected int provideListLayout() {
        return R.layout.base_list_fragment;
    }

    @Override
    protected CharSequence provideEmptyListDescription() {
        return getResources().getText(R.string.empty_list_schedule);
    }

    @Override
    public void onItemClick(View view, int position) {

    }

    @Override
    public void onSuccess(ScheduleCollectionWrapper scheduleWrappers) {
        adapter.addAll(scheduleWrappers);
        adapter.notifyDataSetChanged();
        activity.hideProgress();
    }

    @Override
    public void onError(ErrorMessage errorMessage) {
        activity.onError(errorMessage.getStatus(), errorMessage.getMessage());
    }
}
