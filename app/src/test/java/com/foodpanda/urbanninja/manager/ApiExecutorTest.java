package com.foodpanda.urbanninja.manager;

import android.app.Application;

import com.foodpanda.urbanninja.BuildConfig;
import com.foodpanda.urbanninja.api.model.ScheduleCollectionWrapper;
import com.foodpanda.urbanninja.api.model.ScheduleWrapper;
import com.foodpanda.urbanninja.model.Stop;
import com.foodpanda.urbanninja.model.TimeWindow;
import com.foodpanda.urbanninja.model.enums.Action;
import com.foodpanda.urbanninja.ui.activity.MainActivity;
import com.foodpanda.urbanninja.ui.fragments.OrdersNestedFragment;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, packageName = "com.foodpanda.urbanninja")
public class ApiExecutorTest {

    private ApiExecutor apiExecutor;

    @Mock
    private ApiManager apiManager;

    @Mock
    private StorageManager storageManager;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Application app = RuntimeEnvironment.application;
        app.onCreate();

        MainActivity activity = mock(MainActivity.class);
        OrdersNestedFragment ordersNestedFragment = mock(OrdersNestedFragment.class);
        apiExecutor = new ApiExecutor(activity, ordersNestedFragment, apiManager, storageManager);

        DateTimeUtils.setCurrentMillisFixed(DateTime.now().getMillis());
    }

    @Test
    public void testIsScheduleFinished() {
        ScheduleWrapper scheduleWrapper = new ScheduleWrapper();
        scheduleWrapper.setTimeWindow(new TimeWindow(DateTime.now().minusSeconds(10), DateTime.now().minusSeconds(12)));

        ScheduleWrapper scheduleWrapperNext = new ScheduleWrapper();
        scheduleWrapperNext.setTimeWindow(new TimeWindow(DateTime.now().minusSeconds(5), DateTime.now().minusSeconds(6)));

        ScheduleCollectionWrapper scheduleWrappers = new ScheduleCollectionWrapper();
        scheduleWrappers.add(scheduleWrapper);
        scheduleWrappers.add(scheduleWrapperNext);

        apiExecutor.setScheduleWrappers(scheduleWrappers);
        apiExecutor.setScheduleWrapper(scheduleWrapper);

        assertTrue(apiExecutor.openNextScheduleIfCurrentIsFinished());

        assertEquals(apiExecutor.getScheduleWrapper(), scheduleWrapperNext);
    }

    @Test
    public void testIsScheduleNotFinished() {
        ScheduleWrapper scheduleWrapper = new ScheduleWrapper();
        scheduleWrapper.setTimeWindow(new TimeWindow(DateTime.now().minusSeconds(10), DateTime.now().plusMinutes(12)));

        ScheduleCollectionWrapper scheduleWrappers = new ScheduleCollectionWrapper();
        scheduleWrappers.add(scheduleWrapper);

        apiExecutor.setScheduleWrappers(scheduleWrappers);
        apiExecutor.setScheduleWrapper(scheduleWrapper);

        assertFalse(apiExecutor.openNextScheduleIfCurrentIsFinished());
    }

    @Test
    public void testNotifyActionPerformedDoNothingWhenNoCurrentStop() {
        Action action = Action.COMPLETED;
        apiExecutor.notifyActionPerformed(action);

        verify(storageManager, never()).storeAction(anyLong(), eq(action));
        verify(apiManager, never()).notifyActionPerformed(anyLong(), eq(action));
    }

    @Test
    public void testNotifyActionPerformed() {
        Stop routeStop = new Stop();
        routeStop.setId(1L);

        when(storageManager.getCurrentStop()).thenReturn(routeStop);

        Action action = Action.ON_THE_WAY;
        apiExecutor.notifyActionPerformed(action);

        verify(storageManager).storeAction(eq(routeStop.getId()), eq(action));
        verify(apiManager).notifyActionPerformed(eq(routeStop.getId()), eq(action));
    }

    @Test
    public void testNotifyActionPerformedWithCompletedRouteStop() {
        ScheduleWrapper scheduleWrapper = new ScheduleWrapper();
        scheduleWrapper.setTimeWindow(new TimeWindow(DateTime.now().minusMinutes(10), DateTime.now().plusMinutes(12)));

        ScheduleCollectionWrapper scheduleWrappers = new ScheduleCollectionWrapper();
        scheduleWrappers.add(scheduleWrapper);
        apiExecutor.setScheduleWrapper(scheduleWrapper);
        apiExecutor.setScheduleWrappers(scheduleWrappers);

        Stop routeStop = new Stop();
        routeStop.setId(1L);

        when(storageManager.getCurrentStop()).thenReturn(routeStop);

        Action action = Action.COMPLETED;
        assertNotNull(apiExecutor);
        apiExecutor.notifyActionPerformed(action);

        verify(storageManager).storeAction(eq(routeStop.getId()), eq(action));
        verify(apiManager).notifyActionPerformed(eq(routeStop.getId()), eq(action));

        verify(storageManager).removeCurrentStop();
    }

}
