package com.foodpanda.urbanninja.ui.activity;


import android.app.Application;

import com.foodpanda.urbanninja.BuildConfig;
import com.google.android.gms.common.ConnectionResult;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.gms.ShadowGooglePlayServicesUtil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.robolectric.Robolectric.buildActivity;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, packageName = "com.foodpanda.urbanninja")
public class MainActivityTest {
    private MainActivity mainActivity;
    private Application app;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        ShadowGooglePlayServicesUtil.setIsGooglePlayServicesAvailable(ConnectionResult.SUCCESS);

        app = RuntimeEnvironment.application;
        app.onCreate();

        mainActivity = buildActivity(MainActivity.class).get();
    }

    @Test
    public void testNotNull() {
        assertNotNull(mainActivity);
    }

    @Test
    public void testOnCreateNotNull() {
        assertNotNull(mainActivity);
        assertEquals(app, mainActivity.getApplication());
    }

    //TODO
    // This is problem with google play services and it would be fixed soon
    // https://github.com/robolectric/robolectric/issues/2215
    // As soon as it will work I will add tests for the UI part such as enable visibility for action button


}
