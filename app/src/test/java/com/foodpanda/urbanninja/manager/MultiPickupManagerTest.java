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

import java.util.Collections;
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
     * and sequence of route stops are  P1->P2->D1->D2
     */
    @Test
    public void testGetSamePlacePickUpAndCheckIsEmpty() {
        Stop currentStopPickUp = new Stop(1, "q1fw-1234");
        currentStopPickUp.setTask(RouteStopTask.PICKUP);

        Stop samePlaceStopPickUp = new Stop(2, "q1fw-4321");
        samePlaceStopPickUp.setTask(RouteStopTask.PICKUP);

        Stop currentStopDeliveryPickUp = new Stop(1, "q1fw-1234");
        currentStopDeliveryPickUp.setTask(RouteStopTask.DELIVER);

        Stop samePlaceStopDelivery = new Stop(2, "q1fw-4321");
        samePlaceStopDelivery.setTask(RouteStopTask.DELIVER);

        List<Stop> stopList = new LinkedList<>();
        List<Stop> samePlaceStopList = new LinkedList<>();

        samePlaceStopList.add(currentStopPickUp);
        samePlaceStopList.add(samePlaceStopPickUp);

        stopList.addAll(samePlaceStopList);
        stopList.add(currentStopDeliveryPickUp);
        stopList.add(samePlaceStopDelivery);

        when(storageManager.getStopList()).thenReturn(stopList);
        when(storageManager.getCurrentStop()).thenReturn(currentStopPickUp);

        assertEquals(samePlaceStopList, multiPickupManager.getSamePlaceStops());

        assertTrue(multiPickupManager.isNotEmptySamePlacePickUpStops(currentStopPickUp));
    }

    /**
     * Test good conditions when both orders are from the same place
     * however sequence of route stops are  P1->D1->P2->D2
     */
    @Test
    public void testGetSamePlacePickUpAndCheckIsEmptyNotInSequence() {
        Stop currentStopPickUp = new Stop(1, "q1fw-1234");
        currentStopPickUp.setTask(RouteStopTask.PICKUP);

        Stop currentStopDelivery = new Stop(1, "q1fw-1234");
        currentStopDelivery.setTask(RouteStopTask.DELIVER);

        Stop samePlaceStopPickUp = new Stop(2, "q1fw-4321");
        samePlaceStopPickUp.setTask(RouteStopTask.PICKUP);

        Stop samePlaceStopDelivery = new Stop(2, "q1fw-4321");
        samePlaceStopDelivery.setTask(RouteStopTask.DELIVER);

        List<Stop> stopList = new LinkedList<>();
        stopList.add(currentStopPickUp);
        stopList.add(currentStopDelivery);
        stopList.add(samePlaceStopPickUp);
        stopList.add(samePlaceStopDelivery);

        when(storageManager.getStopList()).thenReturn(stopList);
        when(storageManager.getCurrentStop()).thenReturn(currentStopPickUp);

        assertEquals(Collections.singletonList(currentStopPickUp), multiPickupManager.getSamePlaceStops());
        assertEquals(1, multiPickupManager.getSamePlaceStops().size());

        assertFalse(multiPickupManager.isNotEmptySamePlacePickUpStops(currentStopPickUp));
    }

    /**
     * Test good conditions when 3 orders are from the same place
     * however sequence of route stops are  P1->P2->P3->D1->D2->D3
     */
    @Test
    public void testGetSamePlacePickUpAndCheckIsEmptyForThreeStopsThreeInSequence() {
        //Pick-up tasks

        Stop currentStopPickUp = new Stop(1, "q1fw-1234");
        currentStopPickUp.setTask(RouteStopTask.PICKUP);

        Stop samePlaceStopPickUp = new Stop(2, "q1fw-4321");
        samePlaceStopPickUp.setTask(RouteStopTask.PICKUP);

        Stop thirdStopPickUp = new Stop(1, "q1fw-2345");
        thirdStopPickUp.setTask(RouteStopTask.PICKUP);

        //Delivery tasks
        Stop currentStopDelivery = new Stop(1, "q1fw-1234");
        currentStopDelivery.setTask(RouteStopTask.DELIVER);

        Stop samePlaceStopDelivery = new Stop(2, "q1fw-4321");
        samePlaceStopDelivery.setTask(RouteStopTask.DELIVER);

        Stop thirdStopDelivery = new Stop(2, "q1fw-2345");
        thirdStopDelivery.setTask(RouteStopTask.DELIVER);

        List<Stop> stopList = new LinkedList<>();
        List<Stop> samePlaceStopList = new LinkedList<>();

        samePlaceStopList.add(currentStopPickUp);
        samePlaceStopList.add(samePlaceStopPickUp);
        samePlaceStopList.add(thirdStopPickUp);

        stopList.addAll(samePlaceStopList);
        stopList.add(currentStopDelivery);
        stopList.add(samePlaceStopDelivery);
        stopList.add(thirdStopDelivery);

        when(storageManager.getStopList()).thenReturn(stopList);
        when(storageManager.getCurrentStop()).thenReturn(currentStopPickUp);

        assertEquals(samePlaceStopList, multiPickupManager.getSamePlaceStops());
        assertEquals(3, multiPickupManager.getSamePlaceStops().size());

        assertTrue(multiPickupManager.isNotEmptySamePlacePickUpStops(currentStopPickUp));
    }

    /**
     * Test good conditions when 3 orders are from the same place
     * however sequence of route stops are  P1->P2->D1->D2->P3->D3
     */
    @Test
    public void testGetSamePlacePickUpAndCheckIsEmptyForThreeStopsTwoInSequence() {
        //Pick-up tasks
        Stop currentStopPickUp = new Stop(1, "q1fw-1234");
        currentStopPickUp.setTask(RouteStopTask.PICKUP);

        Stop samePlaceStopPickUp = new Stop(2, "q1fw-4321");
        samePlaceStopPickUp.setTask(RouteStopTask.PICKUP);
        //Delivery tasks
        Stop currentStopDelivery = new Stop(1, "q1fw-1234");
        currentStopDelivery.setTask(RouteStopTask.DELIVER);

        Stop samePlaceStopDelivery = new Stop(2, "q1fw-4321");
        samePlaceStopDelivery.setTask(RouteStopTask.DELIVER);
        //Third order
        Stop thirdStopDeliveryPickUp = new Stop(1, "q1fw-2345");
        thirdStopDeliveryPickUp.setTask(RouteStopTask.PICKUP);

        Stop thirdPlaceStopDelivery = new Stop(2, "q1fw-2345");
        thirdPlaceStopDelivery.setTask(RouteStopTask.DELIVER);

        List<Stop> stopList = new LinkedList<>();
        List<Stop> samePlaceStopList = new LinkedList<>();

        samePlaceStopList.add(currentStopPickUp);
        samePlaceStopList.add(samePlaceStopPickUp);

        stopList.addAll(samePlaceStopList);
        stopList.add(currentStopDelivery);
        stopList.add(samePlaceStopDelivery);
        stopList.add(thirdStopDeliveryPickUp);
        stopList.add(thirdPlaceStopDelivery);

        when(storageManager.getStopList()).thenReturn(stopList);
        when(storageManager.getCurrentStop()).thenReturn(currentStopPickUp);

        assertEquals(samePlaceStopList, multiPickupManager.getSamePlaceStops());
        assertEquals(2, multiPickupManager.getSamePlaceStops().size());

        assertTrue(multiPickupManager.isNotEmptySamePlacePickUpStops(currentStopPickUp));
    }

    /**
     * Test bad conditions when both orders are from the different places
     * however pick-up tasks are in a sequence
     */
    @Test
    public void testGetDifferentPlacePickUpAndCheckIsEmpty() {
        Stop currentStopPickUp = new Stop(1, "q1fw-1234");
        currentStopPickUp.setTask(RouteStopTask.PICKUP);

        Stop differentPlaceStopPickUp = new Stop(1, "s1fw-1234");
        differentPlaceStopPickUp.setTask(RouteStopTask.PICKUP);

        Stop currentStopDelivery = new Stop(1, "q1fw-1234");
        currentStopDelivery.setTask(RouteStopTask.DELIVER);

        Stop differentPlaceStopDelivery = new Stop(1, "q1fw-1234");
        differentPlaceStopDelivery.setTask(RouteStopTask.DELIVER);


        List<Stop> stopList = new LinkedList<>();
        stopList.add(currentStopPickUp);
        stopList.add(differentPlaceStopPickUp);
        stopList.add(currentStopDelivery);
        stopList.add(differentPlaceStopDelivery);

        when(storageManager.getStopList()).thenReturn(stopList);
        when(storageManager.getCurrentStop()).thenReturn(currentStopPickUp);

        assertEquals(Collections.singletonList(currentStopPickUp), multiPickupManager.getSamePlaceStops());
        assertEquals(1, multiPickupManager.getSamePlaceStops().size());

        assertFalse(multiPickupManager.isNotEmptySamePlacePickUpStops(currentStopPickUp));
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

        assertEquals(Collections.singletonList(currentStop), multiPickupManager.getSamePlaceStops());
        assertEquals(1, multiPickupManager.getSamePlaceStops().size());
        assertFalse(multiPickupManager.isNotEmptySamePlacePickUpStops(currentStop));
    }

    /**
     * Test for one pick-up item in a route stop plan
     */
    @Test
    public void testGetSamePlacePickUpSingleOrder() {
        Stop currentStop = new Stop(1, "q1fw-q7dy");
        currentStop.setTask(RouteStopTask.PICKUP);

        List<Stop> stopList = new LinkedList<>();
        stopList.add(currentStop);

        when(storageManager.getStopList()).thenReturn(stopList);
        when(storageManager.getCurrentStop()).thenReturn(currentStop);

        assertEquals(Collections.singletonList(currentStop), multiPickupManager.getSamePlaceStops());
        assertEquals(1, multiPickupManager.getSamePlaceStops().size());
        assertFalse(multiPickupManager.isNotEmptySamePlacePickUpStops(currentStop));
    }

    /**
     * Test for one delivery item in a route stop plan
     */
    @Test
    public void testGetSamePlaceDeliverySingleOrder() {
        Stop currentStop = new Stop(1, "q1fw-q7dy");
        currentStop.setTask(RouteStopTask.DELIVER);

        List<Stop> stopList = new LinkedList<>();
        stopList.add(currentStop);

        when(storageManager.getStopList()).thenReturn(stopList);
        when(storageManager.getCurrentStop()).thenReturn(currentStop);

        assertEquals(currentStop, multiPickupManager.getSamePlaceStops().get(0));
        assertEquals(1, multiPickupManager.getSamePlaceStops().size());
        assertFalse(multiPickupManager.isNotEmptySamePlacePickUpStops(currentStop));
    }

    /**
     * Test for one items in a route stop plan
     */
    @Test
    public void testGetMultiPickUpDetailsSting() {
        Stop currentStop = new Stop(1, "q1fw-q7dy");
        currentStop.setTask(RouteStopTask.PICKUP);

        Stop samePlaceStopPickUp = new Stop(2, "q1fw-4321");
        samePlaceStopPickUp.setTask(RouteStopTask.PICKUP);

        List<Stop> stopList = new LinkedList<>();
        stopList.add(currentStop);
        stopList.add(samePlaceStopPickUp);

        when(storageManager.getStopList()).thenReturn(stopList);
        when(storageManager.getCurrentStop()).thenReturn(currentStop);
        Application app = RuntimeEnvironment.application;

        assertEquals(
            "Orders " +
                currentStop.getOrderCode() +
                ", " +
                samePlaceStopPickUp.getOrderCode() +
                " will need to be picked up from the same restaurant.\n",
            multiPickupManager.getMultiPickUpDetailsSting(app, currentStop).toString());
    }
}
