package com.foodpanda.urbanninja.api.model;

import com.foodpanda.urbanninja.model.Model;

public class StorableAction implements Model {
    private PerformActionWrapper performActionWrapper;
    private int routeId;

    public StorableAction(PerformActionWrapper performActionWrapper, int routeId) {
        this.performActionWrapper = performActionWrapper;
        this.routeId = routeId;
    }

    public PerformActionWrapper getPerformActionWrapper() {
        return performActionWrapper;
    }

    public int getRouteId() {
        return routeId;
    }
}
