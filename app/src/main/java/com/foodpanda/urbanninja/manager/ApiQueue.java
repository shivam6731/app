package com.foodpanda.urbanninja.manager;

import com.foodpanda.urbanninja.App;
import com.foodpanda.urbanninja.api.model.PerformActionWrapper;
import com.foodpanda.urbanninja.api.model.RiderLocation;
import com.foodpanda.urbanninja.api.model.RiderLocationCollectionWrapper;
import com.foodpanda.urbanninja.api.model.StorableStatus;
import com.foodpanda.urbanninja.model.enums.Status;

import java.util.LinkedList;
import java.util.Queue;


/**
 * This class store all API requests that should be resended as soon as connection
 * would work again
 */
public class ApiQueue {
    private static ApiQueue instance = new ApiQueue();
    private StorageManager storageManager;
    private ApiManager apiManager;

    private Queue<StorableStatus> requestsQueue = new LinkedList<>();
    private Queue<RiderLocation> requestsLocationQueue = new LinkedList<>();
    private int vehicleId;

    private ApiQueue() {
        storageManager = App.STORAGE_MANAGER;
        apiManager = App.API_MANAGER;

        requestsQueue = storageManager.getStatusApiRequestList();
        requestsLocationQueue = storageManager.getLocationApiRequestList();
        vehicleId = storageManager.getVehicleId();
    }


    public static ApiQueue getInstance() {
        return instance;
    }

    /**
     * store riders action request data
     *
     * @param performActionWrapper wrapper for user Status
     *                             {@link Status}
     *                             and executed time
     * @param routeId              route id is required param from the API request
     */
    public void enqueueAction(PerformActionWrapper performActionWrapper, long routeId) {

        requestsQueue.add(new StorableStatus(performActionWrapper, routeId));
        storageManager.storeStatusApiRequests(requestsQueue);
    }

    public void enqueueLocation(RiderLocationCollectionWrapper riderLocationCollectionWrapper, int vehicleId) {

        requestsLocationQueue.addAll(riderLocationCollectionWrapper);

        storageManager.storeLocationApiRequests(requestsLocationQueue);

        this.vehicleId = vehicleId;
        storageManager.storeVehicleId(vehicleId);
    }

    /**
     * Try to execute all users action api calls
     */
    private void resendAction() {
        if (!requestsQueue.isEmpty()) {
            StorableStatus storableStatus = requestsQueue.remove();
            apiManager.notifyStoredAction(storableStatus);
            resendAction();
        }
        storageManager.storeStatusApiRequests(requestsQueue);
    }

    /**
     * Try to execute all users location api calls
     */
    private void resendLocation() {
        if (!requestsLocationQueue.isEmpty()) {
            RiderLocationCollectionWrapper riderLocations = new RiderLocationCollectionWrapper();
            riderLocations.addAll(requestsLocationQueue);
            apiManager.sendLocation(vehicleId, riderLocations);

            requestsLocationQueue.clear();
            storageManager.storeLocationApiRequests(requestsLocationQueue);
        }
    }

    public void resendRequests() {
        resendAction();
        resendLocation();
    }

}
