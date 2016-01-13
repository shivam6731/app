package com.foodpanda.urbanninja.manager;

import com.foodpanda.urbanninja.App;
import com.foodpanda.urbanninja.api.RetryCallback;
import com.foodpanda.urbanninja.api.model.PerformActionWrapper;
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

    private ApiQueue() {
        storageManager = App.STORAGE_MANAGER;
        requestsQueue = storageManager.getApiRequestList();
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
    public void enqueue(PerformActionWrapper performActionWrapper, int routeId) {

        requestsQueue.add(new StorableAction(performActionWrapper, routeId));
        storageManager.storeApiRequests(requestsQueue);
    }

    /**
     * Try to execute all users action api calls
     * @param service implementation of {@link LogisticsService} where all call would be executed
     */
    public void recall(LogisticsService service) {
        if (!requestsQueue.isEmpty()) {
            StorableAction storableAction = requestsQueue.remove();
            Call<Stop> call = service.performedActionNotify(storableAction.getRouteId(), storableAction.getPerformActionWrapper());
            call.enqueue(new RetryCallback<>(call, storableAction.getRouteId(), storableAction.getPerformActionWrapper()));
            recall(service);
        }
        storageManager.storeApiRequests(requestsQueue);
    }
}
