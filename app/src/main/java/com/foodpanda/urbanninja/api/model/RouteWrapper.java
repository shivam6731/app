package com.foodpanda.urbanninja.api.model;

import com.foodpanda.urbanninja.model.Stop;

import java.util.List;

public class RouteWrapper {
    private int id;
    private List<Stop> stops;

    public int getId() {
        return id;
    }

    public List<Stop> getStops() {
        return stops;
    }
}
