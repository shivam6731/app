package com.foodpanda.urbanninja.ui.util;

import android.app.Application;
import android.view.View;
import android.widget.Button;

import com.foodpanda.urbanninja.BuildConfig;
import com.foodpanda.urbanninja.R;
import com.foodpanda.urbanninja.model.RouteStopActivity;
import com.foodpanda.urbanninja.model.Stop;
import com.foodpanda.urbanninja.model.enums.RouteStopTaskStatus;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, packageName = "com.foodpanda.urbanninja")
public class ActionLayoutHelperTest {
    private ActionLayoutHelper actionLayoutHelper;
    private Button btnAction;
    private View layoutAction;

    @Before
    public void setUp() {
        Application app = RuntimeEnvironment.application;
        app.onCreate();

        actionLayoutHelper = new ActionLayoutHelper(app);

        btnAction = new Button(app);
        layoutAction = new View(app);

        actionLayoutHelper.setBtnAction(btnAction);
        actionLayoutHelper.setLayoutAction(layoutAction);
    }

    @Test
    public void testNotNull() {
        assertNotNull(btnAction);
        assertNotNull(layoutAction);
    }

    @Test
    public void testHide() {
        actionLayoutHelper.hideActionButton();
        assertTrue(layoutAction.getVisibility() == View.GONE);
    }

    @Test
    public void testVisible() {
        actionLayoutHelper.setVisibility(true);
        assertTrue(layoutAction.getVisibility() == View.VISIBLE);

        actionLayoutHelper.setVisibility(false);
        assertTrue(layoutAction.getVisibility() == View.GONE);
    }

    @Test
    public void testReadyToWork() {
        actionLayoutHelper.setReadyToWorkActionButton();
        assertEquals(btnAction.getText().toString(), "I'm ready to work");
        assertTrue(layoutAction.getVisibility() == View.VISIBLE);
    }

    @Test
    public void testSetRouteStopActionListButton() {
        Stop stop = new Stop();
        List<RouteStopActivity> routeStopActivityList = new LinkedList<>();

        stop.setId(1L);
        stop.setTask(RouteStopTaskStatus.DELIVER);

        stop.setActivities(routeStopActivityList);

        actionLayoutHelper.setRouteStopActionListButton(stop);
        assertEquals(btnAction.getText().toString(), "I delivered");
        assertTrue(layoutAction.getVisibility() == View.VISIBLE);

        stop.setTask(RouteStopTaskStatus.PICKUP);
        routeStopActivityList.add(new RouteStopActivity());

        actionLayoutHelper.setRouteStopActionListButton(stop);
        assertEquals(btnAction.getText().toString(), "I picked up");
        assertTrue(layoutAction.getVisibility() == View.GONE);
    }

    @Test
    public void testSetDrivingHereStatusActionButton() {
        actionLayoutHelper.setDrivingHereStatusActionButton();
        assertEquals(btnAction.getText().toString(), "I'm driving there");
        assertTrue(layoutAction.getVisibility() == View.VISIBLE);
    }

    @Test
    public void testSetViewedStatusActionButton() {
        Stop stop = new Stop();

        stop.setId(1L);
        stop.setTask(RouteStopTaskStatus.DELIVER);

        actionLayoutHelper.setViewedStatusActionButton(stop);
        assertEquals(btnAction.getText().toString(), "I'm at delivery");
        assertTrue(layoutAction.getVisibility() == View.GONE);

        stop.setTask(RouteStopTaskStatus.PICKUP);
        actionLayoutHelper.setViewedStatusActionButton(stop);

        assertEquals(btnAction.getText().toString(), "I'm at pickup");
        assertTrue(layoutAction.getVisibility() == View.GONE);
    }


    @Test
    public void testUpdateActionButton() {
        int textRes = R.string.action_at_picked_up;
        actionLayoutHelper.updateActionButton(true, textRes);
        assertTrue(layoutAction.getVisibility() == View.VISIBLE);
        assertEquals(btnAction.getText().toString(), "I picked up");

        actionLayoutHelper.updateActionButton(false, 0);
        assertTrue(layoutAction.getVisibility() == View.GONE);
        assertEquals(btnAction.getText().toString(), "");
    }

}
