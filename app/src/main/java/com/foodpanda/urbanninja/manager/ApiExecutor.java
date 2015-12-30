package com.foodpanda.urbanninja.manager;

import android.widget.Toast;

import com.foodpanda.urbanninja.App;
import com.foodpanda.urbanninja.api.BaseApiCallback;
import com.foodpanda.urbanninja.api.model.ErrorMessage;
import com.foodpanda.urbanninja.api.model.RouteWrapper;
import com.foodpanda.urbanninja.api.model.ScheduleWrapper;
import com.foodpanda.urbanninja.model.VehicleDeliveryAreaRiderBundle;
import com.foodpanda.urbanninja.ui.activity.BaseActivity;
import com.foodpanda.urbanninja.ui.activity.MainActivity;
import com.foodpanda.urbanninja.ui.interfaces.MainActivityCallback;

import java.util.List;

public class ApiExecutor {
    private final BaseActivity activity;
    private final ApiManager apiManager;
    private final MainActivityCallback mainActivityCallback;

    private VehicleDeliveryAreaRiderBundle vehicleDeliveryAreaRiderBundle;
    private ScheduleWrapper scheduleWrapper;

    public ApiExecutor(MainActivity mainActivity) {
        this.activity = mainActivity;
        this.mainActivityCallback = mainActivity;
        this.apiManager = App.API_MANAGER;
        getCurrentRider();
    }

    private void getCurrentRider() {
        apiManager.getCurrentRider(
            new BaseApiCallback<VehicleDeliveryAreaRiderBundle>() {
                @Override
                public void onSuccess(VehicleDeliveryAreaRiderBundle vehicleDeliveryAreaRiderBundle) {
                    ApiExecutor.this.vehicleDeliveryAreaRiderBundle = vehicleDeliveryAreaRiderBundle;
                    getRidersSchedule();
                }

                @Override
                public void onError(ErrorMessage errorMessage) {
                    activity.onError(errorMessage.getStatus(), errorMessage.getMessage());
                }
            });
    }

    private void getRidersSchedule() {
        apiManager.getSchedule(
            vehicleDeliveryAreaRiderBundle.getRider().getId(),
            new BaseApiCallback<List<ScheduleWrapper>>() {
                @Override
                public void onSuccess(List<ScheduleWrapper> scheduleWrappers) {
                    // Here we get all future and current working schedule
                    // However we need only first one as current
                    if (scheduleWrappers.size() > 0) {
                        ApiExecutor.this.scheduleWrapper = scheduleWrappers.get(0);

                        // In the future we would have isClockIn flag and depend on this value we
                        // Would open clock in screen or route related screens
                        if (scheduleWrapper.getId() == 0) {
                            getRoute();
                        } else {
                            mainActivityCallback.openReadyToWork(scheduleWrapper);
                        }
                    }
                }

                @Override
                public void onError(ErrorMessage errorMessage) {
                    activity.onError(errorMessage.getStatus(), errorMessage.getMessage());
                }
            });
    }

    private void getRoute() {
        apiManager.getRoute(vehicleDeliveryAreaRiderBundle.getVehicle().getId(), new BaseApiCallback<RouteWrapper>() {
            @Override
            public void onSuccess(RouteWrapper routeWrapper) {
                if (routeWrapper.getStops().size() == 0) {
                    mainActivityCallback.openEmptyListFragment(vehicleDeliveryAreaRiderBundle);
                } else {
                    mainActivityCallback.openPickUp(routeWrapper);
                }
            }

            @Override
            public void onError(ErrorMessage errorMessage) {
                activity.onError(errorMessage.getStatus(), errorMessage.getMessage());
            }
        });
    }

    public void clockIn() {
        if (scheduleWrapper != null)
            apiManager.scheduleClockIn(scheduleWrapper.getId(), new BaseApiCallback<ScheduleWrapper>() {
                @Override
                public void onSuccess(ScheduleWrapper scheduleWrapper) {
                    Toast.makeText(activity, "Clock in", Toast.LENGTH_SHORT).show();
                    getRoute();
                }

                @Override
                public void onError(ErrorMessage errorMessage) {
                    activity.onError(errorMessage.getStatus(), errorMessage.getMessage());
                    getRoute();
                }
            });
    }
}
