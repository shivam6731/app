package com.foodpanda.urbanninja.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.foodpanda.urbanninja.App;
import com.foodpanda.urbanninja.R;
import com.foodpanda.urbanninja.api.BaseApiCallback;
import com.foodpanda.urbanninja.api.model.ErrorMessage;
import com.foodpanda.urbanninja.api.model.RouteWrapper;
import com.foodpanda.urbanninja.manager.ApiManager;
import com.foodpanda.urbanninja.model.VehicleDeliveryAreaRiderBundle;

public class EmptyTaskListFragment extends BaseFragment implements BaseApiCallback<RouteWrapper> {
    private ApiManager apiManager;
    private SwipeRefreshLayout swipeRefreshLayout;

    private VehicleDeliveryAreaRiderBundle vehicleDeliveryAreaRiderBundle;

    public static EmptyTaskListFragment newInstance(VehicleDeliveryAreaRiderBundle vehicleDeliveryAreaRiderBundle) {
        EmptyTaskListFragment emptyTaskListFragment = new EmptyTaskListFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(VehicleDeliveryAreaRiderBundle.class.getSimpleName(), vehicleDeliveryAreaRiderBundle);
        emptyTaskListFragment.setArguments(bundle);

        return emptyTaskListFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiManager = App.API_MANAGER;
        vehicleDeliveryAreaRiderBundle = getArguments().getParcelable(VehicleDeliveryAreaRiderBundle.class.getSimpleName());
    }

    @Nullable
    @Override
    public View onCreateView(
        LayoutInflater inflater,
        ViewGroup container,
        Bundle savedInstanceState
    ) {

        return inflater.inflate(R.layout.empty_task_list_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_to_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                apiManager.getRoute(EmptyTaskListFragment.this, vehicleDeliveryAreaRiderBundle.getRider().getId());
            }
        });
    }

    @Override
    public void onSuccess(RouteWrapper routeWrapper) {
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onError(ErrorMessage errorMessage) {
        activity.onError(errorMessage.getStatus(), errorMessage.getMessage());
        swipeRefreshLayout.setRefreshing(false);
    }
}
