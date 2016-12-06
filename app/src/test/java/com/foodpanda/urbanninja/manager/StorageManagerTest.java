package com.foodpanda.urbanninja.manager;

import android.app.Application;

import com.foodpanda.urbanninja.BuildConfig;
import com.foodpanda.urbanninja.api.model.RiderLocation;
import com.foodpanda.urbanninja.model.GeoCoordinate;
import com.foodpanda.urbanninja.model.Rider;
import com.foodpanda.urbanninja.model.Stop;
import com.foodpanda.urbanninja.model.Token;
import com.foodpanda.urbanninja.model.VehicleDetectedActivity;
import com.foodpanda.urbanninja.model.enums.RouteStopTask;
import com.foodpanda.urbanninja.model.enums.VehicleDetectedActivityType;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.Collections;
import java.util.LinkedList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, packageName = "com.foodpanda.urbanninja")
public class StorageManagerTest {
    private StorageManager storageManager;

    @Before
    public void setUp() {
        Application app = RuntimeEnvironment.application;
        app.onCreate();

        storageManager = new StorageManager(app);
    }

    @Test
    public void testStoreToken() {
        Token token = new Token("", "", 0, "");
        assertTrue(storageManager.storeToken(token));
        assertEquals(storageManager.getToken(), token);
    }

    @Test
    public void testStoreTokenNull() {
        assertNull(storageManager.getToken());
    }

    @Test
    public void testIsLogged() {
        Token token = new Token("", "", 0, "");
        assertTrue(storageManager.storeToken(token));
        assertTrue(storageManager.isLogged());
    }

    @Test
    public void testIsLoggedEmptyData() {
        assertFalse(storageManager.isLogged());
    }

    @Test
    public void testClearToken() {
        Token token = new Token("", "", 0, "");
        assertTrue(storageManager.storeToken(token));
        storageManager.cleanSession();
        assertEquals(storageManager.getToken(), null);
        assertTrue(storageManager.getStopList().isEmpty());
    }

    @Test
    public void testGetCurrentStopEmpty() {
        storageManager.storeStopList(new LinkedList<>());

        assertNull(storageManager.getCurrentStop());
    }

    @Test
    public void testHasCurrentStop() {
        storageManager.storeStopList(new LinkedList<>());

        assertFalse(storageManager.hasCurrentStop());

        storageManager.storeStopList(Collections.singletonList(new Stop(1, "abcd-1234")));

        assertTrue(storageManager.hasCurrentStop());
    }

    @Test
    public void testGetNextStopEmpty() {
        storageManager.storeStopList(new LinkedList<>());

        assertNull(storageManager.getNextStop());
    }

    @Test
    public void testGetNextStopFirst() {
        LinkedList<Stop> stops = new LinkedList<>();

        Stop firstStop = new Stop(1, "xxxx-yyyy");
        Stop secondStop = new Stop(2, "xxxx-yyy1");
        stops.add(firstStop);

        storageManager.storeStopList(stops);
        assertNull(storageManager.getNextStop());

        stops.add(secondStop);
        storageManager.storeStopList(stops);
        assertEquals(storageManager.getNextStop(), secondStop);
    }

    @Test
    public void testGetCurrentStopFirst() {
        LinkedList<Stop> stops = new LinkedList<>();

        Stop stop = new Stop(1, "xxxx-yyy1");
        stops.add(stop);
        stops.add(new Stop(2, "xxxx-yyyy"));

        storageManager.storeStopList(stops);
        assertEquals(storageManager.getCurrentStop(), stop);
    }

    @Test
    public void testHasNextStep() {
        assertFalse(storageManager.hasNextStop());

        LinkedList<Stop> stops = new LinkedList<>();
        stops.add(new Stop(1, "xxxx-yyyy"));
        storageManager.storeStopList(stops);
        assertFalse(storageManager.hasNextStop());

        stops.add(new Stop(2, "xxxx-yyy1"));
        storageManager.storeStopList(stops);
        assertTrue(storageManager.hasNextStop());
    }

    @Test
    public void testRemoveCurrentStopEmpty() {
        LinkedList<Stop> stops = new LinkedList<>();

        storageManager.storeStopList(stops);
        assertNull(storageManager.removeCurrentStop());
    }

    @Test
    public void testRemoveCurrentStopFirst() {
        LinkedList<Stop> stops = new LinkedList<>();

        Stop stop = new Stop(1, "xxxx-yyyy");
        stops.add(stop);
        stops.add(new Stop(2, "xxxx-yyy1"));

        storageManager.storeStopList(stops);
        assertEquals(storageManager.removeCurrentStop(), stop);
    }

    @Test
    public void testGetDeliveryPartOfEachRouteStopEmpty() {
        assertNull(storageManager.getDeliveryPartOfEachRouteStop(new Stop(1, "xxxx-yyy1")));
    }

    @Test
    public void testGetDeliveryPartOfEachRouteStopPickUp() {
        LinkedList<Stop> stops = new LinkedList<>();

        Stop stopPickUp = new Stop(1, "xxxx-yyyy");
        stopPickUp.setTask(RouteStopTask.PICKUP);

        Stop stopDelivery = new Stop(2, "xxxx-yyyy");
        stopDelivery.setTask(RouteStopTask.DELIVER);

        stops.add(stopPickUp);
        stops.add(stopDelivery);

        storageManager.storeStopList(stops);
        assertEquals(storageManager.getDeliveryPartOfEachRouteStop(stopPickUp), stopDelivery);
    }

    @Test
    public void testGetDeliveryPartOfEachRouteStopDeliveryPickUp() {
        LinkedList<Stop> stops = new LinkedList<>();

        Stop stopPickUp = new Stop(1, "xxxx-yyyy");
        stopPickUp.setTask(RouteStopTask.PICKUP);

        Stop stopDelivery = new Stop(2, "xxxx-yyyy");
        stopDelivery.setTask(RouteStopTask.DELIVER);

        stops.add(stopPickUp);
        stops.add(stopDelivery);

        storageManager.storeStopList(stops);
        assertEquals(storageManager.getDeliveryPartOfEachRouteStop(stopDelivery), stopDelivery);
    }

    @Test
    public void testLastRiderLocation() {
        RiderLocation riderLocation = new RiderLocation();
        riderLocation.setAzimuth(1);
        riderLocation.setAccuracyInMeters(1);
        riderLocation.setBatteryLevel(1);
        riderLocation.setDateTime(DateTime.now());
        riderLocation.setGeoCoordinate(new GeoCoordinate((double) 1, (double) 1));

        storageManager.storeRiderLocation(riderLocation);
        assertEquals(storageManager.getRiderLocation(), riderLocation);
    }

    public void testStoreStopListValidData() {
        LinkedList<Stop> stops = new LinkedList<>();

        Stop stopPickUp = new Stop(1, "xxxx-yyyy");
        stopPickUp.setTask(RouteStopTask.PICKUP);

        Stop stopDelivery = new Stop(2, "xxxx-yyyy");
        stopDelivery.setTask(RouteStopTask.DELIVER);

        stops.add(stopPickUp);
        stops.add(stopDelivery);

        storageManager.storeStopList(stops);
        assertEquals(storageManager.getCurrentStop(), stopPickUp);
        assertEquals(storageManager.getStopList(), stops);
    }

    @Test
    public void testStoreStopListNullOrderCodeData() {
        LinkedList<Stop> stops = new LinkedList<>();

        Stop stopPickUp = new Stop(1, null);
        stopPickUp.setTask(RouteStopTask.PICKUP);

        Stop stopDelivery = new Stop(2, null);
        stopDelivery.setTask(RouteStopTask.DELIVER);

        stops.add(stopPickUp);
        stops.add(stopDelivery);

        storageManager.storeStopList(stops);

        assertNull(storageManager.getCurrentStop());
        assertTrue(storageManager.getStopList().isEmpty());
    }

    @Test
    public void testStoreStopListInvalidOrderCodeData() {
        LinkedList<Stop> stops = new LinkedList<>();

        Stop stopPickUp = new Stop(1, "xxxxyyyy");
        stopPickUp.setTask(RouteStopTask.PICKUP);

        Stop stopDelivery = new Stop(2, "xxxxyyyy");
        stopDelivery.setTask(RouteStopTask.DELIVER);

        stops.add(stopPickUp);
        stops.add(stopDelivery);

        storageManager.storeStopList(stops);

        assertNull(storageManager.getCurrentStop());
        assertTrue(storageManager.getStopList().isEmpty());
    }

    @Test
    public void testStoreRider() {
        Rider rider = new Rider();
        storageManager.storeRider(rider);

        assertEquals(rider, storageManager.getRider());
    }

    @Test
    public void testStore() {
        VehicleDetectedActivity vehicleDetectedActivity = new VehicleDetectedActivity(VehicleDetectedActivityType.fromInteger(10), 10);
        storageManager.storeVehicleDetectedActivity(vehicleDetectedActivity);

        assertEquals(vehicleDetectedActivity, storageManager.getVehicleDetectedActivity());
    }
}
