package com.foodpanda.urbanninja.api.model;

import com.foodpanda.urbanninja.model.Model;
import com.foodpanda.urbanninja.model.Stop;
import com.foodpanda.urbanninja.model.enums.CollectionIssueReason;

public class CashCollectionIssueWrapper implements Model {
    private long routeStopId;
    private double actualValue;
    private String diffReason;
    private boolean collected = false;
    private Stop routeStop;

    public CashCollectionIssueWrapper(long routeStopId, double actualValue, CollectionIssueReason collectionIssueReason) {
        this.routeStopId = routeStopId;
        this.actualValue = actualValue;
        this.diffReason = collectionIssueReason.toString();
    }
}
