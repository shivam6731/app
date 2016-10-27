package com.foodpanda.urbanninja.ui.util;

import android.app.Application;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.foodpanda.urbanninja.BuildConfig;
import com.foodpanda.urbanninja.R;
import com.foodpanda.urbanninja.manager.StorageManager;
import com.foodpanda.urbanninja.model.Country;
import com.foodpanda.urbanninja.model.RouteStopActivity;
import com.foodpanda.urbanninja.model.Stop;
import com.foodpanda.urbanninja.model.enums.RouteStopActivityType;
import com.foodpanda.urbanninja.model.enums.RouteStopTask;
import com.foodpanda.urbanninja.ui.activity.MainActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.robolectric.Robolectric.buildActivity;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, packageName = "com.foodpanda.urbanninja")
public class OrderTypeAndPaymentHelperTest {
    private OrderTypeAndPaymentHelper orderTypeAndPaymentHelper;
    private RelativeLayout relativeLayout;
    private MainActivity mainActivity;
    private TextView txtOrderType;
    private TextView txtOrderPaymenMethod;

    @Mock
    private StorageManager storageManager;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Application app = RuntimeEnvironment.application;
        app.onCreate();
        mainActivity = buildActivity(MainActivity.class).get();

        relativeLayout = (RelativeLayout) LayoutInflater.from(mainActivity).inflate(R.layout.order_type_payment_method_layout, null);
        txtOrderType = (TextView) relativeLayout.findViewById(R.id.txt_type);
        txtOrderPaymenMethod = (TextView) relativeLayout.findViewById(R.id.txt_payment);
    }

    @Test
    public void testSetTypeDelivery() {
        Stop stop = new Stop();
        stop.setTask(RouteStopTask.DELIVER);
        stop.setActivities(Collections.emptyList());
        when(storageManager.getDeliveryPartOfEachRouteStop(stop)).thenReturn(stop);
        when(storageManager.getCurrentStop()).thenReturn(stop);

        orderTypeAndPaymentHelper = new OrderTypeAndPaymentHelper(mainActivity, storageManager);
        orderTypeAndPaymentHelper.setType(relativeLayout);

        assertEquals(txtOrderType.getText().toString(), "Delivery");
    }

    @Test
    public void testSetTypePickUp() {
        Stop stop = new Stop();
        stop.setTask(RouteStopTask.PICKUP);
        stop.setActivities(Collections.emptyList());

        when(storageManager.getDeliveryPartOfEachRouteStop(stop)).thenReturn(stop);
        when(storageManager.getCurrentStop()).thenReturn(stop);

        orderTypeAndPaymentHelper = new OrderTypeAndPaymentHelper(mainActivity, storageManager);
        orderTypeAndPaymentHelper.setType(relativeLayout);

        assertEquals(txtOrderType.getText().toString(), "Pick up");
    }

    @Test
    public void testSetPaymentMethodCollectMoney() {
        Stop stop = new Stop();
        stop.setTask(RouteStopTask.PICKUP);

        List<RouteStopActivity> routeStopActivities = new ArrayList<>();
        routeStopActivities.add(new RouteStopActivity(RouteStopActivityType.COLLECT, "40"));
        stop.setActivities(routeStopActivities);

        when(storageManager.getDeliveryPartOfEachRouteStop(stop)).thenReturn(stop);
        when(storageManager.getCountry()).thenReturn(getCountryForCode("hk"));
        when(storageManager.getCurrentStop()).thenReturn(stop);

        orderTypeAndPaymentHelper = new OrderTypeAndPaymentHelper(mainActivity, storageManager);
        orderTypeAndPaymentHelper.setType(relativeLayout);

        assertEquals(txtOrderPaymenMethod.getText().toString(), "HKD40.00");
    }

    @Test
    public void testSetPaymentMethodCollectZeroMoney() {
        Stop stop = new Stop();
        stop.setTask(RouteStopTask.PICKUP);

        List<RouteStopActivity> routeStopActivities = new ArrayList<>();
        routeStopActivities.add(new RouteStopActivity(RouteStopActivityType.COLLECT, "0"));
        stop.setActivities(routeStopActivities);

        when(storageManager.getDeliveryPartOfEachRouteStop(stop)).thenReturn(stop);
        when(storageManager.getCountry()).thenReturn(getCountryForCode("hk"));
        when(storageManager.getCurrentStop()).thenReturn(stop);

        orderTypeAndPaymentHelper = new OrderTypeAndPaymentHelper(mainActivity, storageManager);
        orderTypeAndPaymentHelper.setType(relativeLayout);

        assertEquals(txtOrderPaymenMethod.getText().toString(), "Already Paid");
    }

    @Test
    public void testSetPaymentMethodCollectNoActivity() {
        Stop stop = new Stop();
        stop.setTask(RouteStopTask.PICKUP);

        stop.setActivities(Collections.emptyList());

        when(storageManager.getDeliveryPartOfEachRouteStop(stop)).thenReturn(stop);
        when(storageManager.getCountry()).thenReturn(getCountryForCode("hk"));
        when(storageManager.getCurrentStop()).thenReturn(stop);

        orderTypeAndPaymentHelper = new OrderTypeAndPaymentHelper(mainActivity, storageManager);
        orderTypeAndPaymentHelper.setType(relativeLayout);

        assertEquals(txtOrderPaymenMethod.getText().toString(), "Already Paid");
    }

    private Country getCountryForCode(String code) {
        Country country = new Country();
        country.setCode(code);

        return country;
    }

}
