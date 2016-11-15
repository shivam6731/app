package com.foodpanda.urbanninja.manager;

import android.app.Application;

import com.foodpanda.urbanninja.BuildConfig;
import com.foodpanda.urbanninja.model.Stop;
import com.foodpanda.urbanninja.model.enums.RouteStopTask;
import com.foodpanda.urbanninja.ui.interfaces.NestedFragmentCallback;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, packageName = "com.foodpanda.urbanninja")
public class MultiPickupManagerTest {
    @Mock
    private StorageManager storageManager;

    private MultiPickupManager multiPickupManager;

    @Mock
    private NestedFragmentCallback nestedFragmentCallback;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Application app = RuntimeEnvironment.application;
        app.onCreate();

        multiPickupManager = new MultiPickupManager(storageManager);
    }

    /**
     * Test good conditions when both orders are from the same place
     * and both are PICKUP task
     */
    @Test
    public void testGetSamePlacePickUpAndCheckIsEmpty() {
        Stop currentStop = new Stop(1, "q1fw-1234");
        currentStop.setTask(RouteStopTask.PICKUP);

        Stop samePlaceStop = new Stop(2, "q1fw-4321");
        samePlaceStop.setTask(RouteStopTask.PICKUP);

        List<Stop> stopList = new LinkedList<>();
        stopList.add(currentStop);
        stopList.add(samePlaceStop);

        when(storageManager.getStopList()).thenReturn(stopList);
        when(storageManager.getCurrentStop()).thenReturn(currentStop);

        assertEquals(stopList, multiPickupManager.getSamePlaceStops());

        assertTrue(multiPickupManager.isNotEmptySamePlacePickUpStops(currentStop));
    }

    /**
     * Test bad conditions when both orders are from the different places
     * and both are PICKUP task
     */
    @Test
    public void testGetDifferentPlacePickUpAndCheckIsEmpty() {
        Stop currentStop = new Stop(1, "q1fw-1234");
        currentStop.setTask(RouteStopTask.PICKUP);

        Stop samePlaceStop = new Stop(1, "s1fw-1234");
        samePlaceStop.setTask(RouteStopTask.PICKUP);

        List<Stop> stopList = new LinkedList<>();
        stopList.add(currentStop);
        stopList.add(samePlaceStop);

        when(storageManager.getStopList()).thenReturn(stopList);
        when(storageManager.getCurrentStop()).thenReturn(currentStop);

        assertEquals(currentStop, multiPickupManager.getSamePlaceStops().get(0));
        assertEquals(1, multiPickupManager.getSamePlaceStops().size());
        assertFalse(multiPickupManager.isNotEmptySamePlacePickUpStops(currentStop));
    }

    /**
     * Test good conditions when both orders are from the same places
     * and both are DELIVER task
     */
    @Test
    public void testGetSamePlaceDeliveryAndCheckIsEmpty() {
        Stop currentStop = new Stop(1, "q1fw-q7dy");
        currentStop.setTask(RouteStopTask.DELIVER);

        Stop samePlaceStop = new Stop(2, "q1fw-q7dy");
        samePlaceStop.setTask(RouteStopTask.DELIVER);

        List<Stop> stopList = new LinkedList<>();
        stopList.add(currentStop);
        stopList.add(samePlaceStop);

        when(storageManager.getStopList()).thenReturn(stopList);
        when(storageManager.getCurrentStop()).thenReturn(currentStop);

        assertEquals(multiPickupManager.getSamePlaceStops().get(0), currentStop);
        assertEquals(multiPickupManager.getSamePlaceStops().size(), 1);
        assertFalse(multiPickupManager.isNotEmptySamePlacePickUpStops(currentStop));
    }

    /**
     * Test for one items in a route stop plan
     */
    @Test
    public void testGetSamePlacePickUpSingleOrder() {
        Stop currentStop = new Stop(1, "q1fw-q7dy");
        currentStop.setTask(RouteStopTask.PICKUP);

        List<Stop> stopList = new LinkedList<>();
        stopList.add(currentStop);

        when(storageManager.getStopList()).thenReturn(stopList);
        when(storageManager.getCurrentStop()).thenReturn(currentStop);

        storageManager.storeStopList(stopList);

        assertEquals(multiPickupManager.getSamePlaceStops().get(0), currentStop);
        assertEquals(multiPickupManager.getSamePlaceStops().size(), 1);
        assertFalse(multiPickupManager.isNotEmptySamePlacePickUpStops(currentStop));
    }

    /**
     * Test for one items in a route stop plan
     */
    @Test
    public void testGetMultiPickUpDetailsSting() {
        Stop currentStop = new Stop(1, "q1fw-q7dy");
        currentStop.setTask(RouteStopTask.PICKUP);

        when(storageManager.getCurrentStop()).thenReturn(currentStop);
        Application app = RuntimeEnvironment.application;

        assertEquals(
            "Orders " + currentStop.getOrderCode() + " will need to be picked up from the same restaurant.\n",
            multiPickupManager.getMultiPickUpDetailsSting(app, currentStop).toString());
    }
}
