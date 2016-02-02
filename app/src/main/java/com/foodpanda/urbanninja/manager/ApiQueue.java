package com.foodpanda.urbanninja.manager;

import com.foodpanda.urbanninja.App;
import com.foodpanda.urbanninja.api.RetryActionCallback;
import com.foodpanda.urbanninja.api.RetryLocationCallback;
import com.foodpanda.urbanninja.api.model.PerformActionWrapper;
import com.foodpanda.urbanninja.api.model.RiderLocation;
import com.foodpanda.urbanninja.api.model.RiderLocationCollectionWrapper;
import com.foodpanda.urbanninja.api.model.StorableAction;
import com.foodpanda.urbanninja.api.request.LogisticsService;
import com.foodpanda.urbanninja.model.Stop;

import java.util.LinkedList;
import java.util.Queue;

import retrofit.Call;

/**
 * This class store all API requests that should be resended as soon as connection
 * would work again
 */
public class ApiQueue {
    private static ApiQueue instance = new ApiQueue();
    private StorageManager storageManager;

    private Queue<StorableAction> requestsQueue = new LinkedList<>();
    private Queue<RiderLocation> requestsLocationQueue = new LinkedList<>();
    private int vehicleId;

    private ApiQueue() {
        storageManager = App.STORAGE_MANAGER;
        requestsQueue = storageManager.getActionApiRequestList();
        requestsLocationQueue = storageManager.getLocationApiRequestList();
        vehicleId = storageManager.getVehicleId();
    }


    public static ApiQueue getInstance() {
        return instance;
    }

    /**
     * store riders action request data
     *
     * @param performActionWrapper wrapper for user Action
     *                             {@link com.foodpanda.urbanninja.model.enums.Action}
     *                             and executed time
     * @param routeId              route id is required param from the API request
     */
    public void enqueueAction(PerformActionWrapper performActionWrapper, int routeId) {

        requestsQueue.add(new StorableAction(performActionWrapper, routeId));
        storageManager.storeActionApiRequests(requestsQueue);
    }

    public void enqueueLocation(RiderLocationCollectionWrapper riderLocationCollectionWrapper, int vehicleId) {

        requestsLocationQueue.addAll(riderLocationCollectionWrapper);

        storageManager.storeLocationApiRequests(requestsLocationQueue);

        this.vehicleId = vehicleId;
        storageManager.storeVehicleId(vehicleId);
    }

    /**
     * Try to execute all users action api calls
     *
     * @param service implementation of {@link LogisticsService} where all call would be executed
     */
    private void resendAction(LogisticsService service) {
        if (!requestsQueue.isEmpty()) {
            StorableAction storableAction = requestsQueue.remove();
            Call<Stop> call = service.performedActionNotify(storableAction.getRouteId(), storableAction.getPerformActionWrapper());
            call.enqueue(new RetryActionCallback<>(call, storableAction.getRouteId(), storableAction.getPerformActionWrapper()));
            resendAction(service);
        }
        storageManager.storeActionApiRequests(requestsQueue);
    }

    /**
     * Try to execute all users location api calls
     *
     * @param service implementation of {@link LogisticsService} where all call would be executed
     */
    private void resendLocation(LogisticsService service) {
        if (!requestsLocationQueue.isEmpty()) {
            RiderLocationCollectionWrapper riderLocations = new RiderLocationCollectionWrapper();
            riderLocations.addAll(requestsLocationQueue);

            Call<RiderLocationCollectionWrapper> call = service.sendLocation(
                vehicleId,
                riderLocations);

            call.enqueue(new RetryLocationCallback<>(
                call,
                vehicleId,
                riderLocations));

            requestsLocationQueue.clear();
            storageManager.storeLocationApiRequests(requestsLocationQueue);
        }
    }

    public void resendRequests(LogisticsService logisticsService) {
        resendAction(logisticsService);
        resendLocation(logisticsService);
    }

}
