package com.foodpanda.urbanninja.manager;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import com.foodpanda.urbanninja.Constants;
import com.foodpanda.urbanninja.api.BaseApiCallback;
import com.foodpanda.urbanninja.api.model.ErrorMessage;
import com.foodpanda.urbanninja.api.model.RouteWrapper;
import com.foodpanda.urbanninja.api.model.ScheduleCollectionWrapper;
import com.foodpanda.urbanninja.api.model.ScheduleWrapper;
import com.foodpanda.urbanninja.api.receiver.ScheduleFinishedReceiver;
import com.foodpanda.urbanninja.api.service.LocationService;
import com.foodpanda.urbanninja.model.VehicleDeliveryAreaRiderBundle;
import com.foodpanda.urbanninja.model.enums.Action;
import com.foodpanda.urbanninja.ui.activity.MainActivity;
import com.foodpanda.urbanninja.ui.interfaces.NestedFragmentCallback;

public class ApiExecutor {
    public static final String[] PERMISSIONS_ARRAY = new String[]{
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION};

    private final MainActivity activity;
    private final ApiManager apiManager;
    private final StorageManager storageManager;
    private final NestedFragmentCallback nestedFragmentCallback;

    private VehicleDeliveryAreaRiderBundle vehicleDeliveryAreaRiderBundle;
    private ScheduleWrapper scheduleWrapper;
    private ScheduleCollectionWrapper scheduleWrappers;

    public ApiExecutor(MainActivity mainActivity,
                       NestedFragmentCallback nestedFragmentCallback,
                       ApiManager apiManager,
                       StorageManager storageManager) {
        this.activity = mainActivity;
        this.nestedFragmentCallback = nestedFragmentCallback;
        this.apiManager = apiManager;
        this.storageManager = storageManager;
        getCurrentRider();
    }

    public void getRoute() {
        apiManager.getRoute(vehicleDeliveryAreaRiderBundle.getVehicle().getId(), new BaseApiCallback<RouteWrapper>() {
            @Override
            public void onSuccess(RouteWrapper routeWrapper) {
                openCurrentRouteFragment();
                activity.hideProgress();
            }

            @Override
            public void onError(ErrorMessage errorMessage) {
                activity.onError(errorMessage.getStatus(), errorMessage.getMessage());
                activity.hideProgress();
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
                    activity.hideProgress();
                }

                @Override
                public void onError(ErrorMessage errorMessage) {
                    activity.onError(errorMessage.getStatus(), errorMessage.getMessage());
                    activity.hideProgress();
                }
            });
    }

    /**
     * Notify server if any kind of action with route was happened
     * and store this action to the map to save up to date status for each route
     * <p/>
     * Moreover this method should work offline and in this case
     * rider will be redirected to the next route or empty route list fragment
     * as soon as we finish with one particular route.
     *
     * @param action that should be sent to the server side
     */
    public void notifyActionPerformed(final Action action) {
        if (storageManager.getCurrentStop() != null) {
            long routeId = storageManager.getCurrentStop().getId();

            storageManager.storeAction(routeId, action);
            apiManager.notifyActionPerformed(routeId, action);

            if (action == Action.COMPLETED) {
                finishWithCurrentRoute();
            }
        }
    }

    public void startLocationService() {
        if (vehicleDeliveryAreaRiderBundle != null &&
            vehicleDeliveryAreaRiderBundle.getRider() != null) {
            Intent intent = new Intent(activity, LocationService.class);
            Bundle bundle = new Bundle();
            bundle.putInt(Constants.BundleKeys.VEHICLE_ID, vehicleDeliveryAreaRiderBundle.getVehicle().getId());
            intent.putExtras(bundle);

            activity.startService(intent);
        }
    }

    public void getRidersSchedule() {
        apiManager.getCurrentSchedule(
            new BaseApiCallback<ScheduleCollectionWrapper>() {

                @Override
                public void onSuccess(ScheduleCollectionWrapper scheduleWrappers) {
                    ApiExecutor.this.setScheduleWrappers(scheduleWrappers);
                    // Here we get all future and current working schedule
                    // However we need only first one as current
                    if (scheduleWrappers.size() > 0) {
                        ApiExecutor.this.scheduleWrapper = scheduleWrappers.get(0);

                        // If isClockIn flag is false we
                        // Would open clock in screen or route related screens
                        if (scheduleWrapper.isClockedIn()) {
                            getRoute();
                        } else {
                            nestedFragmentCallback.openReadyToWork(scheduleWrapper);
                        }
                        setScheduleFinishedAlarm();
                    } else {
                        nestedFragmentCallback.openReadyToWork(new ScheduleWrapper());
                    }
                    launchServiceOrAskForPermissions();
                    activity.hideProgress();
                }

                @Override
                public void onError(ErrorMessage errorMessage) {
                    activity.onError(errorMessage.getStatus(), errorMessage.getMessage());
                    activity.hideProgress();
                }
            });
    }

    /**
     * Open clock-in screen if current schedule is over
     *
     * @return true is rider's schedule is over
     */
    public boolean openNextScheduleIfCurrentIsFinished() {
        if (scheduleWrapper.isScheduleFinished()) {
            nestedFragmentCallback.openReadyToWork(switchSchedule());

            return true;
        }

        return false;
    }

    private void getCurrentRider() {
        apiManager.getCurrentRider(
            new BaseApiCallback<VehicleDeliveryAreaRiderBundle>() {
                @Override
                public void onSuccess(VehicleDeliveryAreaRiderBundle vehicleDeliveryAreaRiderBundle) {
                    ApiExecutor.this.vehicleDeliveryAreaRiderBundle = vehicleDeliveryAreaRiderBundle;
                    getRidersSchedule();
                    activity.hideProgress();
                }

                @Override
                public void onError(ErrorMessage errorMessage) {
                    activity.onError(errorMessage.getStatus(), errorMessage.getMessage());
                    activity.hideProgress();
                }
            });
    }

    private void launchServiceOrAskForPermissions() {
        if (!activity.isPermissionGranted()) {

            ActivityCompat.requestPermissions(activity,
                PERMISSIONS_ARRAY,
                MainActivity.PERMISSIONS_REQUEST_LOCATION);
        } else {
            startLocationService();
        }
    }

    /**
     * Set up {@link AlarmManager} to trigger {@link ScheduleFinishedReceiver} when the
     * working day of current rider would be finished
     * by setting PendingIntent with endTime of current schedule
     */
    private void setScheduleFinishedAlarm() {
        AlarmManager alarmManager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(activity, ScheduleFinishedReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(activity, 0, intent, 0);

        alarmManager.set(AlarmManager.RTC_WAKEUP, scheduleWrapper.getTimeWindow().getEndAt().getMillis(), pendingIntent);
    }

    private void openCurrentRouteFragment() {
        if (storageManager.getStopList().isEmpty()) {
            // will be called only of rider doesn't have any route to do
            if (!openNextScheduleIfCurrentIsFinished()) {
                nestedFragmentCallback.openEmptyListFragment(vehicleDeliveryAreaRiderBundle);
            }
        } else {
            nestedFragmentCallback.openRoute(storageManager.getCurrentStop());
        }
    }

    /**
     * finish with current schedule and get next from the list
     * if list is empty create new object to show no data information
     * in a fragment
     *
     * @return next schedule object from the API request
     */
    private ScheduleWrapper switchSchedule() {
        scheduleWrappers.remove(0);
        if (scheduleWrappers.isEmpty()) {
            scheduleWrapper = new ScheduleWrapper();
        } else {
            scheduleWrapper = scheduleWrappers.get(0);
        }

        return scheduleWrapper;
    }

    private void finishWithCurrentRoute() {
        storageManager.removeCurrentStop();
        openCurrentRouteFragment();
    }

    /**
     * This method is used only for tests
     * here we emulate a list of users schedules for the next week
     *
     * @param scheduleWrappers list of schedules
     */
    void setScheduleWrappers(ScheduleCollectionWrapper scheduleWrappers) {
        this.scheduleWrappers = scheduleWrappers;
    }

    /**
     * This method is used only for tests
     * here we emulate a current schedule as part of schedule list
     * {@link #setScheduleWrappers(ScheduleCollectionWrapper)}
     *
     * @param scheduleWrapper emulated schedule item
     */
    void setScheduleWrapper(ScheduleWrapper scheduleWrapper) {
        this.scheduleWrapper = scheduleWrapper;
    }

    /**
     * This method is used only for tests
     * here we get current emulated schedule as part of schedule list
     * {@link #setScheduleWrappers(ScheduleCollectionWrapper)}
     */
    public ScheduleWrapper getScheduleWrapper() {
        return scheduleWrapper;
    }
}
