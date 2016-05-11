package com.foodpanda.urbanninja.model;

import com.foodpanda.urbanninja.model.enums.RouteStopTask;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class StopTest {
    @Test
    public void testPhoneNumberDelivery() {
        Stop stop = new Stop("deliveryPhone", "pickupPhone", RouteStopTask.DELIVER);
        assertEquals(stop.getPhoneNumber(), "deliveryPhone");
    }

    @Test
    public void testPhoneNumberPickUp() {
        Stop stop = new Stop("deliveryPhone", "pickupPhone", RouteStopTask.PICKUP);
        assertEquals(stop.getPhoneNumber(), "pickupPhone");
    }

    @Test
    public void testPhoneNumberNull() {
        Stop stop = new Stop("deliveryPhone", "pickupPhone", null);
        assertEquals(stop.getPhoneNumber(), "pickupPhone");
    }

}
