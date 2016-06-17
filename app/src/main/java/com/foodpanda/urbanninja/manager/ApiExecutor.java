package com.foodpanda.urbanninja.manager;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import com.foodpanda.urbanninja.Constants;
import com.foodpanda.urbanninja.api.BaseApiCallback;
import com.foodpanda.urbanninja.api.model.ErrorMessage;
import com.foodpanda.urbanninja.api.model.RouteWrapper;
import com.foodpanda.urbanninja.api.model.ScheduleCollectionWrapper;
import com.foodpanda.urbanninja.api.model.ScheduleWrapper;
import com.foodpanda.urbanninja.api.service.LocationService;
import com.foodpanda.urbanninja.api.utils.ApiUtils;
import com.foodpanda.urbanninja.model.VehicleDeliveryAreaRiderBundle;
import com.foodpanda.urbanninja.model.enums.Status;
import com.foodpanda.urbanninja.ui.activity.MainActivity;
import com.foodpanda.urbanninja.ui.interfaces.NestedFragmentCallback;

import rx.Observable;

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

    public ApiExecutor(
        MainActivity mainActivity,
        NestedFragmentCallback nestedFragmentCallback,
        ApiManager apiManager,
        StorageManager storageManager
    ) {
        this.activity = mainActivity;
        this.nestedFragmentCallback = nestedFragmentCallback;
        this.apiManager = apiManager;
        this.storageManager = storageManager;
        getAllData();
    }

    /**
     * Based on the requirements when we receive push notification
     * with schedule changes type we need to update schedule list
     * and after route stop list, to allow rider to finish all assigned
     * order even if schedule is over
     */
    public void updateScheduleAndRouteStop() {
        updateRoute(getScheduleObservable(apiManager.getCurrentScheduleObservable()));
    }

    /**
     * Update route stop list when rider receives
     * push notification about route plan updates or some route canceled
     */
    public void updateRoute() {
        updateRoute(apiManager.getRouteObservable(vehicleDeliveryAreaRiderBundle.getVehicle().getId()));
    }

    public void clockIn() {
        if (scheduleWrapper != null)
            apiManager.scheduleClockIn(scheduleWrapper.getId(), new BaseApiCallback<ScheduleWrapper>() {
                @Override
                public void onSuccess(ScheduleWrapper scheduleWrapper) {
                    ApiExecutor.this.scheduleWrapper = scheduleWrapper;
                    updateRoute();
                    hideProgressIndicators();
                }

                @Override
                public void onError(ErrorMessage errorMessage) {
                    activity.onError(errorMessage.getStatus(), errorMessage.getMessage());
                    hideProgressIndicators();
                }
            });
    }

    /**
     * Notify server if any kind of status with route was happened
     * and store this status to the map to save up to date status for each route
     * <p>
     * Moreover this method should work offline and in this case
     * rider will be redirected to the next route or empty route list fragment
     * as soon as we finish with one particular route.
     *
     * @param status that should be sent to the server side
     */
    public void notifyActionPerformed(@NonNull final Status status) {
        if (storageManager.getCurrentStop() != null) {
            long routeId = storageManager.getCurrentStop().getId();

            storageManager.storeStatus(routeId, status);
            apiManager.notifyActionPerformed(routeId, status);

            if (status == Status.COMPLETED) {
                finishWithCurrentRoute();
            }
        }
    }

    public void startLocationService() {
        if (vehicleDeliveryAreaRiderBundle != null &&
            vehicleDeliveryAreaRiderBundle.getVehicle() != null) {
            Intent intent = new Intent(activity, LocationService.class);
            Bundle bundle = new Bundle();
            bundle.putInt(Constants.BundleKeys.VEHICLE_ID, vehicleDeliveryAreaRiderBundle.getVehicle().getId());
            intent.putExtras(bundle);

            activity.startService(intent);
        }
    }

    /**
     * Retrieve  all rider information in a sequence
     * with only one error handler and result callback
     * <p>
     * This method should be executed only when app is just launched
     */
    private void getAllData() {
        ApiUtils.wrapObservable(
            apiManager.getRiderObservable())
            .doOnNext(this::updateRiderInfo)
            .concatMap(vehicleDeliveryAreaRiderBundle1 -> ApiUtils.wrapObservable(apiManager.getCurrentScheduleObservable()))
            .doOnNext(this::updateScheduleInfo)
            .concatMap(scheduleWrappers1 -> ApiUtils.wrapObservable(getRouteStopObservable()))
            .subscribe(this::updateRouteStopInfo, throwable -> {
                ApiUtils.showErrorMessage(throwable, activity);
                hideProgressIndicators();
            });
    }

    /**
     * Save all rider information to the local variables
     * and update rider info in UI thread
     *
     * @param vehicleDeliveryAreaRiderBundle bundle with rider info
     */
    void updateRiderInfo(VehicleDeliveryAreaRiderBundle vehicleDeliveryAreaRiderBundle) {
        this.vehicleDeliveryAreaRiderBundle = vehicleDeliveryAreaRiderBundle;
        if (vehicleDeliveryAreaRiderBundle.getRider() != null) {
            activity.setRiderContent(vehicleDeliveryAreaRiderBundle.getRider());
        }
    }

    /**
     * Save rider schedule to the local variables
     * and update title for tha app in UI thread
     *
     * @param scheduleWrappers bundle with rider schedule
     */
    void updateScheduleInfo(ScheduleCollectionWrapper scheduleWrappers) {
        // Remove action title for cases when user is not clocked-in
        activity.writeCodeAsTitle(null);
        setScheduleWrappers(scheduleWrappers);
        // Here we get all future and current working schedule
        // However we need only first one as current
        if (scheduleWrappers.size() > 0) {
            scheduleWrapper = scheduleWrappers.get(0);
        }
        launchServiceOrAskForPermissions();
    }

    /**
     * Store rider route stop list to the #storageManager
     * and open current fragment depend on current state
     *
     * @param routeWrapper bundle with rider route stops
     */
    void updateRouteStopInfo(RouteWrapper routeWrapper) {
        storageManager.storeStopList((routeWrapper).getStops());
        openCurrentFragment();
        hideProgressIndicators();
    }

    /**
     * Generate route stop observable
     * based on rider vehicle id
     *
     * @return RouteWrapper Observable to retrieve rider info
     */
    private Observable<RouteWrapper> getRouteStopObservable() {
        return ApiUtils.wrapObservable(apiManager.getRouteObservable(vehicleDeliveryAreaRiderBundle.getVehicle().getId()));
    }

    /**
     * Concat rider schedule Observable and Route stop plan observable
     * that should be executed in a sequence
     *
     * @param observable Observable to retrieve rider schedule
     * @return Observable to retrieve route stop plan
     */
    private Observable<RouteWrapper> getScheduleObservable(Observable<ScheduleCollectionWrapper> observable) {
        observable.doOnNext(this::updateScheduleInfo);

        return observable.concatMap(
            scheduleWrappers1 -> {
                updateScheduleInfo(scheduleWrappers1);

                return apiManager.getRouteObservable(vehicleDeliveryAreaRiderBundle.getVehicle().getId());
            });
    }

    /**
     * Open clock-in screen if rider is not clocked-in
     * Open clock-in for the next schedule if current one is over
     * Open empty clock-in screen if riders doesn't have schedule
     *
     * @return true is rider's schedule is over or he is not clocked-in
     */
    boolean openRiderScheduleScreen() {
        //open next schedule
        if (scheduleWrapper == null || scheduleWrapper.isScheduleFinished()) {
            nestedFragmentCallback.openReadyToWork(moveToNextSchedule());

            return true;
        }
        //open current schedule in case when rider is not clocked-in
        if (scheduleWrapper != null && !scheduleWrapper.isClockedIn()) {
            nestedFragmentCallback.openReadyToWork(scheduleWrapper);

            return true;
        }

        return false;
    }

    /**
     * Single API call to get rider route stop plan
     * and after open correct fragment
     *
     * @param observable Observable that would be executed
     */
    void updateRoute(Observable<RouteWrapper> observable) {
        ApiUtils.wrapObservable(observable)
            .subscribe(
                this::updateRouteStopInfo,
                throwable -> {
                    ApiUtils.showErrorMessage(throwable, activity);
                    hideProgressIndicators();
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
     * Open proper screen depend on rider current state
     * <p>
     * in case when rider has route the route
     * #nestedFragmentCallback.openRoute()should be called
     * <p>
     * in case when rider doesn't have route and clocked-in
     * #nestedFragmentCallback.openEmptyListFragment should be called
     * <p>
     * in case when rider doesn't have route stop and not clock-in and schedule is not empty
     * #nestedFragmentCallback.openReadyToWork should be called
     * <p>
     * in case when rider doesn't have route stop and not clock-in and schedule is empty
     * #nestedFragmentCallback.openReadyToWork with empty data should be called
     * <p>
     */
    void openCurrentFragment() {
        if (storageManager.getStopList().isEmpty()) {
            //set empty toolbar title and subtitle
            activity.writeCodeAsTitle(null);
            // will be called only of rider doesn't have any route to do
            if (!openRiderScheduleScreen()) {
                //will be called only if rider has valid schedule
                nestedFragmentCallback.openEmptyListFragment();
            }
        } else {
            // will be called only if rider has route to work with
            nestedFragmentCallback.openRoute(storageManager.getCurrentStop());
            //set order based toolbar title and subtitle
            activity.writeCodeAsTitle(storageManager.getCurrentStop());
        }
    }


    /**
     * finish with current schedule and get next from the list
     * if list is empty create new object to show no data information
     * in a fragment
     *
     * @return next schedule object from the API request
     */
    private ScheduleWrapper moveToNextSchedule() {
        if (scheduleWrapper == null || scheduleWrappers == null || scheduleWrappers.isEmpty()) {
            scheduleWrapper = null;
        } else {
            scheduleWrappers.remove(0);
            if (scheduleWrappers.isEmpty()) {
                scheduleWrapper = null;
            } else {
                scheduleWrapper = scheduleWrappers.get(0);
            }
        }

        return scheduleWrapper;
    }

    private void finishWithCurrentRoute() {
        storageManager.removeCurrentStop();
        activity.writeCodeAsTitle(storageManager.getCurrentStop());
        openCurrentFragment();
    }

    private void hideProgressIndicators() {
        activity.hideProgress();
        nestedFragmentCallback.hideProgressIndicator();
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
    ScheduleWrapper getScheduleWrapper() {
        return scheduleWrapper;
    }
}
