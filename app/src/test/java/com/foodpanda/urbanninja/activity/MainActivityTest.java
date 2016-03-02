package com.foodpanda.urbanninja.activity;


import android.app.Application;

import com.foodpanda.urbanninja.BuildConfig;
import com.foodpanda.urbanninja.ui.activity.MainActivity;
import com.google.android.gms.common.ConnectionResult;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
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

    @Test
    public void testNotNull() {
        MainActivity mainActivity = buildActivity(MainActivity.class).get();
        assertNotNull(mainActivity);
    }

    @Test
    public void testOnCreateNotNull() {
        Application app = RuntimeEnvironment.application;
        app.onCreate();

        ShadowGooglePlayServicesUtil.setIsGooglePlayServicesAvailable(ConnectionResult.SUCCESS);

        MainActivity mainActivity = Robolectric.buildActivity(MainActivity.class).get();

        assertNotNull(mainActivity);
        assertEquals(app, mainActivity.getApplication());
    }

    //TODO
    // This is problem with google play services and it would be fixed soon
    // https://github.com/robolectric/robolectric/issues/2215
    // As soon as it will work I will add tests for the UI part such as enable visibility for action button


}
