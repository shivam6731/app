package com.foodpanda.urbanninja.manager;

import android.app.Application;

import com.foodpanda.urbanninja.BuildConfig;
import com.foodpanda.urbanninja.model.Stop;
import com.foodpanda.urbanninja.model.Token;
import com.foodpanda.urbanninja.model.enums.RouteStopTask;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.LinkedList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, packageName = "com.foodpanda.urbanninja")
public class StorageManagerTest {
    private StorageManager storageManager;

    @Before
    public void setUp() {
        Application app = RuntimeEnvironment.application;
        app.onCreate();

        storageManager = new StorageManager();
        storageManager.init(app);
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
        storageManager.storeStopList(new LinkedList<Stop>());

        assertNull(storageManager.getCurrentStop());
    }

    @Test
    public void testGetNextStopEmpty() {
        storageManager.storeStopList(new LinkedList<Stop>());

        assertNull(storageManager.getNextStop());
    }

    @Test
    public void testGetNextStopFirst() {
        LinkedList<Stop> stops = new LinkedList<>();

        Stop firstStop = new Stop();
        Stop secondStop = new Stop();
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

        Stop stop = new Stop();
        stops.add(stop);
        stops.add(new Stop());

        storageManager.storeStopList(stops);
        assertEquals(storageManager.getCurrentStop(), stop);
    }

    @Test
    public void testHasNextStep() {
        assertFalse(storageManager.hasNextStop());

        LinkedList<Stop> stops = new LinkedList<>();
        stops.add(new Stop());
        storageManager.storeStopList(stops);
        assertFalse(storageManager.hasNextStop());

        stops.add(new Stop());
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

        Stop stop = new Stop();
        stops.add(stop);
        stops.add(new Stop());

        storageManager.storeStopList(stops);
        assertEquals(storageManager.removeCurrentStop(), stop);
    }

    @Test
    public void testGetDeliveryPartOfEachRouteStopEmpty() {
        assertNull(storageManager.getDeliveryPartOfEachRouteStop(new Stop()));
    }

    @Test
    public void testGetDeliveryPartOfEachRouteStopPickUp() {
        LinkedList<Stop> stops = new LinkedList<>();

        Stop stopPickUp = new Stop();
        stopPickUp.setTask(RouteStopTask.PICKUP);
        stopPickUp.setOrderCode("testCode");

        Stop stopDelivery = new Stop();
        stopDelivery.setTask(RouteStopTask.DELIVER);
        stopDelivery.setOrderCode("testCode");

        stops.add(stopPickUp);
        stops.add(stopDelivery);

        storageManager.storeStopList(stops);
        assertEquals(storageManager.getDeliveryPartOfEachRouteStop(stopPickUp), stopDelivery);
    }

    @Test
    public void testGetDeliveryPartOfEachRouteStopDeliveryPickUp() {
        LinkedList<Stop> stops = new LinkedList<>();

        Stop stopPickUp = new Stop();
        stopPickUp.setTask(RouteStopTask.PICKUP);
        stopPickUp.setOrderCode("testCode");

        Stop stopDelivery = new Stop();
        stopDelivery.setTask(RouteStopTask.DELIVER);
        stopDelivery.setOrderCode("testCode");

        stops.add(stopPickUp);
        stops.add(stopDelivery);

        storageManager.storeStopList(stops);
        assertEquals(storageManager.getDeliveryPartOfEachRouteStop(stopDelivery), stopDelivery);
    }
}
