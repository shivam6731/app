package com.foodpanda.urbanninja.manager;

import com.foodpanda.urbanninja.BuildConfig;
import com.foodpanda.urbanninja.api.model.HockeyAppVersionList;
import com.foodpanda.urbanninja.api.model.RouteWrapper;
import com.foodpanda.urbanninja.api.model.ScheduleCollectionWrapper;
import com.foodpanda.urbanninja.api.model.ScheduleWrapper;
import com.foodpanda.urbanninja.model.DeliveryZone;
import com.foodpanda.urbanninja.model.Rider;
import com.foodpanda.urbanninja.model.Stop;
import com.foodpanda.urbanninja.model.TimeWindow;
import com.foodpanda.urbanninja.model.Vehicle;
import com.foodpanda.urbanninja.model.VehicleDeliveryAreaRiderBundle;
import com.foodpanda.urbanninja.model.enums.CollectionIssueReason;
import com.foodpanda.urbanninja.model.enums.DialogType;
import com.foodpanda.urbanninja.model.enums.PolygonStatusType;
import com.foodpanda.urbanninja.model.enums.RouteStopTask;
import com.foodpanda.urbanninja.model.enums.Status;
import com.foodpanda.urbanninja.model.hockeyapp.AppVersion;
import com.foodpanda.urbanninja.ui.activity.MainActivity;
import com.foodpanda.urbanninja.ui.interfaces.NestedFragmentCallback;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Collections;
import java.util.LinkedList;

import rx.Observable;
import rx.observers.TestSubscriber;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
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
    private MultiPickupManager multiPickupManager;

    @Mock
    private CheckPolygonManager checkPolygonManager;

    @Mock
    private NestedFragmentCallback nestedFragmentCallback;
    @Mock
    private LocationSettingCheckManager locationSettingCheckManager;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        when(apiManager.getRiderObservable()).thenReturn(Observable.empty());
        when(apiManager.getAppVersionsObservable(anyString())).thenReturn(Observable.empty());
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
        verify(apiManager, never()).notifyActionPerformed(anyLong(), eq(status), eq(activity));
    }

    @Test
    public void testNotifyActionPerformed() {
        Stop routeStop = new Stop(1, "xxxx-yyyy");
        routeStop.setTask(RouteStopTask.PICKUP);

        when(storageManager.getCurrentStop()).thenReturn(routeStop);
        when(multiPickupManager.getSamePlaceStops()).thenReturn(Collections.singletonList(routeStop));

        Status status = Status.ON_THE_WAY;
        apiExecutor.notifyActionPerformed(status);

        verify(storageManager).storeStatus(routeStop.getId(), status);
        verify(apiManager).notifyActionPerformed(routeStop.getId(), status, activity);
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
        when(storageManager.hasCurrentStop()).thenReturn(true);

        Status status = Status.COMPLETED;
        apiExecutor.notifyActionPerformed(status);

        verify(storageManager).storeStatus(routeStop.getId(), status);
        verify(apiManager).notifyActionPerformed(routeStop.getId(), status, activity);

        verify(storageManager).removeCurrentStop();
    }

    @Test
    public void testOpenCurrentFragmentWithRoute() {
        Stop routeStop = new Stop(1, "xxxx-yyyy");

        when(storageManager.getStopList()).thenReturn(Collections.singletonList(routeStop));
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
        ApiExecutor apiExecutor = spy(this.apiExecutor);
        apiExecutor.updateRoute();

        verify(apiExecutor).getAllData();
    }

    @Test
    public void testNotNullVehicleUpdateRoute() {
        ApiExecutor apiExecutor = spy(this.apiExecutor);

        VehicleDeliveryAreaRiderBundle vehicleDeliveryAreaRiderBundle = new VehicleDeliveryAreaRiderBundle();
        vehicleDeliveryAreaRiderBundle.setVehicle(new Vehicle());
        apiExecutor.setVehicleDeliveryAreaRiderBundle(vehicleDeliveryAreaRiderBundle);
        apiExecutor.updateRoute();
        verify(apiExecutor, never()).getAllData();
    }

    @Test
    public void testNullVehicleUpdateScheduleRoute() {
        ApiExecutor apiExecutor = spy(this.apiExecutor);

        apiExecutor.updateScheduleAndRouteStop();
        verify(apiExecutor).getAllData();
    }

    @Test
    public void testNotNullVehicleUpdateScheduleRoute() {
        ApiExecutor apiExecutor = spy(this.apiExecutor);

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
        when(storageManager.hasCurrentStop()).thenReturn(true);

        apiExecutor.reportCollectionIssue(1, CollectionIssueReason.WRONG_ITEMS);

        verify(apiManager).reportCollectionIssue(
            eq(routeStop.getId()),
            anyDouble(),
            eq(CollectionIssueReason.WRONG_ITEMS),
            Matchers.any()
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
            Matchers.any()
        );
        verify(activity, never()).showProgress();
    }

    @Test
    public void testCheckAppVersionObservableNoData() {
        TestSubscriber<VehicleDeliveryAreaRiderBundle> subscriber = new TestSubscriber<>();

        Observable<VehicleDeliveryAreaRiderBundle> observable = apiExecutor.checkAppVersion(new HockeyAppVersionList());

        VehicleDeliveryAreaRiderBundle vehicleDeliveryAreaRiderBundle = new VehicleDeliveryAreaRiderBundle();
        subscriber.onCompleted();
        subscriber.onNext(vehicleDeliveryAreaRiderBundle);
        subscriber.getOnNextEvents();
        observable.subscribe(subscriber);

        subscriber.assertCompleted();
        subscriber.assertNoErrors();
        subscriber.assertValue(vehicleDeliveryAreaRiderBundle);
        subscriber.assertReceivedOnNext(Collections.singletonList(vehicleDeliveryAreaRiderBundle));

        verify(nestedFragmentCallback, never()).openInformationDialog(
            anyString(),
            anyString(),
            anyString(),
            Matchers.eq(DialogType.NOT_UP_TO_DATE_APP_VERSION),
            anyString());
    }

    @Test
    public void testCheckAppVersionObservableNewVersion() {
        TestSubscriber<VehicleDeliveryAreaRiderBundle> subscriber = new TestSubscriber<>();

        HockeyAppVersionList hockeyAppVersionList = new HockeyAppVersionList();
        AppVersion appVersion = new AppVersion(BuildConfig.VERSION_CODE, "", "");
        hockeyAppVersionList.setAppVersions(Collections.singletonList(appVersion));

        Observable<VehicleDeliveryAreaRiderBundle> observable = apiExecutor.checkAppVersion(new HockeyAppVersionList());

        VehicleDeliveryAreaRiderBundle vehicleDeliveryAreaRiderBundle = new VehicleDeliveryAreaRiderBundle();
        subscriber.onCompleted();
        subscriber.onNext(vehicleDeliveryAreaRiderBundle);
        subscriber.getOnNextEvents();
        observable.subscribe(subscriber);

        subscriber.assertCompleted();
        subscriber.assertNoErrors();
        subscriber.assertValue(vehicleDeliveryAreaRiderBundle);
        subscriber.assertReceivedOnNext(Collections.singletonList(vehicleDeliveryAreaRiderBundle));

        verify(nestedFragmentCallback, never()).openInformationDialog(
            anyString(),
            anyString(),
            anyString(),
            Matchers.eq(DialogType.NOT_UP_TO_DATE_APP_VERSION),
            anyString());
    }

    @Test
    public void testCheckAppVersionObservableOldVersion() {
        MainActivity activity = Robolectric.buildActivity(MainActivity.class).get();

        apiExecutor = new ApiExecutor(activity,
            nestedFragmentCallback,
            apiManager,
            storageManager,
            multiPickupManager,
            checkPolygonManager,
            locationSettingCheckManager);

        TestSubscriber<VehicleDeliveryAreaRiderBundle> subscriber = new TestSubscriber<>();

        HockeyAppVersionList hockeyAppVersionList = new HockeyAppVersionList();
        AppVersion appVersion = new AppVersion(BuildConfig.VERSION_CODE + 1, BuildConfig.VERSION_NAME, "Web");
        hockeyAppVersionList.setAppVersions(Collections.singletonList(appVersion));

        Observable<VehicleDeliveryAreaRiderBundle> observable = apiExecutor.checkAppVersion(hockeyAppVersionList);

        subscriber.onCompleted();
        observable.subscribe(subscriber);

        subscriber.assertCompleted();
        subscriber.assertNoErrors();
        subscriber.assertNoValues();
        subscriber.assertReceivedOnNext(Collections.emptyList());

        verify(nestedFragmentCallback).openInformationDialog(
            "Your app is not up to date",
            String.format("You have app version %1$s installed \n" +
                    "which is an outdated version of Urban Ninja.\n\n" +
                    "    To proceed with your work, please download the latest %2$s\n" +
                    "by clicking in the link below.",
                BuildConfig.VERSION_NAME, BuildConfig.VERSION_NAME),
            "OK, let's download it",
            DialogType.NOT_UP_TO_DATE_APP_VERSION,
            "Web");
    }

    @Test
    public void testTryToClockInInsideDeliveryZoneNoSchedule() {
        ApiExecutor apiExecutor = spy(this.apiExecutor);

        apiExecutor.tryToClockInInsideDeliveryZone();
        verify(apiExecutor, never()).clockIn();
    }

    @Test
    public void testTryToClockInInsideDeliveryZoneInside() {
        ApiExecutor apiExecutor = spy(this.apiExecutor);

        ScheduleWrapper scheduleWrapper = new ScheduleWrapper();
        DeliveryZone deliveryZone = new DeliveryZone();
        scheduleWrapper.setDeliveryZone(deliveryZone);

        apiExecutor.setScheduleWrapper(scheduleWrapper);

        when(checkPolygonManager.checkIfLocationInPolygonOrNearStartingPoint(deliveryZone)).thenReturn(PolygonStatusType.INSIDE);
        apiExecutor.tryToClockInInsideDeliveryZone();

        verify(apiExecutor).clockIn();
    }

    @Test
    public void testTryToClockInInsideDeliveryZoneOutside() {
        MainActivity activity = Robolectric.buildActivity(MainActivity.class).get();

        apiExecutor = new ApiExecutor(activity,
            nestedFragmentCallback,
            apiManager,
            storageManager,
            multiPickupManager,
            checkPolygonManager,
            locationSettingCheckManager);
        ApiExecutor apiExecutor = spy(this.apiExecutor);

        ScheduleWrapper scheduleWrapper = new ScheduleWrapper();
        DeliveryZone deliveryZone = new DeliveryZone();
        scheduleWrapper.setDeliveryZone(deliveryZone);

        apiExecutor.setScheduleWrapper(scheduleWrapper);

        when(checkPolygonManager.checkIfLocationInPolygonOrNearStartingPoint(deliveryZone)).thenReturn(PolygonStatusType.OUTSIDE);
        apiExecutor.tryToClockInInsideDeliveryZone();

        verify(apiExecutor, never()).clockIn();
    }

    @Test
    public void testTryToClockInInsideDeliveryZoneNoData() {
        MainActivity activity = Robolectric.buildActivity(MainActivity.class).get();

        apiExecutor = new ApiExecutor(activity,
            nestedFragmentCallback,
            apiManager,
            storageManager,
            multiPickupManager,
            checkPolygonManager,
            locationSettingCheckManager);
        ApiExecutor apiExecutor = spy(this.apiExecutor);

        ScheduleWrapper scheduleWrapper = new ScheduleWrapper();
        DeliveryZone deliveryZone = new DeliveryZone();
        scheduleWrapper.setDeliveryZone(deliveryZone);

        apiExecutor.setScheduleWrapper(scheduleWrapper);

        when(checkPolygonManager.checkIfLocationInPolygonOrNearStartingPoint(deliveryZone)).thenReturn(PolygonStatusType.NO_DATA);
        apiExecutor.tryToClockInInsideDeliveryZone();

        verify(apiExecutor, never()).clockIn();
    }

}
