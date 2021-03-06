package com.foodpanda.urbanninja.api.model;

import com.foodpanda.urbanninja.model.Model;

public class StorableStatus implements Model {
    private PerformActionWrapper performActionWrapper;
    private long routeId;

    public StorableStatus(PerformActionWrapper performActionWrapper, long routeId) {
        this.performActionWrapper = performActionWrapper;
        this.routeId = routeId;
    }

    public PerformActionWrapper getPerformActionWrapper() {
        return performActionWrapper;
    }

    public long getRouteId() {
        return routeId;
    }
}
