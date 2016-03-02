package com.foodpanda.urbanninja.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.foodpanda.urbanninja.Constants;
import com.foodpanda.urbanninja.R;
import com.foodpanda.urbanninja.model.Stop;
import com.foodpanda.urbanninja.ui.adapter.RouteStopActionAdapter;
import com.foodpanda.urbanninja.ui.interfaces.NestedFragmentCallback;

public class RouteStopActionListFragment extends BaseListFragment<RouteStopActionAdapter> {
    private NestedFragmentCallback nestedFragmentCallback;
    private Stop currentStop;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        nestedFragmentCallback = (NestedFragmentCallback) getParentFragment();
    }

    public static RouteStopActionListFragment newInstance(Stop stop) {
        RouteStopActionListFragment fragment = new RouteStopActionListFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BundleKeys.STOP, stop);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentStop = getArguments().getParcelable(Constants.BundleKeys.STOP);
    }

    @Override
    protected RouteStopActionAdapter provideListAdapter() {
        return new RouteStopActionAdapter(currentStop, activity, nestedFragmentCallback);
    }

    @Override
    protected int provideListLayout() {
        return R.layout.base_list_fragment;
    }

    @Override
    public void onItemClick(View view, int position) {

    }
}
