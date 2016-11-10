package com.foodpanda.urbanninja.manager;

import com.foodpanda.urbanninja.BuildConfig;
import com.foodpanda.urbanninja.api.BaseApiCallback;
import com.foodpanda.urbanninja.api.model.CashCollectionIssueList;
import com.foodpanda.urbanninja.api.model.RouteWrapper;
import com.foodpanda.urbanninja.api.model.ScheduleCollectionWrapper;
import com.foodpanda.urbanninja.api.model.ScheduleWrapper;
import com.foodpanda.urbanninja.model.Rider;
import com.foodpanda.urbanninja.model.Stop;
import com.foodpanda.urbanninja.model.TimeWindow;
import com.foodpanda.urbanninja.model.Vehicle;
import com.foodpanda.urbanninja.model.VehicleDeliveryAreaRiderBundle;
import com.foodpanda.urbanninja.model.enums.CollectionIssueReason;
import com.foodpanda.urbanninja.model.enums.RouteStopTask;
import com.foodpanda.urbanninja.model.enums.Status;
import com.foodpanda.urbanninja.ui.activity.MainActivity;
import com.foodpanda.urbanninja.ui.interfaces.NestedFragmentCallback;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import rx.Observable;
import rx.observers.TestSubscriber;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, packageName = "com.foodpanda.urbanninja")
public class ApiExecutorTest {

    private ApiExecutor apiExecutor;

    @Mock
    private MainActivity activity;

    @Mock
    private ApiManager apiManager;

    @Mock
    private StorageManager storageManager;

    @Mock
    MultiPickupManager multiPickupManager;

    @Mock
    CheckPolygonManager checkPolygonManager;

    @Mock
    private NestedFragmentCallback nestedFragmentCallback;
    @Mock
    private LocationSettingCheckManager locationSettingCheckManager;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        when(apiManager.getRiderObservable()).thenReturn(Observable.empty());
        when(apiManager.getCurrentScheduleObservable()).thenReturn(Observable.empty());
        when(apiManager.getRouteObservable(anyInt())).thenReturn(Observable.empty());

        ///https://google.github.io/dagger/testing.html we don't need to inject for tests
        apiExecutor = new ApiExecutor(activity,
            nestedFragmentCallback,
            apiManager,
            storageManager,
            multiPickupManager,
            checkPolygonManager,
            locationSettingCheckManager);

        DateTimeUtils.setCurrentMillisFixed(DateTime.now().getMillis());
    }

    @Test
    public void testIsScheduleFinished() {
        ScheduleWrapper scheduleWrapper = new ScheduleWrapper();
        scheduleWrapper.setTimeWindow(new TimeWindow(DateTime.now().minusSeconds(10), DateTime.now().minusSeconds(12)));

        ScheduleWrapper scheduleWrapperNext = createScheduleWrapper();

        ScheduleCollectionWrapper scheduleWrappers = new ScheduleCollectionWrapper();
        scheduleWrappers.add(scheduleWrapper);
        scheduleWrappers.add(scheduleWrapperNext);

        apiExecutor.setScheduleWrappers(scheduleWrappers);
        apiExecutor.setScheduleWrapper(scheduleWrapper);

        assertTrue(apiExecutor.openRiderScheduleScreen());
        verify(nestedFragmentCallback).openReadyToWork(scheduleWrapperNext);

        assertEquals(apiExecutor.getScheduleWrapper(), scheduleWrapperNext);
    }

    @Test
    public void testIsScheduleNotFinishedClockedIn() {
        ScheduleWrapper scheduleWrapper = createScheduleWrapper();
        scheduleWrapper.setClockedIn(true);

        ScheduleCollectionWrapper scheduleWrappers = new ScheduleCollectionWrapper();
        scheduleWrappers.add(scheduleWrapper);

        apiExecutor.setScheduleWrappers(scheduleWrappers);
        apiExecutor.setScheduleWrapper(scheduleWrapper);

        assertFalse(apiExecutor.openRiderScheduleScreen());
        verify(nestedFragmentCallback, never()).openReadyToWork(any(ScheduleWrapper.class));
    }

    @Test
    public void testIsScheduleNotFinishedNotClockedIn() {
        ScheduleWrapper scheduleWrapper = createScheduleWrapper();
        scheduleWrapper.setClockedIn(false);

        ScheduleCollectionWrapper scheduleWrappers = new ScheduleCollectionWrapper();
        scheduleWrappers.add(scheduleWrapper);

        apiExecutor.setScheduleWrappers(scheduleWrappers);
        apiExecutor.setScheduleWrapper(scheduleWrapper);

        assertTrue(apiExecutor.openRiderScheduleScreen());
        verify(nestedFragmentCallback).openReadyToWork(scheduleWrapper);
    }

    @Test
    public void testIsScheduleNull() {

        apiExecutor.setScheduleWrappers(null);
        apiExecutor.setScheduleWrapper(null);

        assertTrue(apiExecutor.openRiderScheduleScreen());
        verify(nestedFragmentCallback).openReadyToWork(any(ScheduleWrapper.class));
    }

    @Test
    public void testIsScheduleEmpty() {

        apiExecutor.setScheduleWrappers(new ScheduleCollectionWrapper());
        apiExecutor.setScheduleWrapper(null);

        assertTrue(apiExecutor.openRiderScheduleScreen());
        verify(nestedFragmentCallback).openReadyToWork(any(ScheduleWrapper.class));
    }

    @Test
    public void testNotifyActionPerformedDoNothingWhenNoCurrentStop() {
        Status status = Status.COMPLETED;
        apiExecutor.notifyActionPerformed(status);

        verify(storageManager, never()).storeStatus(anyLong(), eq(status));
        verify(apiManager, never()).notifyActionPerformed(anyLong(), eq(status));
    }

    @Test
    public void testNotifyActionPerformed() {
        Stop routeStop = new Stop(1, "xxxx-yyyy");
        routeStop.setTask(RouteStopTask.PICKUP);

        when(storageManager.getCurrentStop()).thenReturn(routeStop);
        when(multiPickupManager.getSamePlaceStops()).thenReturn(Collections.singletonList(routeStop));

        Status status = Status.ON_THE_WAY;
        apiExecutor.notifyActionPerformed(status);

        verify(storageManager).storeStatus(eq(routeStop.getId()), eq(status));
        verify(apiManager).notifyActionPerformed(eq(routeStop.getId()), eq(status));
    }

    @Test
    public void testNotifyActionPerformedWithCompletedRouteStop() {
        ScheduleWrapper scheduleWrapper = createScheduleWrapper();

        ScheduleCollectionWrapper scheduleWrappers = new ScheduleCollectionWrapper();
        scheduleWrappers.add(scheduleWrapper);
        apiExecutor.setScheduleWrapper(scheduleWrapper);
        apiExecutor.setScheduleWrappers(scheduleWrappers);

        Stop routeStop = new Stop(1, "xxxx-yyyy");

        when(storageManager.getCurrentStop()).thenReturn(routeStop);
        when(storageManager.getCurrentStop()).thenReturn(routeStop);

        Status status = Status.COMPLETED;
        assertNotNull(apiExecutor);
        apiExecutor.notifyActionPerformed(status);

        verify(storageManager).storeStatus(eq(routeStop.getId()), eq(status));
        verify(apiManager).notifyActionPerformed(eq(routeStop.getId()), eq(status));

        verify(storageManager).removeCurrentStop();
    }

    @Test
    public void testOpenCurrentFragmentWithRoute() {
        Stop routeStop = new Stop(1, "xxxx-yyyy");

        List<Stop> stopList = new LinkedList<>();
        stopList.add(routeStop);

        when(storageManager.getStopList()).thenReturn(stopList);
        when(storageManager.getCurrentStop()).thenReturn(routeStop);

        apiExecutor.openCurrentFragment();

        verify(nestedFragmentCallback).openRoute(routeStop);
        verify(nestedFragmentCallback, never()).openReadyToWork(any(ScheduleWrapper.class));
        verify(nestedFragmentCallback, never()).openEmptyListFragment();
    }


    @Test
    public void testOpenCurrentFragmentWithoutRouteAndSchedule() {

        when(storageManager.getStopList()).thenReturn(new LinkedList<>());

        apiExecutor.openCurrentFragment();

        verify(nestedFragmentCallback).openReadyToWork(isNull(ScheduleWrapper.class));
        verify(nestedFragmentCallback, never()).openRoute(any(Stop.class));
        verify(nestedFragmentCallback, never()).openEmptyListFragment();
    }

    @Test
    public void testOpenCurrentFragmentWithoutRouteButWithScheduleClockedIn() {

        ScheduleWrapper scheduleWrapper = createScheduleWrapper();
        scheduleWrapper.setClockedIn(true);

        ScheduleCollectionWrapper scheduleWrappers = new ScheduleCollectionWrapper();
        scheduleWrappers.add(scheduleWrapper);
        apiExecutor.setScheduleWrapper(scheduleWrapper);
        apiExecutor.setScheduleWrappers(scheduleWrappers);

        when(storageManager.getStopList()).thenReturn(new LinkedList<>());

        apiExecutor.openCurrentFragment();

        verify(nestedFragmentCallback).openEmptyListFragment();
        verify(nestedFragmentCallback, never()).openReadyToWork(any(ScheduleWrapper.class));
        verify(nestedFragmentCallback, never()).openRoute(any(Stop.class));
    }

    @Test
    public void testOpenCurrentFragmentWithoutRouteButWithScheduleNotClockedIn() {

        ScheduleWrapper scheduleWrapper = createScheduleWrapper();
        scheduleWrapper.setClockedIn(false);

        ScheduleCollectionWrapper scheduleWrappers = new ScheduleCollectionWrapper();
        scheduleWrappers.add(scheduleWrapper);
        apiExecutor.setScheduleWrapper(scheduleWrapper);
        apiExecutor.setScheduleWrappers(scheduleWrappers);

        when(storageManager.getStopList()).thenReturn(new LinkedList<>());

        apiExecutor.openCurrentFragment();

        verify(nestedFragmentCallback).openReadyToWork(scheduleWrapper);
        verify(nestedFragmentCallback, never()).openRoute(any(Stop.class));
        verify(nestedFragmentCallback, never()).openEmptyListFragment();
    }

    private ScheduleWrapper createScheduleWrapper() {
        ScheduleWrapper scheduleWrapper = new ScheduleWrapper();
        scheduleWrapper.setTimeWindow(new TimeWindow(DateTime.now().minusMinutes(10), DateTime.now().plusMinutes(12)));

        return scheduleWrapper;
    }

    @Test
    public void testUpdateRiderInfo() {
        VehicleDeliveryAreaRiderBundle vehicleDeliveryAreaRiderBundle = new VehicleDeliveryAreaRiderBundle();
        apiExecutor.updateRiderInfo(vehicleDeliveryAreaRiderBundle);
        verify(storageManager, never()).storeRider(any(Rider.class));
        verify(activity, never()).setRiderContent();

        Rider rider = new Rider();
        vehicleDeliveryAreaRiderBundle.setRider(rider);
        apiExecutor.updateRiderInfo(vehicleDeliveryAreaRiderBundle);
        verify(storageManager).storeRider(rider);
        verify(activity).setRiderContent();
    }

    @Test
    public void testUpdateRouteStopInfo() {
        RouteWrapper routeWrapper = new RouteWrapper();

        apiExecutor.updateRouteStopInfo(routeWrapper);

        verify(storageManager).storeStopList(routeWrapper.getStops());
    }

    @Test
    public void testGetScheduleObservable() {
        TestSubscriber<ScheduleCollectionWrapper> subscriber = new TestSubscriber<>();

        Observable<ScheduleCollectionWrapper> observable = apiExecutor.getScheduleObservable();

        ScheduleCollectionWrapper scheduleWrappers = new ScheduleCollectionWrapper();
        ScheduleWrapper scheduleWrapper = new ScheduleWrapper();
        scheduleWrappers.add(scheduleWrapper);

        subscriber.onCompleted();
        subscriber.onNext(scheduleWrappers);
        subscriber.getOnNextEvents();
        observable.subscribe(subscriber);

        subscriber.assertCompleted();
        subscriber.assertNoErrors();
        subscriber.assertValue(scheduleWrappers);
        subscriber.assertReceivedOnNext(Collections.singletonList(scheduleWrappers));
    }

    @Test
    public void testGetRouteStopObservable() {
        TestSubscriber<RouteWrapper> subscriber = new TestSubscriber<>();

        VehicleDeliveryAreaRiderBundle vehicleDeliveryAreaRiderBundle = new VehicleDeliveryAreaRiderBundle();
        vehicleDeliveryAreaRiderBundle.setVehicle(new Vehicle());
        apiExecutor.setVehicleDeliveryAreaRiderBundle(vehicleDeliveryAreaRiderBundle);

        Observable<RouteWrapper> observable = apiExecutor.getRouteStopObservable();
        RouteWrapper routeWrapper = new RouteWrapper();

        subscriber.onCompleted();
        subscriber.onNext(routeWrapper);
        subscriber.getOnNextEvents();
        observable.subscribe(subscriber);


        subscriber.assertCompleted();
        subscriber.assertNoErrors();
        subscriber.assertValue(routeWrapper);
        subscriber.assertReceivedOnNext(Collections.singletonList(routeWrapper));
    }

    @Test
    public void testNullVehicleRouteStopObservable() {
        assertEquals(Observable.empty(), apiExecutor.getRouteStopObservable());

        VehicleDeliveryAreaRiderBundle vehicleDeliveryAreaRiderBundle = new VehicleDeliveryAreaRiderBundle();
        vehicleDeliveryAreaRiderBundle.setVehicle(new Vehicle());
        apiExecutor.setVehicleDeliveryAreaRiderBundle(vehicleDeliveryAreaRiderBundle);

        assertNotEquals(Observable.empty(), apiExecutor.getRouteStopObservable());
    }

    @Test
    public void testNullVehicleUpdateRoute() {
        ApiExecutor apiExecutor = Mockito.spy(this.apiExecutor);
        apiExecutor.updateRoute();

        verify(apiExecutor).getAllData();
    }

    @Test
    public void testNotNullVehicleUpdateRoute() {
        ApiExecutor apiExecutor = Mockito.spy(this.apiExecutor);

        VehicleDeliveryAreaRiderBundle vehicleDeliveryAreaRiderBundle = new VehicleDeliveryAreaRiderBundle();
        vehicleDeliveryAreaRiderBundle.setVehicle(new Vehicle());
        apiExecutor.setVehicleDeliveryAreaRiderBundle(vehicleDeliveryAreaRiderBundle);
        apiExecutor.updateRoute();
        verify(apiExecutor, never()).getAllData();
    }

    @Test
    public void testNullVehicleUpdateScheduleRoute() {
        ApiExecutor apiExecutor = Mockito.spy(this.apiExecutor);

        apiExecutor.updateScheduleAndRouteStop();
        verify(apiExecutor).getAllData();
    }

    @Test
    public void testNotNullVehicleUpdateScheduleRoute() {
        ApiExecutor apiExecutor = Mockito.spy(this.apiExecutor);

        VehicleDeliveryAreaRiderBundle vehicleDeliveryAreaRiderBundle = new VehicleDeliveryAreaRiderBundle();
        vehicleDeliveryAreaRiderBundle.setVehicle(new Vehicle());
        apiExecutor.setVehicleDeliveryAreaRiderBundle(vehicleDeliveryAreaRiderBundle);
        apiExecutor.updateScheduleAndRouteStop();

        verify(apiExecutor, never()).getAllData();
    }


    @Test
    public void testReportCollectIssue() {
        Stop routeStop = new Stop(1, "xxxx-yyyy");

        when(storageManager.getCurrentStop()).thenReturn(routeStop);

        apiExecutor.reportCollectionIssue(1, CollectionIssueReason.WRONG_ITEMS);

        verify(apiManager).reportCollectionIssue(
            eq(routeStop.getId()),
            anyDouble(),
            eq(CollectionIssueReason.WRONG_ITEMS),
            Matchers.<BaseApiCallback<CashCollectionIssueList>>any()
        );
        verify(activity).showProgress();
    }

    @Test
    public void testReportCollectIssueNullCurrentStop() {
        apiExecutor.reportCollectionIssue(1, CollectionIssueReason.WRONG_ITEMS);

        verify(apiManager, never()).reportCollectionIssue(
            anyLong(),
            anyDouble(),
            eq(CollectionIssueReason.WRONG_ITEMS),
            Matchers.<BaseApiCallback<CashCollectionIssueList>>any()
        );
        verify(activity, never()).showProgress();
    }
}
