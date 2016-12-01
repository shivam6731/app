package com.foodpanda.urbanninja.model.enums;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class VehicleDetectedActivityTypeTest {

    @Test
    public void testFromInteger() {
        assertEquals(VehicleDetectedActivityType.fromInteger(0), VehicleDetectedActivityType.IN_VEHICLE);
        assertEquals(VehicleDetectedActivityType.fromInteger(1), VehicleDetectedActivityType.ON_BICYCLE);
        assertEquals(VehicleDetectedActivityType.fromInteger(2), VehicleDetectedActivityType.ON_FOOT);
        assertEquals(VehicleDetectedActivityType.fromInteger(8), VehicleDetectedActivityType.RUNNING);
        assertEquals(VehicleDetectedActivityType.fromInteger(3), VehicleDetectedActivityType.STILL);
        assertEquals(VehicleDetectedActivityType.fromInteger(5), VehicleDetectedActivityType.TILTING);
        assertEquals(VehicleDetectedActivityType.fromInteger(4), VehicleDetectedActivityType.UNKNOWN);
        assertEquals(VehicleDetectedActivityType.fromInteger(7), VehicleDetectedActivityType.WALKING);
        assertEquals(VehicleDetectedActivityType.fromInteger(-7), VehicleDetectedActivityType.UNKNOWN);
    }
}
