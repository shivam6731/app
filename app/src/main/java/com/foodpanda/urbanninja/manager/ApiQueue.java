package com.foodpanda.urbanninja.manager;

import com.foodpanda.urbanninja.api.model.PerformActionWrapper;
import com.foodpanda.urbanninja.api.model.RiderLocation;
import com.foodpanda.urbanninja.api.model.RiderLocationCollectionWrapper;
import com.foodpanda.urbanninja.api.model.StorableStatus;
import com.foodpanda.urbanninja.model.enums.Status;

import java.util.LinkedList;
import java.util.Queue;

import javax.inject.Inject;
import javax.inject.Singleton;


/**
 * This class store all API requests that should be resended as soon as connection
 * would work again
 */
@Singleton
public class ApiQueue {
    private StorageManager storageManager;

    private Queue<StorableStatus> requestsQueue = new LinkedList<>();
    private Queue<RiderLocation> requestsLocationQueue = new LinkedList<>();

    @Inject
    public ApiQueue(StorageManager storageManager) {
        this.storageManager = storageManager;
        requestsQueue = storageManager.getStatusApiRequestList();
        requestsLocationQueue = storageManager.getLocationApiRequestList();
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

        storageManager.storeVehicleId(vehicleId);
    }

    /**
     * Provides not send riders locations to be sent by the {@link ApiManager}
     *
     * @return queue of failed locations
     */
    Queue<RiderLocation> getRequestsLocationQueue() {
        return requestsLocationQueue;
    }

    /**
     * Provides not send riders action  to be sent by the {@link ApiManager}
     *
     * @return queue of failed rider actions
     */
    Queue<StorableStatus> getRequestsQueue() {
        return requestsQueue;
    }

}
