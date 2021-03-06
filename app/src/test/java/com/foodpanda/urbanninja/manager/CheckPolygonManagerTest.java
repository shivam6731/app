package com.foodpanda.urbanninja.manager;

import android.app.Application;
import android.location.Location;

import com.foodpanda.urbanninja.BuildConfig;
import com.foodpanda.urbanninja.model.DeliveryZone;
import com.foodpanda.urbanninja.model.GeoCoordinate;
import com.foodpanda.urbanninja.model.StartingPoint;
import com.foodpanda.urbanninja.model.enums.PolygonStatusType;
import com.foodpanda.urbanninja.ui.activity.BaseActivity;
import com.foodpanda.urbanninja.ui.activity.MainActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.robolectric.Robolectric.buildActivity;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, packageName = "com.foodpanda.urbanninja")
public class CheckPolygonManagerTest {
    @Mock
    private CheckPolygonManager checkPolygonManager;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Application app = RuntimeEnvironment.application;
        app.onCreate();
        BaseActivity baseActivity = buildActivity(MainActivity.class).get();

        checkPolygonManager = new CheckPolygonManager(baseActivity);
    }

    @Test
    public void testCheckIfLocationInPolygonOrNearStartingPointNoData() throws Exception {
        DeliveryZone deliveryZone = new DeliveryZone();
        List<GeoCoordinate> geoCoordinates = new ArrayList<>();

        deliveryZone.setPolygon(geoCoordinates);
        assertEquals(PolygonStatusType.NO_DATA, checkPolygonManager.checkIfLocationInPolygonOrNearStartingPoint(deliveryZone));
    }

    @Test
    public void testCheckIfLocationInPolygonOrNearStartingPointInsideDeliveryZone() throws Exception {

        Location location = new Location("");
        location.setLatitude(33.5004686);
        location.setLongitude(-111.9027061);

        CheckPolygonManager checkPolygonManager = Mockito.spy(this.checkPolygonManager);
        when(checkPolygonManager.getLastRiderLocation()).thenReturn(location);

        DeliveryZone deliveryZone = new DeliveryZone();
        List<GeoCoordinate> geoCoordinates = new ArrayList<>();
        geoCoordinates.add(new GeoCoordinate(33.5362475, -111.9267386));
        geoCoordinates.add(new GeoCoordinate(33.5104882, -111.9627875));
        geoCoordinates.add(new GeoCoordinate(33.5004686, -111.9027061));

        deliveryZone.setPolygon(geoCoordinates);
        assertEquals(PolygonStatusType.INSIDE, checkPolygonManager.checkIfLocationInPolygonOrNearStartingPoint(deliveryZone));
    }

    @Test
    public void testCheckIfLocationInPolygonOrNearStartingPointOutSideDeliveryZone() throws Exception {

        Location location = new Location("");
        location.setLatitude(33.5362476);
        location.setLongitude(-111.9267386);

        CheckPolygonManager checkPolygonManager = Mockito.spy(this.checkPolygonManager);
        when(checkPolygonManager.getLastRiderLocation()).thenReturn(location);

        DeliveryZone deliveryZone = new DeliveryZone();
        List<GeoCoordinate> geoCoordinates = new ArrayList<>();
        geoCoordinates.add(new GeoCoordinate(33.5362475, -111.9267386));
        geoCoordinates.add(new GeoCoordinate(33.5104882, -111.9627875));
        geoCoordinates.add(new GeoCoordinate(33.5004686, -111.9027061));

        deliveryZone.setPolygon(geoCoordinates);
        assertEquals(PolygonStatusType.OUTSIDE, checkPolygonManager.checkIfLocationInPolygonOrNearStartingPoint(deliveryZone));
    }

    @Test
    public void testCheckIfLocationInPolygonOrNearStartingPointNotNearStartingPoint() throws Exception {
        Location location = new Location("");
        location.setLatitude(63.66504166152633);
        location.setLongitude(-113.72000813484192);

        CheckPolygonManager checkPolygonManager = Mockito.spy(this.checkPolygonManager);
        when(checkPolygonManager.getLastRiderLocation()).thenReturn(location);

        DeliveryZone deliveryZone = new DeliveryZone();
        StartingPoint startingPoint = new StartingPoint();

        startingPoint.setGeoCoordinate(new GeoCoordinate(63.66322823186246, -113.715341091156));
        deliveryZone.setStartingPoint(startingPoint);

        //Distance is 306 meters
        assertEquals(PolygonStatusType.OUTSIDE, checkPolygonManager.checkIfLocationInPolygonOrNearStartingPoint(deliveryZone));
    }

    @Test
    public void testCheckIfLocationInPolygonOrNearStartingPointNearStartingPoint() throws Exception {
        Location location = new Location("");
        location.setLatitude(63.6660458994591);
        location.setLongitude(-113.7204909324646);

        CheckPolygonManager checkPolygonManager = Mockito.spy(this.checkPolygonManager);
        when(checkPolygonManager.getLastRiderLocation()).thenReturn(location);

        DeliveryZone deliveryZone = new DeliveryZone();
        StartingPoint startingPoint = new StartingPoint();

        startingPoint.setGeoCoordinate(new GeoCoordinate(63.66398027188404, -113.71671438217163));
        deliveryZone.setStartingPoint(startingPoint);

        //Distance is 296 meters
        assertEquals(PolygonStatusType.INSIDE, checkPolygonManager.checkIfLocationInPolygonOrNearStartingPoint(deliveryZone));
    }

}
