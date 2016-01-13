package com.foodpanda.urbanninja.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.foodpanda.urbanninja.R;
import com.foodpanda.urbanninja.model.RouteStopAction;
import com.foodpanda.urbanninja.ui.adapter.RouteStopActionAdapter;
import com.foodpanda.urbanninja.ui.interfaces.MainActivityCallback;

import java.util.LinkedList;
import java.util.List;

public class RouteStopActionListFragment extends BaseListFragment<RouteStopActionAdapter> {
    private MainActivityCallback mainActivityCallback;

    private List<RouteStopAction> list = new LinkedList<>();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivityCallback = (MainActivityCallback) context;
    }

    public static RouteStopActionListFragment newInstance() {
        RouteStopActionListFragment fragment = new RouteStopActionListFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        for (int i = 0; i < 5; i++) {
            list.add(new RouteStopAction());
        }
    }

    @Override
    protected RouteStopActionAdapter provideListAdapter() {
        return new RouteStopActionAdapter(list, activity, mainActivityCallback);
    }

    @Override
    protected int provideListLayout() {
        return R.layout.base_list_fragment;
    }

    @Override
    public void onItemClick(View view, int position) {

    }
}
