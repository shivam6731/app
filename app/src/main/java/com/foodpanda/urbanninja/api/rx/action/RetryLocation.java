package com.foodpanda.urbanninja.api.rx.action;

import com.foodpanda.urbanninja.api.StorableApiCallback;
import com.foodpanda.urbanninja.api.model.RiderLocationCollectionWrapper;
import com.foodpanda.urbanninja.manager.ApiQueue;
import com.foodpanda.urbanninja.model.GeoCoordinate;

import org.joda.time.DateTime;

public class RetryLocation extends RetryWithDelay {
    private int vehicleId;
    private RiderLocationCollectionWrapper locationCollectionWrapper;
    private StorableApiCallback storableApiCallback;

    /**
     * Api call with retry logic inside
     * In case this request was failed rider location
     * would be added to the queue and send as soon as connection would be available again
     *
     * @param storableApiCallback       callback inform us about the statuses of request success,
     *                                  failed and stored
     * @param vehicleId                 id of rider vehicle that should be saved and sent
     * @param locationCollectionWrapper wrapper with a list of all information about rider status such as
     *                                  {@link DateTime} and {@link GeoCoordinate}
     */
    public RetryLocation(
        StorableApiCallback storableApiCallback,
        int vehicleId,
        RiderLocationCollectionWrapper locationCollectionWrapper) {
        this.vehicleId = vehicleId;
        this.locationCollectionWrapper = locationCollectionWrapper;
        this.storableApiCallback = storableApiCallback;
    }

    @Override
    protected void storeAction() {
        super.storeAction();
        ApiQueue.getInstance().enqueueLocation(locationCollectionWrapper, vehicleId);
        if (storableApiCallback != null) {
            storableApiCallback.onItemStored();
        }
    }
}
