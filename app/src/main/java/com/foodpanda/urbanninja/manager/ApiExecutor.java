package com.foodpanda.urbanninja.manager;

import com.foodpanda.urbanninja.App;
import com.foodpanda.urbanninja.api.BaseApiCallback;
import com.foodpanda.urbanninja.api.model.ErrorMessage;
import com.foodpanda.urbanninja.api.model.RouteWrapper;
import com.foodpanda.urbanninja.api.model.ScheduleCollectionWrapper;
import com.foodpanda.urbanninja.api.model.ScheduleWrapper;
import com.foodpanda.urbanninja.model.VehicleDeliveryAreaRiderBundle;
import com.foodpanda.urbanninja.model.enums.Action;
import com.foodpanda.urbanninja.ui.activity.BaseActivity;
import com.foodpanda.urbanninja.ui.activity.MainActivity;
import com.foodpanda.urbanninja.ui.interfaces.MainActivityCallback;

public class ApiExecutor {
    private final BaseActivity activity;
    private final ApiManager apiManager;
    private final StorageManager storageManager;
    private final MainActivityCallback mainActivityCallback;

    private VehicleDeliveryAreaRiderBundle vehicleDeliveryAreaRiderBundle;
    private ScheduleWrapper scheduleWrapper;

    public ApiExecutor(MainActivity mainActivity) {
        this.activity = mainActivity;
        this.mainActivityCallback = mainActivity;
        this.apiManager = App.API_MANAGER;
        this.storageManager = App.STORAGE_MANAGER;
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
        apiManager.getCurrentSchedule(
            new BaseApiCallback<ScheduleCollectionWrapper>() {

                @Override
                public void onSuccess(ScheduleCollectionWrapper scheduleWrappers) {
                    // Here we get all future and current working schedule
                    // However we need only first one as current
                    if (scheduleWrappers.size() > 0) {
                        ApiExecutor.this.scheduleWrapper = scheduleWrappers.get(0);

                        // If isClockIn flag is false we
                        // Would open clock in screen or route related screens
                        if (scheduleWrapper.isclockedIn()) {
                            getRoute();
                        } else {
                            mainActivityCallback.openReadyToWork(scheduleWrapper);
                        }
                    } else {
                        mainActivityCallback.openReadyToWork(new ScheduleWrapper());
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
                storageManager.storeStopList(routeWrapper.getStops());
                if (storageManager.getStopList().size() == 0) {
                    mainActivityCallback.openEmptyListFragment(vehicleDeliveryAreaRiderBundle);
                } else {
                    mainActivityCallback.openRouteStopDetails();
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
                    ApiExecutor.this.scheduleWrapper = scheduleWrapper;
                    getRoute();
                }

                @Override
                public void onError(ErrorMessage errorMessage) {
                    activity.onError(errorMessage.getStatus(), errorMessage.getMessage());
                }
            });
    }

    public void performAction(Action action) {
        int routeId = storageManager.getStopList().get(0).getId();
        apiManager.notifyActionPerformed(routeId, action);
    }

    public VehicleDeliveryAreaRiderBundle getVehicleDeliveryAreaRiderBundle() {
        return vehicleDeliveryAreaRiderBundle;
    }
}
