package com.foodpanda.urbanninja.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.foodpanda.urbanninja.R;
import com.foodpanda.urbanninja.model.Stop;
import com.foodpanda.urbanninja.ui.adapter.StopAdapter;
import com.foodpanda.urbanninja.ui.interfaces.MainActivityCallback;

import java.util.LinkedList;
import java.util.List;

public class StopsListFragment extends BaseListFragment<StopAdapter> {
    private MainActivityCallback mainActivityCallback;

    private List<Stop> list = new LinkedList<>();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivityCallback = (MainActivityCallback) context;
    }

    public static StopsListFragment newInstance() {
        StopsListFragment fragment = new StopsListFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        for (int i = 0; i < 5; i++) {
            list.add(new Stop());
        }
    }

    @Override
    protected StopAdapter provideListAdapter() {
        return new StopAdapter(list, activity, mainActivityCallback);
    }

    @Override
    protected int provideListLayout() {
        return R.layout.stop_list_fragment;
    }

    @Override
    public void onItemClick(View view, int position) {

    }
}
