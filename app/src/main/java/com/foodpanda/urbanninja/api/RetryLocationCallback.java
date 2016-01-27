package com.foodpanda.urbanninja.api;

import com.foodpanda.urbanninja.api.model.RiderLocation;
import com.foodpanda.urbanninja.api.model.RiderLocationCollectionWrapper;
import com.foodpanda.urbanninja.manager.ApiQueue;
import com.foodpanda.urbanninja.model.GeoCoordinate;

import org.joda.time.DateTime;

import retrofit.Call;

/**
 * Child of {@see BaseCallback} to allows to store location updates params to the {@link ApiQueue}
 * and after reSend this location to the server side as soon as internet would work
 *
 * @param <T> extends list of {@link RiderLocation}
 */
public class RetryLocationCallback<T extends RiderLocationCollectionWrapper> extends BaseCallback<T> {
    private int vehicleId;
    private RiderLocationCollectionWrapper locationCollectionWrapper;
    private StorableApiCallback<T> storableApiCallback;

    /**
     * We should be able to clean rider locations list if data was sent or was stored
     *
     * @param storableApiCallback       callback inform us about the statuses of request success,
     *                                  failed and stored
     * @param call                      required param for {@link BaseCallback} to be able to retry request
     * @param vehicleId                 id of rider vehicle that should be saved and sent
     * @param locationCollectionWrapper wrapper with a list of all information about rider status such as
     *                                  {@link DateTime} and {@link GeoCoordinate}
     */
    public RetryLocationCallback(StorableApiCallback<T> storableApiCallback,
                                 Call<T> call,
                                 int vehicleId,
                                 RiderLocationCollectionWrapper locationCollectionWrapper) {
        this(call, vehicleId, locationCollectionWrapper);
        this.storableApiCallback = storableApiCallback;
    }

    /**
     * Default constructor for sendLocation API request
     *
     * @param call                      required param for {@link BaseCallback} to be able to retry request
     * @param vehicleId                 id of rider vehicle that should be saved and sent
     * @param locationCollectionWrapper wrapper with a list of all information about rider status such as
     *                                  {@link DateTime} and {@link GeoCoordinate}
     */
    public RetryLocationCallback(Call<T> call,
                                 int vehicleId,
                                 RiderLocationCollectionWrapper locationCollectionWrapper) {
        super(null, call);
        this.vehicleId = vehicleId;
        this.locationCollectionWrapper = locationCollectionWrapper;
    }

    @Override
    protected boolean sendRetry() {
        boolean isSent = super.sendRetry();
        if (!isSent) {
            ApiQueue.getInstance().enqueueLocation(locationCollectionWrapper, vehicleId);
            if (storableApiCallback != null) {
                storableApiCallback.onItemStored();
            }
        }

        return isSent;
    }

}
