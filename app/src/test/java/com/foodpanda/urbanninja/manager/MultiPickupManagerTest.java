package com.foodpanda.urbanninja.manager;

import com.foodpanda.urbanninja.BuildConfig;
import com.foodpanda.urbanninja.model.GeoCoordinate;
import com.foodpanda.urbanninja.model.Stop;
import com.foodpanda.urbanninja.model.enums.RouteStopTask;
import com.foodpanda.urbanninja.ui.interfaces.NestedFragmentCallback;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
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

        multiPickupManager = new MultiPickupManager(storageManager);
    }

    @Test
    public void getSamePlacePickUpAndCheckIsEmpty() {
        Stop currentStop = new Stop();
        currentStop.setGps(new GeoCoordinate(52.5000000, 13.5000000));
        currentStop.setTask(RouteStopTask.PICKUP);

        Stop samePlaceStop = new Stop();
        samePlaceStop.setGps(new GeoCoordinate(52.5000200, 13.5000200));
        samePlaceStop.setTask(RouteStopTask.PICKUP);

        List<Stop> stopList = new LinkedList<>();
        stopList.add(currentStop);
        stopList.add(samePlaceStop);

        when(storageManager.getStopList()).thenReturn(stopList);
        when(storageManager.getCurrentStop()).thenReturn(currentStop);

        assertEquals(stopList, multiPickupManager.getSamePlaceStops());
        assertTrue(multiPickupManager.isNotEmptySamePlacePickUpStops(currentStop));
    }

    @Test
    public void getDifferentPlacePickUpAndCheckIsEmpty() {
        Stop currentStop = new Stop();
        currentStop.setGps(new GeoCoordinate(52.5000000, 13.5000000));
        currentStop.setTask(RouteStopTask.PICKUP);

        Stop samePlaceStop = new Stop();
        samePlaceStop.setGps(new GeoCoordinate(52.510000, 13.510000));
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

    @Test
    public void getSamePlaceDeliveryAndCheckIsEmpty() {
        Stop currentStop = new Stop();
        currentStop.setGps(new GeoCoordinate(52.5000000, 13.5000000));
        currentStop.setTask(RouteStopTask.DELIVER);

        Stop samePlaceStop = new Stop();
        samePlaceStop.setGps(new GeoCoordinate(52.5000000, 13.510000));
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

}
