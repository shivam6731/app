package com.foodpanda.urbanninja.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.foodpanda.urbanninja.App;
import com.foodpanda.urbanninja.Constants;
import com.foodpanda.urbanninja.R;
import com.foodpanda.urbanninja.api.BaseApiCallback;
import com.foodpanda.urbanninja.api.model.ErrorMessage;
import com.foodpanda.urbanninja.api.model.RouteWrapper;
import com.foodpanda.urbanninja.manager.ApiManager;
import com.foodpanda.urbanninja.manager.StorageManager;
import com.foodpanda.urbanninja.model.VehicleDeliveryAreaRiderBundle;
import com.foodpanda.urbanninja.ui.interfaces.MainActivityCallback;

public class EmptyTaskListFragment extends BaseFragment implements BaseApiCallback<RouteWrapper> {
    private MainActivityCallback mainActivityCallback;

    private ApiManager apiManager;
    private StorageManager storageManager;
    private SwipeRefreshLayout swipeRefreshLayout;

    private VehicleDeliveryAreaRiderBundle vehicleDeliveryAreaRiderBundle;

    public static EmptyTaskListFragment newInstance(VehicleDeliveryAreaRiderBundle vehicleDeliveryAreaRiderBundle) {
        EmptyTaskListFragment emptyTaskListFragment = new EmptyTaskListFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BundleKeys.VEHICLE_DELIVERY_AREA_RIDER_BUNDLE, vehicleDeliveryAreaRiderBundle);
        emptyTaskListFragment.setArguments(bundle);

        return emptyTaskListFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivityCallback = (MainActivityCallback) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiManager = App.API_MANAGER;
        storageManager = App.STORAGE_MANAGER;

        vehicleDeliveryAreaRiderBundle = getArguments().getParcelable(Constants.BundleKeys.VEHICLE_DELIVERY_AREA_RIDER_BUNDLE);
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
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                apiManager.getRoute(
                    vehicleDeliveryAreaRiderBundle.getRider().getId(),
                    EmptyTaskListFragment.this
                );
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                apiManager.getRoute(
                    vehicleDeliveryAreaRiderBundle.getRider().getId(),
                    EmptyTaskListFragment.this
                );
            }
        });
    }

    @Override
    public void onSuccess(RouteWrapper routeWrapper) {
        storageManager.storeStopList(routeWrapper.getStops());
        swipeRefreshLayout.setRefreshing(false);
        if (!storageManager.getStopList().isEmpty()) {
            mainActivityCallback.openRoute(storageManager.getCurrentStop());
        }
    }

    @Override
    public void onError(ErrorMessage errorMessage) {
        activity.onError(errorMessage.getStatus(), errorMessage.getMessage());
        swipeRefreshLayout.setRefreshing(false);
    }
}
